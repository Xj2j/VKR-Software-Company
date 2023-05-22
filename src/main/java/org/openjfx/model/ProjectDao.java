package org.openjfx.model;

import org.openjfx.model.dto.*;
import org.openjfx.util.Util;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectDao extends AbstractJDBCDao<Project> {

    @Override
    public String getSelectQuery() {
        return "SELECT projects.id, projects.title, projects.application_id, projects.leader_id, projects.date_of_beginning, projects.date_of_completion, projects.description, projects.status_id, "
                + "applications.title as app_title, employees.fio as leader_fio, statuses.title as status_title "
                + "FROM projects JOIN applications ON applications.id = projects.application_id "
                + "JOIN employees ON employees.id = projects.leader_id JOIN statuses ON statuses.id = projects.status_id";
    }

    @Override
    public String getSearchQuery() {
        return null;
    }

    @Override
    public String getCreateQuery() {
        return "INSERT INTO projects (title, application_id, leader_id, date_of_beginning, date_of_completion, description, status_id) \n" +
                "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id;";
    }

    @Override
    public String getUpdateQuery() {
        return "UPDATE projects \n" +
                "SET title = ?, application_id = ?, leader_id  = ?, date_of_beginning = ?, date_of_completion = ?, description = ?, status_id = ? \n" +
                "WHERE id = ?;";
    }

    @Override
    public String getDeleteQuery() {
        return "DELETE FROM projects WHERE id= ?;";
    }

    public ProjectDao(DaoFactoryInterface<Connection> parentFactory, Connection connection) {
        super(parentFactory, connection);
    }

    @Override
    protected List<Project> parseResultSet(ResultSet rs) throws PersistException {

        List<Project> result = new ArrayList<>();
        Map<Integer, Application> applications = new HashMap<>();
        Map<Integer, Employee> heads = new HashMap<>();
        Map<Integer, Status> stats = new HashMap<>();

        try {
            while (rs.next()) {

                Application application = applications.get(rs.getInt("application_id"));
                Employee head = heads.get(rs.getInt("leader_id"));
                Status stat = stats.get(rs.getInt("status_id"));

                if (application == null) {
                    application = parseApplication(rs);
                    applications.put(rs.getInt("application_id"), application);
                }

                if (head == null) {
                    head = parseEmployee(rs);
                    heads.put(rs.getInt("leader_id"), head);
                }

                if (stat == null) {
                    stat = parseStatus(rs);
                    stats.put(rs.getInt("status_id"), stat);
                }

                Project obj = parseProject(rs, application, head, stat);
                result.add(obj);
            }
            return result;
        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

    @Override
    protected Project parseResultSetForOne(ResultSet rs) throws PersistException {
        try {
            if (rs.next()) {
                Application application = parseApplication(rs);
                Employee head = parseEmployee(rs);
                Status status = parseStatus(rs);
                Project obj = parseProject(rs, application, head, status);
                return obj;
            }
            return null;
        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

    @Override
    protected void prepareStatementForSearch(PreparedStatement statement, Project object) throws PersistException {

    }

    private Project parseProject(ResultSet rs, Application application, Employee head, Status status) throws SQLException {
        Project project = new Project();
        project.setId(rs.getInt("id"));
        project.setTitle(rs.getString("title"));
        project.setApplication(application);
        project.setHead(head);
        project.setDateOfStart(rs.getObject(5, LocalDate.class));
        project.setDateOfCompletion(rs.getObject(6, LocalDate.class));
        project.setDescription(rs.getString("description"));
        project.setStatus(status);
        return project;
    }

    private Application parseApplication(ResultSet rs) throws SQLException {
        Application application = new Application();
        application.setId(rs.getInt("application_id"));
        application.setTitle(rs.getString("app_title"));
        return application;
    }

    private Employee parseEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setId(rs.getInt("leader_id"));
        employee.setName(rs.getString("leader_fio"));
        return employee;
    }

    private Status parseStatus(ResultSet rs) throws SQLException {
        Status stat = new Status();
        stat.setId(rs.getInt("status_id"));
        stat.setTitle(rs.getString("status_title"));
        return stat;
    }

    @Override
    protected void prepareStatementForUpdate(PreparedStatement statement, Project object) throws PersistException {
        try {
            Date sqlDateStart = Util.convertToDate(object.getDateOfStart());
            Date sqlDateCom = Util.convertToDate(object.getDateOfCompletion());
            int applicationId = (object.getApplication() == null || object.getApplication().idProperty() == null) ? -1
                    : object.getApplication().getId();
            int employeeId = (object.getHead() == null || object.getHead().idProperty() == null) ? -1
                    : object.getHead().getId();
            int statusId = (object.getStatus() == null || object.getStatus().idProperty() == null) ? -1
                    : object.getStatus().getId();
            statement.setString(1, object.getTitle());
            statement.setInt(2, applicationId);
            statement.setInt(3, employeeId);
            statement.setDate(4, sqlDateStart);
            statement.setDate(5, sqlDateCom);
            statement.setString(6, object.getDescription());
            statement.setInt(7, statusId);
            statement.setInt(8, object.getId());
        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

    @Override
    protected void prepareStatementForInsert(PreparedStatement statement, Project object) throws PersistException {
        try {
            Date sqlDateStart = Util.convertToDate(object.getDateOfStart());
            Date sqlDateCom = Util.convertToDate(object.getDateOfCompletion());
            int applicationId = (object.getApplication() == null || object.getApplication().idProperty() == null) ? -1
                    : object.getApplication().getId();
            int employeeId = (object.getHead() == null || object.getHead().idProperty() == null) ? -1
                    : object.getHead().getId();
            int statusId = (object.getStatus() == null || object.getStatus().idProperty() == null) ? -1
                    : object.getStatus().getId();
            statement.setString(1, object.getTitle());
            statement.setInt(2, applicationId);
            statement.setInt(3, employeeId);
            statement.setDate(4, sqlDateStart);
            statement.setDate(5, sqlDateCom);
            statement.setString(6, object.getDescription());
            statement.setInt(7, statusId);
        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

    protected java.sql.Date convert(java.util.Date date) {
        if (date == null) {
            return null;
        }
        return new java.sql.Date(date.getTime());
    }
}
