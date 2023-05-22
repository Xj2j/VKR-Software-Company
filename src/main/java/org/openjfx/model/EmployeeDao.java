package org.openjfx.model;

import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.openjfx.model.dto.Department;
import org.openjfx.model.dto.Employee;
import org.openjfx.model.dto.Position;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeDao extends AbstractJDBCDao<Employee> {

    @Override
    public String getSelectQuery() {
        return "SELECT employees.id, employees.fio, employees.telephone_number, employees.email, employees.address, employees.department_id, "
                + "employees.position_id, employees.isadmin, employees.login, departments.title as dep_title, positions.title as pos_title "
                + "FROM employees JOIN departments ON departments.id = employees.department_id "
                + "JOIN positions ON positions.id = employees.position_id";
    }

    @Override
    public String getSearchQuery() {
        return null;
    }

    @Override
    public String getCreateQuery() {
        return "INSERT INTO employees (fio, telephone_number, email, address, department_id, position_id, isadmin, login, pass) \n" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
    }

    @Override
    public String getUpdateQuery() {
        return "UPDATE employees \n" +
                "SET fio = ?, telephone_number  = ?, email = ?, address = ?, department_id = ?, position_id = ?, isadmin = ?, login = ? \n" +
                "WHERE id = ?;";
    }

    /**public String verifyQuery() {
        return getSelectQuery() + " WHERE login = ? AND pass = ?";
    }*/

    public String verifyQuery() {
        return "SELECT employees.id, employees.fio, employees.telephone_number, employees.email, employees.address, employees.department_id, "
                + "employees.position_id, employees.isadmin, employees.login, departments.title as dep_title, positions.title as pos_title "
                + "FROM employees JOIN departments ON departments.id = employees.department_id "
                + "JOIN positions ON positions.id = employees.position_id WHERE employees.login = ? AND employees.pass = ?";
    }

    @Override
    public String getDeleteQuery() {
        return "DELETE FROM employees WHERE id= ?;";
    }

    public EmployeeDao(DaoFactoryInterface<Connection> parentFactory, Connection connection) {
        super(parentFactory, connection);
    }

    @Override
    protected List<Employee> parseResultSet(ResultSet rs) throws PersistException {

        List<Employee> result = new ArrayList<>();
        Map<Integer, Department> deps = new HashMap<>();
        Map<Integer, Position> poss = new HashMap<>();

        try {
            while (rs.next()) {

                Department dep = deps.get(rs.getInt("department_id"));
                Position pos = poss.get(rs.getInt("position_id"));

                if (dep == null) {
                    dep = parseDepartment(rs);
                    deps.put(rs.getInt("department_id"), dep);
                }

                if (pos == null) {
                    pos = parsePosition(rs);
                    poss.put(rs.getInt("position_id"), pos);
                }

                Employee obj = parseEmployee(rs, dep, pos);
                result.add(obj);
            }
            return result;
        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

    @Override
    protected Employee parseResultSetForOne(ResultSet rs) throws PersistException {
        try {
            if (rs.next()) {
                Department dep = parseDepartment(rs);
                Position pos = parsePosition(rs);
                Employee obj = parseEmployee(rs, dep, pos);
                return obj;
            }
            return null;
        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

    private Employee parseEmployee(ResultSet rs, Department dep, Position pos) throws SQLException {
        Employee emp = new Employee();
        emp.setId(rs.getInt("id"));
        emp.setName(rs.getString("fio"));
        emp.setTelephoneNumber(rs.getString("telephone_number"));
        emp.setEmail(rs.getString("email"));
        emp.setAddress(rs.getString("address"));
        emp.setDepartment(dep);
        emp.setPosition(pos);
        emp.setIsAdmin(rs.getBoolean("isadmin"));
        emp.setLogin(rs.getString("login"));
        return emp;
    }

    private Department parseDepartment(ResultSet rs) throws SQLException {
        Department dep = new Department();
        dep.setId(rs.getInt("department_id"));
        dep.setTitle(rs.getString("dep_title"));
        return dep;
    }

    private Position parsePosition(ResultSet rs) throws SQLException {
        Position pos = new Position();
        pos.setId(rs.getInt("position_id"));
        pos.setTitle(rs.getString("pos_title"));
        return pos;
    }


    @Override
    protected void prepareStatementForSearch(PreparedStatement statement, Employee object) throws PersistException {

    }

    @Override
    protected void prepareStatementForUpdate(PreparedStatement statement, Employee object) throws PersistException {
        try {
            int departmentId = (object.getDepartment() == null || object.getDepartment().getId() == 0) ? -1
                    : object.getDepartment().getId();
            int positionId = (object.getPosition() == null || object.getPosition().getId() == 0) ? -1
                    : object.getPosition().getId();
            statement.setString(1, object.getName());
            statement.setString(2, object.getTelephoneNumber());
            statement.setString(3, object.getEmail());
            statement.setString(4, object.getAddress());
            statement.setInt(5, departmentId);
            statement.setInt(6, positionId);
            statement.setBoolean(7, object.getIsAdmin());
            statement.setString(8, object.getLogin());
            statement.setInt(9, object.getId());
        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

    @Override
    protected void prepareStatementForInsert(PreparedStatement statement, Employee object) throws PersistException {
        try {
            int departmentId = (object.getDepartment() == null || object.getDepartment().idProperty() == null) ? -1
                    : object.getDepartment().getId();
            int positionId = (object.getPosition() == null || object.getPosition().idProperty() == null) ? -1
                    : object.getPosition().getId();
            statement.setString(1, object.getName());
            statement.setString(2, object.getTelephoneNumber());
            statement.setString(3, object.getEmail());
            statement.setString(4, object.getAddress());
            statement.setInt(5, departmentId);
            statement.setInt(6, positionId);
            statement.setBoolean(7, object.getIsAdmin());
            statement.setString(8, object.getLogin());
            statement.setString(9, object.getPassword());
        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

    /**public static boolean isVerifyLogin(Connection connection, String login, String password) throws PersistException {
        String sql = verifyQuery();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, login);
            statement.setString(2, password);
            ResultSet rs =  statement.executeQuery();
            while (rs.next()) {
                return true ? (rs.getInt(1) == 1) : false;
            }
        } catch (Exception e) {
            throw new PersistException(e);
        }
        return false;
    }*/

    public Employee getUser(String login, String password) throws PersistException {
        String sql = verifyQuery();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, login);
            statement.setString(2, password);
            ResultSet rs =  statement.executeQuery();
            //while (rs.next()) {
                Employee loggedUser = parseResultSetForOne(rs);
                return loggedUser;
            //}
        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

}
