package org.openjfx.model.services;;

import javafx.collections.ObservableList;
import org.openjfx.model.ApplicationDao;
import org.openjfx.model.DaoFactory;
import org.openjfx.model.PersistException;
import org.openjfx.model.dto.Application;

import java.sql.Connection;
import java.util.List;

public class  ApplicationService {

    DaoFactory factory = new DaoFactory();
    Connection connection = factory.getContext();
    ApplicationDao dao = (ApplicationDao) factory.getDao(connection, Application.class);

    public ApplicationService() throws PersistException {
    }

    public List<Application> findAll() throws PersistException {
        return dao.getAll();
    }

    public Application findOne(int id) throws PersistException {
        return dao.getByPK(id);
    }

    public void saveOrUpdate(Application object) throws PersistException {
        if (object.getId() == 0) {
            dao.create(object);
        }
        else {
            dao.update(object);
        }
    }

    public void delete(Application object) throws PersistException {
        dao.delete(object);
    }
}
