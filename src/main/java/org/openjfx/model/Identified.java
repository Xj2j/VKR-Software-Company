package org.openjfx.model;

import javafx.beans.property.IntegerProperty;

public interface Identified {

    public int getId();

    public void setId(int id);

    public IntegerProperty idProperty();
}
