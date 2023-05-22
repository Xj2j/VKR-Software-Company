package org.openjfx.model.services;

import javafx.collections.ObservableList;
import org.openjfx.model.DaoFactory;
import org.openjfx.model.PersistException;
import org.openjfx.model.ProjectDao;
import org.openjfx.model.TaskDao;
import org.openjfx.model.dto.Employee;
import org.openjfx.model.dto.Project;
import org.openjfx.model.dto.Task;

import java.sql.Connection;
import java.util.List;

public class TaskService {

    DaoFactory factory = new DaoFactory();
    Connection connection = factory.getContext();
    TaskDao dao = (TaskDao) factory.getDao(connection, Task.class);

    public TaskService() throws PersistException {
    }

    public List<Task> findAll() throws PersistException {
        return dao.getAll();
    }

    public void saveOrUpdate(Task object) throws PersistException {
        if (object.getId() == 0) {
            dao.create(object);
        }
        else {
            dao.update(object);
        }
    }

    public List<Task> findByProject(Project project) throws PersistException {
        return dao.findByProject(project);
    }

    public List<Integer> getCountTasks(Employee employee) throws PersistException {
        return dao.getCountTasks(employee);
    }

    public void delete(Task object) throws PersistException {
        dao.delete(object);
    }
}
