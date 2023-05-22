package org.openjfx.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.openjfx.model.dto.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDao extends AbstractJDBCDao<Department> {

    @Override
    public String getSelectQuery() {
        return "SELECT id, title FROM departments ";
    }

    @Override
    public String getSearchQuery() {
        return null;
    }

    @Override
    public String getCreateQuery() {
        return null;
    }

    @Override
    public String getUpdateQuery() {
        return null;
    }

    @Override
    public String getDeleteQuery() {
        return null;
    }

    public DepartmentDao(DaoFactoryInterface<Connection> parentFactory, Connection connection) {
        super(parentFactory, connection);
    }

    @Override
    protected List<Department> parseResultSet(ResultSet rs) throws PersistException {
        List<Department> result = new ArrayList<>();
        try {
            while (rs.next()) {
                Department department = new Department();
                department.setId(rs.getInt("id"));
                department.setTitle(rs.getString("title"));
                result.add(department);
            }
        } catch (Exception e) {
            throw new PersistException(e);
        }
        return result;
    }

    @Override
    protected Object parseResultSetForOne(ResultSet rs) throws PersistException {
        return null;
    }

    @Override
    protected void prepareStatementForSearch(PreparedStatement statement, Department object) throws PersistException {

    }

    @Override
    protected void prepareStatementForUpdate(PreparedStatement statement, Department object) throws PersistException {
        try {

        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

    @Override
    protected void prepareStatementForInsert(PreparedStatement statement, Department object) throws PersistException {
        try {

        } catch (Exception e) {
            throw new PersistException(e);
        }
    }
}

