package org.openjfx.model.dto;

import javafx.beans.property.*;
import org.openjfx.model.Identified;

import java.time.LocalDate;

public class Task implements Identified {

    private IntegerProperty id = null;
    private StringProperty title;
    private ObjectProperty<Project> project;
    private ObjectProperty<Employee> employee;
    private SimpleObjectProperty<LocalDate> dateOfStart;
    private SimpleObjectProperty<LocalDate> dateOfLast;
    private StringProperty description;
    private ObjectProperty<Status> status;
    private StringProperty report;
    private SimpleObjectProperty<LocalDate> dateOfCompletion;

    public Task() {
        this.id = new SimpleIntegerProperty();
        this.title = new SimpleStringProperty();
        this.project = new SimpleObjectProperty();
        this.employee = new SimpleObjectProperty();
        this.dateOfStart = new SimpleObjectProperty<LocalDate>();
        this.dateOfLast = new SimpleObjectProperty<LocalDate>();
        this.description = new SimpleStringProperty();
        this.status = new SimpleObjectProperty();
        this.report = new SimpleStringProperty();
        this.dateOfCompletion = new SimpleObjectProperty<LocalDate>();
    }





    @Override
    public int getId() {
        return id.get();
    }

    @Override
    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public Project getProject() {
        return project.get();
    }

    public ObjectProperty<Project> projectProperty() {
        return project;
    }

    public void setProject(Project project) {
        this.project.set(project);
    }

    public Employee getEmployee() {
        return employee.get();
    }

    public ObjectProperty<Employee> employeeProperty() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee.set(employee);
    }

    public LocalDate getDateOfStart() {
        return dateOfStart.get();
    }

    public SimpleObjectProperty<LocalDate> dateOfStartProperty() {
        return dateOfStart;
    }

    public void setDateOfStart(LocalDate dateOfStart) {
        this.dateOfStart.set(dateOfStart);
    }

    public LocalDate getDateOfCompletion() {
        return dateOfCompletion.get();
    }

    public SimpleObjectProperty<LocalDate> dateOfCompletionProperty() {
        return dateOfCompletion;
    }

    public void setDateOfCompletion(LocalDate dateOfCompletion) {
        this.dateOfCompletion.set(dateOfCompletion);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public Status getStatus() {
        return status.get();
    }

    public ObjectProperty<Status> statusProperty() {
        return status;
    }

    public void setStatus(Status status) {
        this.status.set(status);
    }

    public String getReport() {
        return report.get();
    }

    public StringProperty reportProperty() {
        return report;
    }

    public void setReport(String report) {
        this.report.set(report);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public LocalDate getDateOfLast() {
        return dateOfLast.get();
    }

    public SimpleObjectProperty<LocalDate> dateOfLastProperty() {
        return dateOfLast;
    }

    public void setDateOfLast(LocalDate dateOfLast) {
        this.dateOfLast.set(dateOfLast);
    }

    @Override
    public String toString() {
        return getTitle();
    }
}
