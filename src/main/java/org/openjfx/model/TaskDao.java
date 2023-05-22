package org.openjfx.model;

import org.openjfx.App;
import org.openjfx.model.dto.*;
import org.openjfx.util.Util;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskDao extends AbstractJDBCDao<Task> {

    private class PersistTask extends Task {
        public void setId(int id) {
            super.setId(id);
        }
    }

    @Override
    public String getSelectQuery() {
        return "SELECT tasks.id, tasks.title, tasks.project_id, tasks.employee_id, tasks.date_of_beginning, " +
                "tasks.date_of_last, tasks.description, tasks.status_id, tasks.report, tasks.date_of_completion, "
                + "projects.title as project_title, employees.fio as employee_fio, statuses.title as status_title "
                + "FROM tasks JOIN projects ON projects.id = tasks.project_id "
                + "JOIN employees ON employees.id = tasks.employee_id JOIN statuses ON statuses.id = tasks.status_id";
    }

    @Override
    public String getSearchQuery() {
        return null;
    }

    @Override
    public String getCreateQuery() {
        return "INSERT INTO tasks (title, project_id, employee_id, date_of_beginning, date_of_last, description, status_id) \n" +
                "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id;";
    }

    @Override
    public String getUpdateQuery() {
            return "UPDATE tasks \n" +
                    "SET title = ?, project_id = ?, employee_id  = ?, date_of_beginning = ?, date_of_last = ?, description = ?, status_id = ?, report = ?, date_of_completion = ? \n" +
                    "WHERE id = ?;";
    }


    @Override
    public String getDeleteQuery() {
        return "DELETE FROM tasks WHERE id= ?;";
    }

    public String getCountQuery() {
        //return "SELECT COUNT(*) as new_tasks_count FROM tasks WHERE status_id = ? and employee_id = ?;";
        return "SELECT  sum(case when status_id = 1 and employee_id = ? then 1 else 0 end) as new_tasks_count,\n" +
                " sum(case when status_id = 2 and employee_id = ? then 1 else 0 end) as work_tasks_count," +
                " sum(case when status_id = 2 and employee_id = ? and ? > date_of_last then 1 else 0 end) as overdue_tasks_count" +
                " FROM tasks";
    }

    public TaskDao(DaoFactoryInterface<Connection> parentFactory, Connection connection) {
        super(parentFactory, connection);
    }

    @Override
    protected List<Task> parseResultSet(ResultSet rs) throws PersistException {

        List<Task> result = new ArrayList<>();
        Map<Integer, Project> projects = new HashMap<>();
        Map<Integer, Employee> employees = new HashMap<>();
        Map<Integer, Status> stats = new HashMap<>();

        try {
            while (rs.next()) {

                Project project = projects.get(rs.getInt("project_id"));
                Employee employee = employees.get(rs.getInt("employee_id"));
                Status status = stats.get(rs.getInt("status_id"));

                if (project == null) {
                    project = parseProject(rs);
                    projects.put(rs.getInt("project_id"), project);
                }

                if (employee == null) {
                    employee = parseEmployee(rs);
                    employees.put(rs.getInt("employee_id"), employee);
                }

                if (status == null) {
                    status = parseStatus(rs);
                    stats.put(rs.getInt("status_id"), status);
                }

                Task obj = parseTask(rs, project, employee, status);
                result.add(obj);
            }
            return result;
        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

    @Override
    protected Task parseResultSetForOne(ResultSet rs) throws PersistException {
        try {
            if (rs.next()) {
                Project project = parseProject(rs);
                Employee employee = parseEmployee(rs);
                Status status = parseStatus(rs);
                Task obj = parseTask(rs, project, employee, status);
                return obj;
            }
            return null;
        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

    @Override
    protected void prepareStatementForSearch(PreparedStatement statement, Task object) throws PersistException {

    }

    private Task parseTask(ResultSet rs, Project project, Employee employee, Status status) throws SQLException {
        Task task = new Task();
        task.setId(rs.getInt("id"));
        task.setTitle(rs.getString("title"));
        task.setProject(project);
        task.setEmployee(employee);
        task.setDateOfStart(rs.getObject(5, LocalDate.class));
        task.setDateOfLast(rs.getObject(6, LocalDate.class));
        task.setDescription(rs.getString("description"));
        task.setStatus(status);
        task.setReport(rs.getString("report"));
        task.setDateOfCompletion(rs.getObject(10, LocalDate.class));
        return task;
    }

    private Project parseProject(ResultSet rs) throws SQLException {
        Project project = new Project();
        project.setId(rs.getInt("project_id"));
        project.setTitle(rs.getString("project_title"));
        return project;
    }

    private Employee parseEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setId(rs.getInt("employee_id"));
        employee.setName(rs.getString("employee_fio"));
        return employee;
    }

    private Status parseStatus(ResultSet rs) throws SQLException {
        Status stat = new Status();
        stat.setId(rs.getInt("status_id"));
        stat.setTitle(rs.getString("status_title"));
        return stat;
    }

    public List<Task> findByProject(Project project) throws PersistException {
        List<Task> list = new ArrayList<>();
        String sql = getSelectQuery();
        sql += " WHERE project_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, project.getId());
            ResultSet rs = statement.executeQuery();
            list = parseResultSet(rs);
        } catch (Exception e) {
            throw new PersistException(e);
        }
        return list;
    }

    public List getCountTasks(Employee employee) throws PersistException {
        List<Integer> list = new ArrayList<>();
        String sql = getCountQuery();
        int count = 0;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, employee.getId());
            statement.setInt(2, employee.getId());
            statement.setInt(3, employee.getId());
            statement.setDate(4, Date.valueOf(LocalDate.now()));
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                list.add(rs.getInt("new_tasks_count"));
                list.add(rs.getInt("work_tasks_count"));
                list.add(rs.getInt("overdue_tasks_count"));
            }
        } catch (Exception e) {
            throw new PersistException(e);
        }
        return list;
    }

    /**@Override
    public void updateFromEmp(Task object) throws PersistException {
        String sql = getUpdateQuery();
        try (PreparedStatement statement = connection.prepareStatement(sql);) {
            prepareStatementForUpdate(statement, object); // заполнение аргументов запроса оставим на совесть потомков
            //int count = statement.executeUpdate();
            statement.executeUpdate();
            //if (count != 1) {
            //throw new PersistException("On update modify more then 1 record: " + count);
            // }
        } catch (Exception e) {
            throw new PersistException(e);
        }
    }*/

    @Override
    protected void prepareStatementForUpdate(PreparedStatement statement, Task object) throws PersistException {
        try {
            int projectId = (object.getProject() == null || object.getProject().idProperty() == null) ? -1
                    : object.getProject().getId();
            int employeeId = (object.getEmployee() == null || object.getEmployee().idProperty() == null) ? -1
                    : object.getEmployee().getId();
            int statusId = (object.getStatus() == null || object.getStatus().idProperty() == null) ? -1
                    : object.getStatus().getId();
            statement.setString(1, object.getTitle());
            statement.setInt(2, projectId);
            statement.setInt(3, employeeId);
            statement.setDate(4, Util.convertToDate(object.getDateOfStart()));
            statement.setDate(5, Util.convertToDate(object.getDateOfLast()));
            statement.setString(6, object.getDescription());
            statement.setInt(7, statusId);
            statement.setString(8, object.getReport());
            //if (App.loggedUser.getIsAdmin()) {
               // statement.setDate(9, Util.convertToDate(object.getDateOfLast()));
            //} else {
                statement.setDate(9, Util.convertToDate(object.getDateOfCompletion()));
           // }
            statement.setInt(10, object.getId());
        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

    @Override
    protected void prepareStatementForInsert(PreparedStatement statement, Task object) throws PersistException {
        try {
                Date sqlDateBeg = Util.convertToDate(object.getDateOfStart());
                Date sqlDateLast = Util.convertToDate(object.getDateOfLast());
                int projectId = (object.getProject() == null || object.getProject().idProperty() == null) ? -1
                        : object.getProject().getId();
                int employeeId = (object.getEmployee() == null || object.getEmployee().idProperty() == null) ? -1
                        : object.getEmployee().getId();
                int statusId = (object.getStatus() == null || object.getStatus().idProperty() == null) ? -1
                        : object.getStatus().getId();
                statement.setString(1, object.getTitle());
                statement.setInt(2, projectId);
                statement.setInt(3, employeeId);
                statement.setDate(4, sqlDateBeg);
                statement.setDate(5, sqlDateLast);
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