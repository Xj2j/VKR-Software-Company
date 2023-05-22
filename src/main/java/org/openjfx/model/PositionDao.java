package org.openjfx.model;

import org.openjfx.model.dto.Position;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PositionDao extends AbstractJDBCDao<Position> {

    public class PersistPosition extends Position {
        public void setId(int id) {
            super.setId(id);
        }
    }

    @Override
    public String getSelectQuery() {
        return "SELECT id, title FROM positions ";
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

    public PositionDao(DaoFactoryInterface<Connection> parentFactory, Connection connection) {
        super(parentFactory, connection);
    }

    @Override
    protected List<Position> parseResultSet(ResultSet rs) throws PersistException {
        List<Position> result = new ArrayList<>();
        try {
            while (rs.next()) {
                Position position = new Position();
                position.setId(rs.getInt("id"));
                position.setTitle(rs.getString("title"));
                result.add(position);
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
    protected void prepareStatementForSearch(PreparedStatement statement, Position object) throws PersistException {

    }

    @Override
    protected void prepareStatementForUpdate(PreparedStatement statement, Position object) throws PersistException {
        try {

        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

    @Override
    protected void prepareStatementForInsert(PreparedStatement statement, Position object) throws PersistException {
        try {

        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

}
