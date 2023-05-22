package org.openjfx.model.services;;

import javafx.collections.ObservableList;

import org.openjfx.model.CostumerDao;
import org.openjfx.model.DaoFactory;
import org.openjfx.model.PersistException;
import org.openjfx.model.dto.Costumer;

import java.sql.Connection;
import java.util.List;

public class CostumerService<ClientDao> {

    DaoFactory factory = new DaoFactory();
    Connection connection = factory.getContext();
    CostumerDao dao = (CostumerDao) factory.getDao(connection, Costumer.class);

    public CostumerService() throws PersistException {
    }

    public List<Costumer> findAll() throws PersistException {
        return dao.getAll();
    }

    public void saveOrUpdate(Costumer object) throws PersistException {
        if (object.getId() == 0) {
            dao.create(object);
        }
        else {
            dao.update(object);
        }
    }

    public void delete(Costumer object) throws PersistException {
        dao.delete(object);
    }

}
