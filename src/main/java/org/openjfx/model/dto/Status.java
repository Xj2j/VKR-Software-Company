package org.openjfx.model.dto;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.openjfx.model.Identified;

public class Status implements Identified {
    private IntegerProperty id = null;
    private StringProperty title;

    public Status(IntegerProperty id, StringProperty title) {
        this.id = new SimpleIntegerProperty();
        this.title = new SimpleStringProperty();
    }

    public Status() {
        this.id = new SimpleIntegerProperty();
        this.title = new SimpleStringProperty();
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
