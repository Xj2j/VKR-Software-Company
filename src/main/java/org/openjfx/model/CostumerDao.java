package org.openjfx.model;

import org.openjfx.model.dto.Costumer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CostumerDao extends AbstractJDBCDao<Costumer> {

    @Override
    public String getSelectQuery() {
        return "SELECT id, title, telephone_number, email, address FROM costumers";
    }

    @Override
    public String getSearchQuery() {
        return null;
    }

    @Override
    public String getCreateQuery() {
        return "INSERT INTO costumers (title, telephone_number, email, address) \n" +
                "VALUES (?, ?, ?, ?);";
    }

    @Override
    public String getUpdateQuery() {
        return "UPDATE costumers SET title= ?, telephone_number = ?, email = ?, address = ? WHERE id= ?;";
    }

    @Override
    public String getDeleteQuery() {
        return "DELETE FROM costumers WHERE id= ?;";
    }

    public CostumerDao(DaoFactoryInterface<Connection> parentFactory, Connection connection) {
        super(parentFactory, connection);
    }

    @Override
    protected List<Costumer> parseResultSet(ResultSet rs) throws PersistException {
        List<Costumer> result = new ArrayList<>();
        try {
            while (rs.next()) {
                Costumer costumer = new Costumer();
                costumer.setId(rs.getInt("id"));
                costumer.setName(rs.getString("title"));
                costumer.setTelephoneNumber(rs.getString("telephone_number"));
                costumer.setEmail(rs.getString("email"));
                costumer.setAddress(rs.getString("address"));
                result.add(costumer);
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
    protected void prepareStatementForSearch(PreparedStatement statement, Costumer object) throws PersistException {

    }

    @Override
    protected void prepareStatementForUpdate(PreparedStatement statement, Costumer object) throws PersistException {
        try {
            statement.setString(1, object.getName());
            statement.setString(2, object.getTelephoneNumber());
            statement.setString(3, object.getEmail());
            statement.setString(4, object.getAddress());
            statement.setInt(5, object.getId());
        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

    @Override
    protected void prepareStatementForInsert(PreparedStatement statement, Costumer object) throws PersistException {
        try {
            statement.setString(1, object.getName());
            statement.setString(2, object.getTelephoneNumber());
            statement.setString(3, object.getEmail());
            statement.setString(4, object.getAddress());
        } catch (Exception e) {
            throw new PersistException(e);
        }
    }


}
