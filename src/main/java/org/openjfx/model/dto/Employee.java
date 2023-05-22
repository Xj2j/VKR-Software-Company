package org.openjfx.model.dto;
;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.openjfx.model.Identified;

public class Employee extends User implements Identified {

    private Department department;
    private Position position;
    private BooleanProperty isAdmin;
    private StringProperty login;
    private StringProperty password;

    public Employee() {
        super();
        //this.department = new SimpleObjectProperty<>();
        //this.position = new SimpleObjectProperty<>();
        this.isAdmin = new SimpleBooleanProperty();
        this.login = new SimpleStringProperty();
        this.password = new SimpleStringProperty();
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public boolean getIsAdmin() {
        return isAdmin.get();
    }

    public BooleanProperty isAdminProperty() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin.set(isAdmin);
    }

    public String getLogin() {
        return login.get();
    }

    public StringProperty loginProperty() {
        return login;
    }

    public void setLogin(String login) {
        this.login.set(login);
    }

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

}
