package org.openjfx.model.services;;

import javafx.collections.ObservableList;
import org.openjfx.model.DaoFactory;
import org.openjfx.model.DepartmentDao;
import org.openjfx.model.PersistException;
import org.openjfx.model.dto.Department;

import java.sql.Connection;
import java.util.List;

public class DepartmentService {

    DaoFactory factory = new DaoFactory();
    Connection connection = factory.getContext();
    DepartmentDao dao = (DepartmentDao) factory.getDao(connection, Department.class);

    public DepartmentService() throws PersistException {
    }

    public List<Department> findAll() throws PersistException {
        return dao.getAll();
    }

    public void saveOrUpdate(Department object) throws PersistException {
        if (object.getId() == 0) {
            dao.create(object);
        }
        else {
            dao.update(object);
        }
    }

    public void delete(Department object) throws PersistException {
        dao.delete(object);
    }

}
