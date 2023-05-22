package org.openjfx.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.openjfx.model.PersistException;
import org.openjfx.model.dto.Department;
import org.openjfx.model.dto.Employee;
import org.openjfx.model.dto.Position;
import org.openjfx.model.services.DepartmentService;
import org.openjfx.model.services.EmployeeService;
import org.openjfx.model.services.PositionService;
import org.openjfx.DataChangeListener;
import org.openjfx.util.Alerts;
import org.openjfx.util.Util;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class EmployeeFormController {

    @FXML
    private TextField addressTextField;

    @FXML
    private ComboBox<Department> departmentCB;

    @FXML
    private TextField emailTextField;

    @FXML
    private TextField idTextField;

    @FXML
    private TextField loginTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField numberTextField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private ComboBox<Position> positionCB;

    @FXML
    private CheckBox isAdminCheckBox;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSave;

    @FXML
    private Label passwordLabel;

    private EmployeeService service;

    private DepartmentService departmentService;

    private PositionService positionService;

    private ObservableList<Department> obsListDep;

    private ObservableList<Position> obsListPos;

    private Stage dialogStage;
    private Employee employee;
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

    public void setServices(EmployeeService service, DepartmentService departmentService, PositionService positionService) {
        this.service = service;
        this.departmentService = departmentService;
        this.positionService = positionService;
    }

    public void setEmployee(Employee obj) {
        this.employee = obj;
    }

    @FXML
    public void onBtnSaveAction(ActionEvent event) {
        /**if (employee == null) {
            throw new IllegalStateException("Entity was null");
        }
        if (service == null) {
            throw new IllegalStateException("Service was null");
        }*/
        try {
            Employee employee2 = getFormData();
            service.saveOrUpdate(employee2);
            notifyDataChangeListeners();
            Util.currentStage(event).close();
        }
        catch (PersistException e) {
            Alerts.showAlert("Error saving object", null, e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void updateFormData() {
        if (employee == null) {
            throw new IllegalStateException("Entity was null");
        }
        idTextField.setText(Integer.toString(employee.getId()));
        nameTextField.setText(employee.getName());
        numberTextField.setText(employee.getTelephoneNumber());
        emailTextField.setText(employee.getEmail());
        addressTextField.setText(employee.getAddress());
        if (employee.getDepartment() == null) {
            departmentCB.getSelectionModel().selectFirst();
        } else {
            departmentCB.setValue(employee.getDepartment());
        }
        if (employee.getPosition() == null) {
            positionCB.getSelectionModel().selectFirst();
        } else {
            positionCB.setValue(employee.getPosition());
        }
        isAdminCheckBox.setSelected(employee.getIsAdmin());  //setText(employee.isRole() ? "Администратор" : "Сотрудник");
        loginTextField.setText(employee.getLogin());
    }

    private Employee getFormData() {

        Employee obj = new Employee();

        if (isInputValid()) {
            obj.setId(Integer.parseInt(idTextField.getText()));
            obj.setName(nameTextField.getText());
            obj.setDepartment(departmentCB.getValue());
            obj.setPosition(positionCB.getValue());
            obj.setTelephoneNumber(numberTextField.getText());
            obj.setEmail(emailTextField.getText());
            obj.setAddress(addressTextField.getText());
            obj.setIsAdmin(isAdminCheckBox.isSelected());
            obj.setLogin(loginTextField.getText());
            obj.setPassword(passwordTextField.getText());

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
        if (departmentCB.getValue() == null) {
            errorMessage += "Не выбран отдел!\n";
        }
        if (positionCB.getValue() == null ) {
            errorMessage += "не выбрана должность!\n";
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
        if (loginTextField.getText() == null || loginTextField.getText().length() == 0) {
            errorMessage += "Не валидный логин!\n";
        }
        if (passwordTextField.isVisible()) {
            if (passwordTextField.getText() == null || passwordTextField.getText().length() == 0) {
            errorMessage += "Не валидный пароль!\n";
            }
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
        if (departmentService == null) {
            throw new IllegalStateException("DepartmentService was null");
        }
        if (positionService == null) {
            throw new IllegalStateException("PositionService was null");
        }

        List<Department> listDep = departmentService.findAll();
        obsListDep = FXCollections.observableArrayList(listDep);
        departmentCB.setItems(obsListDep);

        List<Position> listPos = positionService.findAll();
        obsListPos = FXCollections.observableArrayList(listPos);
        positionCB.setItems(obsListPos);
    }

    private void initializeCBDepartment() {
        Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
            @Override
            protected void updateItem(Department item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getTitle());
            }
        };
        departmentCB.setCellFactory(factory);
        departmentCB.setButtonCell(factory.call(null));
    }

    private void initializeCBPosition() {
        Callback<ListView<Position>, ListCell<Position>> factory = lv -> new ListCell<Position>() {
            @Override
            protected void updateItem(Position item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getTitle());
            }
        };
        positionCB.setCellFactory(factory);
        positionCB.setButtonCell(factory.call(null));
    }

    public void setVisiblePasswordForm(Boolean e) {
        passwordLabel.setVisible(e);
        passwordTextField.setVisible(e);
    }

}
