package org.openjfx;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.openjfx.controller.EmployeeFormController;
import org.openjfx.model.PersistException;
import org.openjfx.model.dto.*;
import org.openjfx.model.services.DepartmentService;
import org.openjfx.model.services.EmployeeService;
import org.openjfx.model.services.PositionService;
import org.openjfx.util.Alerts;
import org.openjfx.util.Util;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class EmployeeListController implements DataChangeListener {

    @FXML
    private Button addEmployeeBtn;

    @FXML
    private Label addressLabel;

    @FXML
    private Button deleteEmployeeBtn;

    @FXML
    private ComboBox<Department> departmentCB;

    @FXML
    private Label departmentLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private TableView<Employee> employeeTable;

    @FXML
    private TableColumn<Employee, Integer> idColumn;

    @FXML
    private Label loginLabel;

    @FXML
    private TableColumn<Employee, String> nameColumn;

    @FXML
    private Label numberLabel;

    @FXML
    private ComboBox<Position> positionCB;

    @FXML
    private Label positionLabel;

    @FXML
    private Label isAdminLabel;

    @FXML
    private TextField searchTextField;

    @FXML
    private Button updateEmployeeBtn;

    @FXML
    private TableColumn<Employee, Employee> editColumn;

    @FXML
    private TableColumn<Employee, Employee> removeColumn;

    private App mainApp;

    private EmployeeService service;

    private Employee selectEmployee;

    private ObservableList<Employee> obsList;

    private FilteredList<Employee> filteredData;

    private SortedList<Employee> sortedData;

    private DepartmentService departmentService;

    private PositionService positionService;

    private ObservableList<Department> obsListDep;

    private ObservableList<Position> obsListPos;

    @FXML
    public void initialize() throws PersistException {

        //idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        //nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        try {
            setServices(new EmployeeService(), new DepartmentService(), new PositionService());
        } catch (PersistException e) {
            e.printStackTrace();
        }

        loadAssociatedObjects();

        try {
            updateTableView();
        } catch (PersistException e) {
            e.printStackTrace();
        }

        // Инициализация таблицы адресатов с двумя столбцами.
        idColumn.setCellValueFactory(
                cellData -> cellData.getValue().idProperty().asObject());
        nameColumn.setCellValueFactory(
                cellData -> cellData.getValue().nameProperty());

        // Очистка дополнительной информации об адресате.
        showEmployeeDetails(null);

        // Слушаем изменения выбора, и при изменении отображаем
        // дополнительную информацию об адресате.
        employeeTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showEmployeeDetails(newValue));

        //Stage stage = (Stage) App.getMainScene().getWindow();
        //employeeTable.prefHeightProperty().bind(stage.heightProperty());
    }

    @Override
    public void onDataChanged() throws PersistException {
        updateTableView();
    }

    public void setServices(EmployeeService service, DepartmentService departmentService, PositionService positionService) {
        this.service = service;
        this.departmentService = departmentService;
        this.positionService = positionService;
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
        obsListDep.add(0, new Department(new SimpleIntegerProperty(0), new SimpleStringProperty("Все")));
        departmentCB.setItems(obsListDep);
        departmentCB.getSelectionModel().selectFirst();

        List<Position> listPos = positionService.findAll();
        obsListPos = FXCollections.observableArrayList(listPos);
        obsListPos.add(0, new Position(new SimpleIntegerProperty(0), new SimpleStringProperty("Все")));
        positionCB.setItems(obsListPos);
        positionCB.getSelectionModel().selectFirst();
    }

    public void addDepartmentCBFilter() {
        departmentCB.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(employee -> {

                if (departmentCB.getValue().getId() == 0) {

                } else {
                    if (departmentCB.getValue().getId() != employee.getDepartment().getId()) {
                        return false;
                    }
                }

                if (positionCB.getValue().getId() == 0) {

                } else {
                    if (positionCB.getValue().getId() != employee.getPosition().getId()) {
                        return false;
                    }
                }

                if (searchTextField.getText().trim().length() > 0) {
                    String searchKeyword = searchTextField.getText().trim().toLowerCase();
                    if (employee.getName().toLowerCase().contains(searchKeyword) ||
                            employee.getTelephoneNumber().toLowerCase().contains(searchKeyword) ||
                            employee.getEmail().toLowerCase().contains(searchKeyword) ||
                            employee.getAddress().toLowerCase().contains(searchKeyword) ||
                            employee.getLogin().toLowerCase().contains(searchKeyword)) {
                    } else {
                        return false;
                    }
                }
                return true;
            });
            sortedData = new SortedList<>(filteredData);
        });
    }

    public void addPositionCBFilter() {
        positionCB.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(employee -> {

                if (positionCB.getValue().getId() == 0) {

                } else {
                    if (positionCB.getValue().getId() != employee.getPosition().getId()) {
                        return false;
                    }
                }

                if (departmentCB.getValue().getId() == 0) {

                } else {
                    if (departmentCB.getValue().getId() != employee.getDepartment().getId()) {
                        return false;
                    }
                }

                if (searchTextField.getText().trim().length() > 0) {
                    String searchKeyword = searchTextField.getText().trim().toLowerCase();
                    if (employee.getName().toLowerCase().contains(searchKeyword) ||
                            employee.getTelephoneNumber().toLowerCase().contains(searchKeyword) ||
                            employee.getEmail().toLowerCase().contains(searchKeyword) ||
                            employee.getAddress().toLowerCase().contains(searchKeyword) ||
                            employee.getLogin().toLowerCase().contains(searchKeyword)) {
                    } else {
                        return false;
                    }
                }

                return true;
            });
            sortedData = new SortedList<>(filteredData);
        });
    }

    private void addTextFilter() {

        filteredData = new FilteredList<>(obsList, t -> true);

        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(employee -> {

                if (newValue == null || newValue.isEmpty()) {
                } else {
                    String searchKeyword = newValue.toLowerCase();
                    if (employee.getName().toLowerCase().contains(searchKeyword) ||
                            employee.getTelephoneNumber().toLowerCase().contains(searchKeyword) ||
                            employee.getEmail().toLowerCase().contains(searchKeyword) ||
                            employee.getAddress().toLowerCase().contains(searchKeyword) ||
                            employee.getLogin().toLowerCase().contains(searchKeyword)) {
                    } else {
                        return false;
                    }
                }

                if (departmentCB.getValue().getId() == 0) {

                } else {
                    if (departmentCB.getValue().getId() != employee.getDepartment().getId()) {
                        return false;
                    }
                }

                if (positionCB.getValue().getId() == 0) {

                } else {
                    if (positionCB.getValue().getId() != employee.getPosition().getId()) {
                        return false;
                    }
                }

                return true;

            });
        });

        sortedData = new SortedList<>(filteredData);

        //связываем отсортированные записи с таблицей
        sortedData.comparatorProperty().bind(employeeTable.comparatorProperty());

        employeeTable.setItems(sortedData);
    }

    /**
     * Заполняет все текстовые поля, отображая подробности об адресате.
     * Если указанный адресат = null, то все текстовые поля очищаются.
     *
     * @param employee — адресат типа Employee или null
     */
    private void showEmployeeDetails(Employee employee) {
        if (employee != null) {
            // Заполняем метки информацией из объекта
            numberLabel.setText(employee.getTelephoneNumber());
            emailLabel.setText(employee.getEmail());
            addressLabel.setText(employee.getAddress());
            departmentLabel.setText(employee.getDepartment().getTitle());
            positionLabel.setText(employee.getPosition().getTitle());
            isAdminLabel.setText(employee.getIsAdmin() ? "Да" : "Нет");
            loginLabel.setText(employee.getLogin());
        } else {
            // Если Person = null, то убираем весь текст.
            numberLabel.setText("");
            emailLabel.setText("");
            addressLabel.setText("");
            departmentLabel.setText("");
            positionLabel.setText("");
            isAdminLabel.setText("");
            loginLabel.setText("");
        }
    }

    public void updateTableView() throws PersistException {
        if (service == null) {
            throw new IllegalStateException("Service was null");
        }

        List<Employee> list = service.findAll();
        obsList = FXCollections.observableArrayList(list);
        employeeTable.setItems(obsList);

        initEditButtons();
        initRemoveButtons();

        addTextFilter();
        addDepartmentCBFilter();
        addPositionCBFilter();
    }

    @FXML
    public void onBtNewAction(ActionEvent event) {
        Stage parentStage = Util.currentStage(event);
        Employee obj = new Employee();
        createDialogForm(obj, "formEmployee.fxml", parentStage);
    }

    @FXML
    public void onBtUpdAction(ActionEvent event) {
        Stage parentStage = Util.currentStage(event);
        selectEmployee = employeeTable.getSelectionModel().getSelectedItem();
        if (selectEmployee != null) {
            createDialogForm(selectEmployee, "formEmployee.fxml", parentStage);
        } else {
            Alerts.showAlert("Сотрудник не выбран", "Не выбран сотрудник в таблице сотрудников", "Выберите существующего сотрудника", Alert.AlertType.ERROR);
        }
    }

    private void createDialogForm(Employee obj, String absoluteName, Stage parentStage) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(absoluteName));
            Pane pane = loader.load();

            EmployeeFormController controller = loader.getController();
            controller.setEmployee(obj);
            controller.setServices(new EmployeeService(), new DepartmentService(), new PositionService());
            controller.loadAssociatedObjects();
            controller.subscribeDataChangeListener(this);
            controller.updateFormData();
            if (obj.getId() == 0) {
                controller.setVisiblePasswordForm(true);
            }

            Stage dialogStage = new Stage();
            dialogStage.setTitle("");
            dialogStage.setScene(new Scene(pane));
            dialogStage.setResizable(false);
            dialogStage.initOwner(parentStage);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.showAndWait();
        } catch (IOException | PersistException e) {
            e.printStackTrace();
            Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void removeEmployee(Employee obj) {

        Optional<ButtonType> result = Alerts.showConfirmation("Подтверждение", "Вы действительно хотите удалить сотрудника?");

        if (result.get() == ButtonType.OK) {
            if (service == null) {
                throw new IllegalStateException("Service was null");
            }
            try {
                service.delete(obj);
                updateTableView();
            } catch (PersistException e) {
                Alerts.showAlert("Error removing object", null, e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void initEditButtons() {
        editColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        editColumn.setCellFactory(param -> new TableCell<Employee, Employee>() {
            private final Button button = new Button("Редактировать");

            @Override
            protected void updateItem(Employee obj, boolean empty) {
                super.updateItem(obj, empty);
                if (obj == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(
                        event -> createDialogForm(obj, "formEmployee.fxml", Util.currentStage(event)));
            }
        });
    }

    private void initRemoveButtons() {
        removeColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        removeColumn.setCellFactory(param -> new TableCell<Employee, Employee>() {
            private final Button button = new Button("Удалить");

            @Override
            protected void updateItem(Employee obj, boolean empty) {
                super.updateItem(obj, empty);
                if (obj == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(event -> removeEmployee(obj));
            }
        });
    }
}