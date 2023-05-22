package org.openjfx.model.services;;

import javafx.collections.ObservableList;
import org.openjfx.model.DaoFactory;
import org.openjfx.model.PersistException;
import org.openjfx.model.StatusDao;
import org.openjfx.model.dto.Status;


import java.sql.Connection;
import java.util.List;

public class StatusService {

    DaoFactory factory = new DaoFactory();
    Connection connection = factory.getContext();
    StatusDao dao = (StatusDao) factory.getDao(connection, Status.class);

    public StatusService() throws PersistException {
    }

    public List<Status> findAll() throws PersistException {
        return dao.getAll();
    }

    /**public void saveOrUpdate(Status object) throws PersistException {
     if (object.idProperty() == null) {
     dao.persist(object);
     }
     else {
     dao.update(object);
     }
     }

     public void delete(Status object) throws PersistException {
     dao.delete(object);
     }*/

}
