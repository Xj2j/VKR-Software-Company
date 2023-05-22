package org.openjfx.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.openjfx.DataChangeListener;
import org.openjfx.model.PersistException;
import org.openjfx.model.dto.Costumer;
import org.openjfx.model.services.CostumerService;
import org.openjfx.util.Alerts;
import org.openjfx.util.Util;

import java.util.ArrayList;
import java.util.List;

public class CostumerFormController {

    @FXML
    private TextField addressTextField;

    @FXML
    private TextField emailTextField;

    @FXML
    private TextField idTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField numberTextField;

    private CostumerService service;

    private Stage dialogStage;
    private Costumer costumer;
    private boolean okClicked = false;
    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

    /**
     * Инициализирует класс-контроллер. Этот метод вызывается автоматически
     * после того, как fxml-файл будет загружен.
     */
    @FXML
    private void initialize() {
        //initializeCBDepartment();
        // initializeCBPosition();
    }

    public void subscribeDataChangeListener(DataChangeListener listener) {
        dataChangeListeners.add(listener);
    }

    private void notifyDataChangeListeners() throws PersistException {
        for (DataChangeListener listener : dataChangeListeners) {
            listener.onDataChanged();
        }
    }

    public void setServices(CostumerService service) {
        this.service = service;
    }

    public void setCostumer(Costumer obj) {
        this.costumer = obj;
    }

    @FXML
    public void onBtnSaveAction(ActionEvent event) {
        try {
            Costumer costumer2 = getFormData();
            service.saveOrUpdate(costumer2);
            notifyDataChangeListeners();
            Util.currentStage(event).close();
        } catch (PersistException e) {
            Alerts.showAlert("Error saving object", null, e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void updateFormData() {
        if (costumer == null) {
            throw new IllegalStateException("Entity was null");
        }
        idTextField.setText(Integer.toString(costumer.getId()));
        nameTextField.setText(costumer.getName());
        numberTextField.setText(costumer.getTelephoneNumber());
        emailTextField.setText(costumer.getEmail());
        addressTextField.setText(costumer.getAddress());
    }

    private Costumer getFormData() {

        Costumer obj = new Costumer();

        if (isInputValid()) {
            obj.setId(Integer.parseInt(idTextField.getText()));
            obj.setName(nameTextField.getText());
            obj.setTelephoneNumber(numberTextField.getText());
            obj.setEmail(emailTextField.getText());
            obj.setAddress(addressTextField.getText());
            return obj;
        }
        return null;
    }

    /**
     * Вызывается, когда пользователь кликнул по кнопке Cancel.
     */
    @FXML
    public void onBtnCancelAction(ActionEvent event) {
        Util.currentStage(event).close();
    }

    /**
     * Проверяет пользовательский ввод в текстовых полях.
     *
     * @return true, если пользовательский ввод корректен
     */

    private boolean isInputValid() {
        String errorMessage = "";

        if (nameTextField.getText() == null || nameTextField.getText().length() == 0) {
            errorMessage += "Не валидное ФИО!\n";
        }
        if (numberTextField.getText() == null || numberTextField.getText().length() == 0) {
            errorMessage += "Не указан мобильный номер!\n";
        }
        if (emailTextField.getText() == null || emailTextField.getText().length() == 0) {
            errorMessage += "Не указана почта!\n";
        }
        if (addressTextField.getText() == null || addressTextField.getText().length() == 0) {
            errorMessage += "Не указан адрес!\n";
        }
        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Показываем сообщение об ошибке.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
        }
    }
}
