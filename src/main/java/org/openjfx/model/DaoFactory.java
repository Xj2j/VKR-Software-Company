package org.openjfx.model;

import org.openjfx.model.dto.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DaoFactory implements DaoFactoryInterface<Connection> {

    private String user = "postgres";//Логин пользователя
    private String password = "f28sSca72yd";//Пароль пользователя
    private String url = "jdbc:postgresql://localhost:5432/company";//URL адрес
    private String driver = "org.postgresql.Driver";//Имя драйвера
    private Map<Class, DaoCreator> creators;

    public Connection getContext() throws PersistException {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new PersistException(e);
        }
        return  connection;
    }

    @Override
    public Dao getDao(Connection connection, Class dtoClass) throws PersistException {
        DaoCreator creator = creators.get(dtoClass);
        if (creator == null) {
            throw new PersistException("Dao object for " + dtoClass + " not found.");
        }
        return creator.create(connection);
    }

    public DaoFactory() {
        try {
            Class.forName(driver);//Регистрируем драйвер
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        creators = new HashMap<Class, DaoCreator>();
        creators.put(Costumer.class, new DaoCreator<Connection>() {
            @Override
            public Dao create(Connection connection) {
                return new CostumerDao(DaoFactory.this, connection);
            }
        });
        creators.put(Employee.class, new DaoCreator<Connection>() {
            @Override
            public Dao create(Connection connection) {
                return new EmployeeDao(DaoFactory.this, connection);
            }
        });
        creators.put(Application.class, new DaoCreator<Connection>() {
            @Override
            public Dao create(Connection connection) {
                return new ApplicationDao(DaoFactory.this, connection);
            }
        });
        creators.put(Project.class, new DaoCreator<Connection>() {
            @Override
            public Dao create(Connection connection) {
                return new ProjectDao(DaoFactory.this, connection);
            }
        });
        creators.put(Task.class, new DaoCreator<Connection>() {
            @Override
            public Dao create(Connection connection) {
                return new TaskDao(DaoFactory.this, connection);
            }
        });
        creators.put(Department.class, new DaoCreator<Connection>() {
            @Override
            public Dao create(Connection connection) {
                return new DepartmentDao(DaoFactory.this, connection);
            }
        });
        creators.put(Position.class, new DaoCreator<Connection>() {
            @Override
            public Dao create(Connection connection) {
                return new PositionDao(DaoFactory.this, connection);
            }
        });
        creators.put(Status.class, new DaoCreator<Connection>() {
            @Override
            public Dao create(Connection connection) {
                return new StatusDao(DaoFactory.this, connection);
            }
        });
    }
}
