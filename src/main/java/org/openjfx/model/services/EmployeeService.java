package org.openjfx.model.services;;

import org.openjfx.model.DaoFactory;
import org.openjfx.model.EmployeeDao;
import org.openjfx.model.PersistException;
import org.openjfx.model.dto.Employee;
import java.sql.Connection;
import java.util.List;

public class EmployeeService {

    DaoFactory factory = new DaoFactory();
    Connection connection = factory.getContext();
    EmployeeDao dao = (EmployeeDao) factory.getDao(connection, Employee.class);

    public EmployeeService() throws PersistException {
    }

    public List<Employee> findAll() throws PersistException {
        return dao.getAll();
    }

    public void saveOrUpdate(Employee object) throws PersistException {
        if (object.getId() == 0) {
            dao.create(object);
        }
        else {
            dao.update(object);
        }
    }

    public void delete(Employee object) throws PersistException {
        dao.delete(object);
    }

    public Employee getLoggedUser(String login, String password) throws PersistException {
        return dao.getUser(login, password);
    }

}
