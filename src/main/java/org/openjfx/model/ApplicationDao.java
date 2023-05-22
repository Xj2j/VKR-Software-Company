package org.openjfx.model;

import org.openjfx.model.dto.*;
import org.openjfx.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class ApplicationDao extends AbstractJDBCDao<Application> {

    @Override
    public String getSelectQuery() {
        return "SELECT applications.id, applications.title, applications.costumer_id, applications.date_of_application, "
                + "applications.date_of_completion, applications.price, applications.description, applications.status_id, "
                + "costumers.title as costumer_title, statuses.title as status_title "
                + "FROM applications JOIN costumers ON costumers.id = applications.costumer_id "
                + "JOIN statuses ON statuses.id = applications.status_id";
    }

    @Override
    public String getSearchQuery() {
        return null;
    }

    @Override
    public String getCreateQuery() {
        return "INSERT INTO applications (title, costumer_id, date_of_application, date_of_completion, price, description, status_id) \n" +
                "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id;";
    }

    @Override
    public String getUpdateQuery() {
        return "UPDATE applications \n" +
                "SET title = ?, costumer_id = ?, date_of_application  = ?, date_of_completion = ?, price = ?, description = ?, status_id = ? \n" +
                "WHERE id = ?;";
    }

    @Override
    public String getDeleteQuery() {
        return "DELETE FROM applications WHERE id= ?;";
    }

    public ApplicationDao(DaoFactoryInterface<Connection> parentFactory, Connection connection) {
        super(parentFactory, connection);
    }

    @Override
    protected List<Application> parseResultSet(ResultSet rs) throws PersistException {

        List<Application> result = new ArrayList<>();
        Map<Integer, Costumer> costumers = new HashMap<>();
        Map<Integer, Status> stats = new HashMap<>();

        try {
            while (rs.next()) {

                Costumer costumer = costumers.get(rs.getInt("costumer_id"));
                Status stat = stats.get(rs.getInt("status_id"));

                if (costumer == null) {
                    costumer = parseCostumer(rs);
                    costumers.put(rs.getInt("costumer_id"), costumer);
                }

                if (stat == null) {
                    stat = parseStatus(rs);
                    stats.put(rs.getInt("status_id"), stat);
                }

                Application obj = parseApplication(rs, costumer, stat);
                result.add(obj);
            }
            return result;
        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

    @Override
    protected Application parseResultSetForOne(ResultSet rs) throws PersistException {
        try {
            if (rs.next()) {
                Costumer costumer = parseCostumer(rs);
                Status status = parseStatus(rs);
                Application obj = parseApplication(rs, costumer, status);
                return obj;
            }
            return null;
        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

    private Application parseApplication(ResultSet rs, Costumer costumer, Status stat) throws SQLException {
        Application application = new Application();
        application.setId(rs.getInt("id"));
        application.setTitle(rs.getString("title"));
        application.setCostumer(costumer);
        application.setDateOfApplication(rs.getObject(4, LocalDate.class));
        application.setDateOfCompletion(rs.getObject(5, LocalDate.class));
        application.setPrice(rs.getInt("price"));
        application.setDescription(rs.getString("description"));
        application.setStatus(stat);
        return application;
    }

    private Costumer parseCostumer(ResultSet rs) throws SQLException {
        Costumer costumer = new Costumer();
        costumer.setId(rs.getInt("costumer_id"));
        costumer.setName(rs.getString("costumer_title"));
        return costumer;
    }

    private Status parseStatus(ResultSet rs) throws SQLException {
        Status stat = new Status();
        stat.setId(rs.getInt("status_id"));
        stat.setTitle(rs.getString("status_title"));
        return stat;
    }



    @Override
    protected void prepareStatementForSearch(PreparedStatement statement, Application object) throws PersistException {

    }

    @Override
    protected void prepareStatementForUpdate(PreparedStatement statement, Application object) throws PersistException {
        try {
            Date dateApp = Util.convertToDate(object.getDateOfApplication());
            Date dateCom = Util.convertToDate(object.getDateOfCompletion());
            int costumerId = (object.getCostumer() == null || object.getCostumer().idProperty() == null) ? -1
                    : object.getCostumer().getId();
            int statusId = (object.getStatus() == null || object.getStatus().idProperty() == null) ? -1
                    : object.getStatus().getId();
            statement.setString(1, object.getTitle());
            statement.setInt(2, costumerId);
            statement.setDate(3, convert(dateApp));
            statement.setDate(4, convert(dateCom));
            statement.setInt(5, object.getPrice());
            statement.setString(6, object.getDescription());
            statement.setInt(7, statusId);
            statement.setInt(8, object.getId());
        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

    @Override
    protected void prepareStatementForInsert(PreparedStatement statement, Application object) throws PersistException {
        try {
            Date dateApp = Util.convertToDate(object.getDateOfApplication());
            Date dateCom = Util.convertToDate(object.getDateOfCompletion());
            int costumerId = (object.getCostumer() == null || object.getCostumer().idProperty() == null) ? -1
                    : object.getCostumer().getId();
            int statusId = (object.getStatus() == null || object.getStatus().idProperty() == null) ? -1
                    : object.getStatus().getId();
            statement.setString(1, object.getTitle());
            statement.setInt(2, costumerId);
            statement.setDate(3, convert(dateApp));
            statement.setDate(4, convert(dateCom));
            statement.setInt(5, object.getPrice());
            statement.setString(6, object.getDescription());
            statement.setInt(7, statusId);
        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

    protected java.sql.Date convert(java.util.Date date) {
        if (date == null) {
            return null;
        }
        return new java.sql.Date(date.getTime());
    }
}
