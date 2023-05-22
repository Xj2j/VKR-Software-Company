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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class TaskFormController {

    @FXML
    private DatePicker datePicker1;

    @FXML
    private DatePicker datePicker2;

    @FXML
    private DatePicker datePicker3;


    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private ComboBox<Employee> employeeCB;

    @FXML
    private TextField idTextField;

    @FXML
    private TextField titleTextField;

    @FXML
    private TextField projectTitleTextField;

    @FXML
    private ComboBox<Status> statusCB;

    @FXML
    private TextArea reportTextArea;

    private TaskService service;

    private ProjectService projectService;

    private StatusService statusService;

    private EmployeeService employeeService;

    private Project project;

    private ObservableList<Employee> obsListEmp;

    private ObservableList<Status> obsListStat;


    private Stage dialogStage;
    private Task task;
    private boolean okClicked = false;
    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();


    /**
     * Инициализирует класс-контроллер. Этот метод вызывается автоматически
     * после того, как fxml-файл будет загружен.
     */
    @FXML
    private void initialize() { }

    public void subscribeDataChangeListener(DataChangeListener listener) {
        dataChangeListeners.add(listener);
    }

    private void notifyDataChangeListeners() throws PersistException {
        for (DataChangeListener listener : dataChangeListeners) {
            listener.onDataChanged();
        }
    }

    public void setServices(TaskService service, ProjectService projectService, StatusService statusService) {
        this.service = service;
        this.projectService = projectService;
        this.statusService = statusService;
    }

    public void setServices(TaskService service, EmployeeService employeeService, StatusService statusService) {
        this.service = service;
        this.employeeService = employeeService;
        this.statusService = statusService;
    }

    public void setTask(Task obj) {
        this.task = obj;
    }

    public void setProject(Project obj) {
        this.project = obj;
    }

    @FXML
    public void onBtnSaveAction(ActionEvent event) {
        try {
            Task obj = getFormData();
            service.saveOrUpdate(obj);
            notifyDataChangeListeners();
            Util.currentStage(event).close();
        }
        catch (PersistException e) {
            Alerts.showAlert("Error saving object", null, e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onBtnUpdateAction(ActionEvent event) throws PersistException {
        try {
            Task obj = getFormDataUpd(task);
            service.saveOrUpdate(obj);
            notifyDataChangeListeners();
            Util.currentStage(event).close();
        }
        catch (PersistException e) {
            Alerts.showAlert("Error saving object", null, e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void updateFormData() {
        if (App.loggedUser.getIsAdmin()) {
            if (task == null) {
                throw new IllegalStateException("Entity was null");
            }
            idTextField.setText(Integer.toString(task.getId()));
            if (task.getDateOfStart() != null) {
                datePicker1.setValue(task.getDateOfStart());
            }
            if (task.getDateOfLast() != null) {
                datePicker2.setValue(task.getDateOfLast());
            }
            if (task.getTitle() != null) {
                titleTextField.setText(task.getTitle());
            }
            if (task.getEmployee() == null) {
                employeeCB.getSelectionModel().selectFirst();
            } else {
                employeeCB.setValue(task.getEmployee());
            }
            if (task.getStatus() == null) {
                statusCB.getSelectionModel().selectFirst();
            } else {
                statusCB.setValue(task.getStatus());
            }
            projectTitleTextField.setText(project.getTitle());
            descriptionTextArea.setText(task.getDescription());
            reportTextArea.setText(task.getReport());
            if (task.getDateOfCompletion() != null) {
                datePicker3.setValue(task.getDateOfCompletion());
            }
        } else {
            idTextField.setText(Integer.toString(task.getId()));
            reportTextArea.setText(task.getReport());
            datePicker3.setValue(LocalDate.now());

            //datePicker3.setValue(task.getDateOfLast());
        }
    }

    private Task getFormData() {
        Task obj = new Task();
        if (isInputValid()) {
            obj.setId(Integer.parseInt(idTextField.getText()));
            obj.setProject(project);
            obj.setDateOfStart(datePicker1.getValue());
            obj.setDateOfLast(datePicker2.getValue());
            obj.setTitle(titleTextField.getText());
            obj.setEmployee(employeeCB.getValue());
            obj.setStatus(statusCB.getValue());
            obj.setDescription(descriptionTextArea.getText());
            obj.setReport(reportTextArea.getText());
            if (datePicker3.getValue() != null) {
                obj.setDateOfCompletion(datePicker3.getValue());
            }
            return obj;
        }
        return null;
    }

    private Task getFormDataUpd(Task obj) {
            obj.setStatus(obj.getStatus());
            obj.setReport(reportTextArea.getText());
            obj.setDateOfCompletion(datePicker3.getValue());
            return obj;
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
            errorMessage += "Не валидный текст!\n";
        }
        if (employeeCB.getValue() == null) {
            errorMessage += "Не выбран сотрудник!\n";
        }
        if (statusCB.getValue() == null ) {
            errorMessage += "Не выбран статус!\n";
        }
        if (datePicker1.getValue() == null) {
            errorMessage += "Не указана дата создания задачи!\n";
        }
        if (datePicker2.getValue() == null) {
            errorMessage += "Не указан срок задачи!\n";
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
        if (App.loggedUser.getIsAdmin()) {
            if (employeeService == null) {
                throw new IllegalStateException("employeeService was null");
            }

            if (statusService== null) {
                throw new IllegalStateException("employeeService was null");
            }

            List<Employee> listEmp = employeeService.findAll();
            obsListEmp = FXCollections.observableArrayList(listEmp);
            employeeCB.setItems(obsListEmp);

            List<Status> listStat = statusService.findAll();
            obsListStat = FXCollections.observableArrayList(listStat);
            statusCB.setItems(obsListStat);
        }

        /**if (statusService == null) {
            throw new IllegalStateException("statusService was null");
        }

        List<Status> listPos = statusService.findAll();
        obsListStat = FXCollections.observableArrayList(listPos);
        statusCB.setItems(obsListStat);*/
    }

}