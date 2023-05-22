package org.openjfx.model;

import org.openjfx.model.dto.Status;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StatusDao extends AbstractJDBCDao<Status> {

    @Override
    public String getSelectQuery() {
        return "SELECT id, title FROM statuses ";
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

    public StatusDao(DaoFactoryInterface<Connection> parentFactory, Connection connection) {
        super(parentFactory, connection);
    }

    @Override
    protected List<Status> parseResultSet(ResultSet rs) throws PersistException {
        List<Status> result = new ArrayList<>();
        try {
            while (rs.next()) {
                Status status = new Status();
                status.setId(rs.getInt("id"));
                status.setTitle(rs.getString("title"));
                result.add(status);
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
    protected void prepareStatementForSearch(PreparedStatement statement, Status object) throws PersistException {

    }

    @Override
    protected void prepareStatementForUpdate(PreparedStatement statement, Status object) throws PersistException {
        try {

        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

    @Override
    protected void prepareStatementForInsert(PreparedStatement statement, Status object) throws PersistException {
        try {

        } catch (Exception e) {
            throw new PersistException(e);
        }
    }
}
