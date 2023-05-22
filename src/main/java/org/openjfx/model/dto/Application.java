package org.openjfx.model.dto;


import javafx.beans.property.*;
import org.openjfx.model.Identified;

import java.time.LocalDate;


public class Application implements Identified {

    private IntegerProperty id = null;
    private StringProperty title;
    private ObjectProperty<Costumer> costumer;
    private SimpleObjectProperty<LocalDate> dateOfApplication;
    private SimpleObjectProperty<LocalDate> dateOfCompletion;
    private IntegerProperty price;
    private StringProperty description;
    private ObjectProperty<Status> status;

    public Application() {
        this.id =  new SimpleIntegerProperty();
        this.title = new SimpleStringProperty();
        this.costumer = new SimpleObjectProperty();
        this.dateOfApplication = new SimpleObjectProperty();
        this.dateOfCompletion = new SimpleObjectProperty();
        this.price = new SimpleIntegerProperty();
        this.description = new SimpleStringProperty();
        this.status = new SimpleObjectProperty();
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

    public Costumer getCostumer() {
        return costumer.get();
    }

    public ObjectProperty<Costumer> costumerProperty() {
        return costumer;
    }

    public void setCostumer(Costumer costumer) {
        this.costumer.set(costumer);
    }

    public LocalDate getDateOfApplication() {
        return dateOfApplication.get();
    }

    public SimpleObjectProperty<LocalDate> dateOfApplicationProperty() {
        return dateOfApplication;
    }

    public void setDateOfApplication(LocalDate dateOfApplication) {
        this.dateOfApplication.set(dateOfApplication);
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

    public int getPrice() {
        return price.get();
    }

    public IntegerProperty priceProperty() {
        return price;
    }

    public void setPrice(int price) {
        this.price.set(price);
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
