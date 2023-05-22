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
import org.openjfx.model.services.ApplicationService;
import org.openjfx.model.services.ProjectService;
import org.openjfx.model.services.StatusService;
import org.openjfx.util.Alerts;
import org.openjfx.util.Util;

import java.util.ArrayList;
import java.util.List;

public class ProjectFormController {

    @FXML
    private ComboBox<Application> applicationCB;

    @FXML
    private DatePicker datePicker1;

    @FXML
    private DatePicker datePicker2;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private TextField headTextField;

    @FXML
    private TextField idTextField;

    @FXML
    private TextField titleTextField;

    @FXML
    private ComboBox<Status> statusCB;

    private ProjectService service;

    private ApplicationService applicationService;

    private StatusService statusService;

    private ObservableList<Application> obsListApp;

    private ObservableList<Status> obsListStat;

    private App mainApp;

    private Stage dialogStage;
    private Project project;
    private boolean okClicked = false;
    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

    /**
     * Инициализирует класс-контроллер. Этот метод вызывается автоматически
     * после того, как fxml-файл будет загружен.
     */
    @FXML
    private void initialize() {
        applicationCB.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            datePicker1.setValue(newValue.getDateOfApplication());
            datePicker2.setValue(newValue.getDateOfCompletion());
    });}

    public void subscribeDataChangeListener(DataChangeListener listener) {
        dataChangeListeners.add(listener);
    }

    private void notifyDataChangeListeners() throws PersistException {
        for (DataChangeListener listener : dataChangeListeners) {
            listener.onDataChanged();
        }
    }

    public void setServices(ProjectService service, ApplicationService applicationService, StatusService statusService) {
        this.service = service;
        this.applicationService = applicationService;
        this.statusService = statusService;
    }

    public void setProject(Project obj) {
        this.project = obj;
    }

    @FXML
    public void onBtnSaveAction(ActionEvent event) {
        try {
            Project obj = getFormData();
            service.saveOrUpdate(obj);
            updateApplicationStatusForProject(obj);
            notifyDataChangeListeners();
            Util.currentStage(event).close();
        }
        catch (PersistException e) {
            Alerts.showAlert("Error saving object", null, e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void updateApplicationStatusForProject(Project obj) throws PersistException {
        Application app = obj.getApplication();
        app.setStatus(obsListStat.get(1));
        applicationService.saveOrUpdate(app);
    }

    public void updateFormData() {
        if (project == null) {
            throw new IllegalStateException("Entity was null");
        }
        idTextField.setText(Integer.toString(project.getId()));
        if (project.getApplication() == null) {
            applicationCB.getSelectionModel().selectFirst();
        } else {
            applicationCB.setValue(project.getApplication());
        }
        if (project.getDateOfStart() != null) {
            datePicker1.setValue(project.getDateOfStart());
        }
        if (project.getDateOfCompletion() != null) {
            datePicker2.setValue(project.getDateOfCompletion());
        }
        titleTextField.setText(project.getTitle());
        if (project.getStatus() == null) {
            statusCB.getSelectionModel().selectFirst();
        } else {
            statusCB.setValue(project.getStatus());
        }
        headTextField.setText(App.loggedUser.getName());
        descriptionTextArea.setText(project.getDescription());
    }

    private Project getFormData() {

        Project obj = new Project();

        if (isInputValid()) {
            obj.setId(Integer.parseInt(idTextField.getText()));
            obj.setDateOfStart(datePicker1.getValue());
            obj.setDateOfCompletion(datePicker2.getValue());
            obj.setTitle(titleTextField.getText());
            obj.setApplication(applicationCB.getValue());
            obj.setStatus(statusCB.getValue());
            obj.setHead(App.loggedUser);
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

        if (titleTextField.getText() == null || titleTextField.getText().length() == 0) {
            errorMessage += "Не валидное ФИО!\n";
        }
        if (applicationCB.getValue() == null) {
            errorMessage += "Не выбран отдел!\n";
        }
        if (statusCB.getValue() == null ) {
            errorMessage += "не выбрана должность!\n";
        }
        if (titleTextField.getText() == null || titleTextField.getText().length() == 0) {
            errorMessage += "Не указан мобильный номер!\n";
        }
        if (datePicker1.getValue() == null) {
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

    public void loadAssociatedObjects() throws PersistException {
        if (applicationService == null) {
            throw new IllegalStateException("costumerService was null");
        }
        if (statusService == null) {
            throw new IllegalStateException("statusService was null");
        }

        List<Application> listApp = applicationService.findAll();
        obsListApp = FXCollections.observableArrayList(listApp);
        applicationCB.setItems(obsListApp);

        List<Status> listPos = statusService.findAll();
        obsListStat = FXCollections.observableArrayList(listPos);
        statusCB.setItems(obsListStat);
    }

}
