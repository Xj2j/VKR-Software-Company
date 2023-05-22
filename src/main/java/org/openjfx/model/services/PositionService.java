package org.openjfx.model.services;;

import javafx.collections.ObservableList;
import org.openjfx.model.DaoFactory;
import org.openjfx.model.PersistException;
import org.openjfx.model.PositionDao;
import org.openjfx.model.dto.Position;

import java.sql.Connection;
import java.util.List;

public class PositionService {

    DaoFactory factory = new DaoFactory();
    Connection connection = factory.getContext();
    PositionDao dao = (PositionDao) factory.getDao(connection, Position.class);

    public PositionService() throws PersistException {
    }

    public List<Position> findAll() throws PersistException {
        return dao.getAll();
    }

    public void saveOrUpdate(Position object) throws PersistException {
        if (object.getId() == 0) {
            dao.create(object);
        }
        else {
            dao.update(object);
        }
    }

    public void delete(Position object) throws PersistException {
        dao.delete(object);
    }
}
