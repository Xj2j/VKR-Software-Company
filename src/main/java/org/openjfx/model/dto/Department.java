package org.openjfx.model.dto;

import javafx.beans.property.*;
import org.openjfx.model.Identified;

import java.util.Objects;

public class Department implements Identified {

    private IntegerProperty id = null;
    private StringProperty title;

    public Department() {
        this.id = new SimpleIntegerProperty();
        this.title = new SimpleStringProperty();
    }

    public Department(IntegerProperty id, StringProperty title) {
        this.id = id;
        this.title = title;
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

    @Override
    public String toString() {
        return getTitle();
    }
}
