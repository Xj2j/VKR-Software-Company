package org.openjfx.model.dto;

import javafx.beans.property.*;
import org.openjfx.model.Identified;

import java.time.LocalDate;


public class Project implements Identified {

    private IntegerProperty id = null;
    private StringProperty title;
    private ObjectProperty<Application> application;
    private ObjectProperty<Employee> head;
    private SimpleObjectProperty<LocalDate> dateOfStart;
    private SimpleObjectProperty<LocalDate> dateOfCompletion;
    private StringProperty description;
    private ObjectProperty<Status> status;

    public Project() {
        this.id = new SimpleIntegerProperty();
        this.title = new SimpleStringProperty();
        this.application = new SimpleObjectProperty<>();
        this.head = new SimpleObjectProperty<>();
        this.dateOfStart = new SimpleObjectProperty<LocalDate>();
        this.dateOfCompletion = new SimpleObjectProperty<LocalDate>();
        this.description = new SimpleStringProperty();
        this.status = new SimpleObjectProperty<>();
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

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public Application getApplication() {
        return application.get();
    }

    public ObjectProperty<Application> applicationProperty() {
        return application;
    }

    public void setApplication(Application application) {
        this.application.set(application);
    }

    public Employee getHead() {
        return head.get();
    }

    public ObjectProperty<Employee> headProperty() {
        return head;
    }

    public void setHead(Employee head) {
        this.head.set(head);
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

    @Override
    public String toString() {
        return getTitle();
    }

}