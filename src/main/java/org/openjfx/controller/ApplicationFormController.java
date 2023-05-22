package org.openjfx.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.openjfx.App;
import org.openjfx.DataChangeListener;
import org.openjfx.model.PersistException;
import org.openjfx.model.dto.*;
import org.openjfx.model.services.*;
import org.openjfx.util.Alerts;
import org.openjfx.util.Util;

import java.sql.Date;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class ApplicationFormController {

    @FXML
    private TextField priceTextField;

    @FXML
    private ComboBox<Costumer> costumerCB;

    @FXML
    private DatePicker datePicker1;

    @FXML
    private DatePicker datePicker2;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private TextField idTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private ComboBox<Status> statusCB;

    private ApplicationService service;

    private CostumerService costumerService;

    private StatusService statusService;

    private ObservableList<Costumer> obsListCos;

    private ObservableList<Status> obsListStat;

    private App mainApp;

    private Stage dialogStage;
    private Application application;
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

    public void setServices(ApplicationService service, CostumerService costumerService, StatusService statusService) {
        this.service = service;
        this.costumerService = costumerService;
        this.statusService = statusService;
    }

    public void setApplication(Application obj) {
        this.application = obj;
    }

    @FXML
    public void onBtnSaveAction(ActionEvent event) {
        try {
            Application application2 = getFormData();
            service.saveOrUpdate(application2);
            notifyDataChangeListeners();
            Util.currentStage(event).close();
        }
        catch (PersistException e) {
            Alerts.showAlert("Error saving object", null, e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void updateFormData() {
        if (application == null) {
            throw new IllegalStateException("Entity was null");
        }
        idTextField.setText(Integer.toString(application.getId()));
        if (application.getDateOfApplication() != null) {
            datePicker1.setValue(application.getDateOfApplication());
        }
        if (application.getDateOfCompletion() != null) {
            datePicker2.setValue(application.getDateOfCompletion());
        }
        nameTextField.setText(application.getTitle());
        if (application.getCostumer() == null) {
            costumerCB.getSelectionModel().selectFirst();
        } else {
            costumerCB.setValue(application.getCostumer());
        }
        priceTextField.setText(Integer.toString(application.getPrice()));
        if (application.getStatus() == null) {
            statusCB.getSelectionModel().selectFirst();
        } else {
            statusCB.setValue(application.getStatus());
        }
        descriptionTextArea.setText(application.getDescription());
    }

    private Application getFormData() {

        Application obj = new Application();

        if (isInputValid()) {
            obj.setId(Integer.parseInt(idTextField.getText()));
            obj.setDateOfApplication(datePicker1.getValue());
            obj.setDateOfCompletion(datePicker2.getValue());
            obj.setTitle(nameTextField.getText());
            obj.setCostumer(costumerCB.getValue());
            obj.setPrice(Integer.parseInt(priceTextField.getText()));
            obj.setStatus(statusCB.getValue());
            obj.setDescription(descriptionTextArea.getText());
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
        if (costumerCB.getValue() == null) {
            errorMessage += "Не выбран отдел!\n";
        }
        if (statusCB.getValue() == null ) {
            errorMessage += "не выбрана должность!\n";
        }
        if (nameTextField.getText() == null || nameTextField.getText().length() == 0) {
            errorMessage += "Не указан мобильный номер!\n";
        }
        if (priceTextField.getText() == null || priceTextField.getText().length() == 0) {
            errorMessage += "Не указана почта!\n";
        }
        if (datePicker1.getValue() == null) {
            errorMessage += "Не указан адрес!\n";
        }
        if (datePicker2.getValue() == null) {
            errorMessage += "Не валидный логин!\n";
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

    public void loadAssociatedObjects() throws PersistException {
        if (costumerService == null) {
            throw new IllegalStateException("costumerService was null");
        }
        if (statusService == null) {
            throw new IllegalStateException("statusService was null");
        }

        List<Costumer> listDep = costumerService.findAll();
        obsListCos = FXCollections.observableArrayList(listDep);
        costumerCB.setItems(obsListCos);

        List<Status> listPos = statusService.findAll();
        obsListStat = FXCollections.observableArrayList(listPos);
        statusCB.setItems(obsListStat);
    }

    /**private void initializeCBCostumer() {
        Callback<ListView<Costumer>, ListCell<Costumer>> factory = lv -> new ListCell<Department>() {
            @Override
            protected void updateItem(Department item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getName());
            }
        };
        costumerCB.setCellFactory(factory);
        costumerCB.setButtonCell(factory.call(null));
    }

    private void initializeCBStatus() {
        Callback<ListView<Status>, ListCell<Status>> factory = lv -> new ListCell<Position>() {
            @Override
            protected void updateItem(Position item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getTitle());
            }
        };
        statusCB.setCellFactory(factory);
        statusCB.setButtonCell(factory.call(null));
    }*/



}
