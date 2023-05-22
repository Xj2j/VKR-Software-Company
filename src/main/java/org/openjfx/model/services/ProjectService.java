package org.openjfx.model.services;;

import javafx.collections.ObservableList;
import org.openjfx.model.DaoFactory;
import org.openjfx.model.PersistException;
import org.openjfx.model.ProjectDao;
import org.openjfx.model.dto.Application;
import org.openjfx.model.dto.Project;

import java.sql.Connection;
import java.util.List;

public class ProjectService {

    DaoFactory factory = new DaoFactory();
    Connection connection = factory.getContext();
    ProjectDao dao = (ProjectDao) factory.getDao(connection, Project.class);

    public ProjectService() throws PersistException {
    }

    public List<Project> findAll() throws PersistException {
        return dao.getAll();
    }

    public void saveOrUpdate(Project object) throws PersistException {
        if (object.getId() == 0) {
            dao.create(object);
        }
        else {
            dao.update(object);
        }
    }

    public Project findOne(int id) throws PersistException {
        return dao.getByPK(id);
    }

    public void delete(Project object) throws PersistException {
        dao.delete(object);
    }

}
