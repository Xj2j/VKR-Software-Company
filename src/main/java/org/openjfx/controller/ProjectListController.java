package org.openjfx.controller;

import javafx.beans.binding.Bindings;
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
import org.openjfx.App;
import org.openjfx.DataChangeListener;
import org.openjfx.model.PersistException;
import org.openjfx.model.dto.*;
import org.openjfx.model.services.*;
import org.openjfx.util.Alerts;
import org.openjfx.util.Util;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

public class ProjectListController implements DataChangeListener {

    @FXML
    private DatePicker datePicker1;

    @FXML
    private DatePicker datePicker2;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private TableColumn<Project, Integer> idColumn;

    @FXML
    private TableView<Project> projectTable;

    @FXML
    private TextField searchTextField;

    @FXML
    private TableView<Task> taskTable;

    @FXML
    private TableColumn<Project, String> titleColumn;

    @FXML
    private TableColumn<Project, LocalDate> dateOfApplicationColumn;

    @FXML
    private TableColumn<Project, LocalDate> dateOfCompletionColumn;

    @FXML
    private TableColumn<Project, Project> editColumn;

    @FXML
    private TableColumn<Project, Project> removeColumn;

    @FXML
    private TableColumn<Task, Integer> idColumnTaskTable;

    @FXML
    private TableColumn<Task, String> titleColumnTaskTable;

    @FXML
    private TableColumn<Task, String> statusColumnTaskTable;

    @FXML
    private TableColumn<Task, Task> editColumnTaskTable;

    @FXML
    private TableColumn<Task, Task> removeColumnTaskTable;

    @FXML
    private ComboBox<Status> statusCB;

    @FXML
    private TableColumn<Project, String> statusColumn;

    @FXML
    private TableColumn<Task, LocalDate> dateOfLastColumn;

    @FXML
    private TableColumn<Task, LocalDate> dateOfStartColumn;

    @FXML
    private Label workTasksLabel;

    @FXML
    private Label dateOfCompletionTaskLabel;

    @FXML
    private TextArea reportTextArea;

    @FXML
    private Button btnNew;

    @FXML
    private Button btnTaskNew;


    private App mainApp;

    private ProjectService service;

    private Project selectProject;

    private ObservableList<Project> obsList;

    private FilteredList<Project> filteredData;

    private SortedList<Project> sortedData;

    private EmployeeService employeeService;

    private StatusService statusService;

    private TaskService taskService;

    private ObservableList<Employee> obsListEmp;

    private ObservableList<Status> obsListStat;

    private ObservableList<Task> obsListTask;

    @FXML
    public void initialize() throws PersistException {

        getClass().getClassLoader().getResource("style.css").toExternalForm();

        try {
            setServices(new ProjectService(), new EmployeeService(), new StatusService(), new TaskService());
        } catch (PersistException e) {
            e.printStackTrace();
        }

        loadAssociatedObjects();

        try {
            updateTableView();
        } catch (PersistException e) {
            e.printStackTrace();
        }

        initializeProjectTable();

        initializeTaskTable();
    }

    public void initializeProjectTable() {
        idColumn.setCellValueFactory(
                cellData -> cellData.getValue().idProperty().asObject());
        titleColumn.setCellValueFactory(
                cellData -> cellData.getValue().titleProperty());
        dateOfApplicationColumn.setCellValueFactory(
                cellData -> cellData.getValue().dateOfStartProperty());
        dateOfCompletionColumn.setCellValueFactory(
                cellData -> cellData.getValue().dateOfCompletionProperty());
        statusColumn.setCellValueFactory(
                cellData -> cellData.getValue().getStatus().titleProperty());

        // Очистка дополнительной информации об адресате.
        showProjectDetails(null);

        // Слушаем изменения выбора, и при изменении отображаем
        // дополнительную информацию об адресате.
        projectTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showProjectDetails(newValue));

        projectTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    try {
                        showTaskTable(newValue);
                    } catch (PersistException e) {
                        e.printStackTrace();
                    }
                });
    }

    public void initializeTaskTable() {
        idColumnTaskTable.setCellValueFactory(
                cellData -> cellData.getValue().idProperty().asObject());
        titleColumnTaskTable.setCellValueFactory(
                cellData -> cellData.getValue().titleProperty());
        statusColumnTaskTable.setCellValueFactory(
                cellData -> cellData.getValue().getStatus().titleProperty());
        dateOfStartColumn.setCellValueFactory(
                cellData -> cellData.getValue().dateOfStartProperty());
        dateOfLastColumn.setCellValueFactory(
                cellData -> cellData.getValue().dateOfLastProperty());

        showTaskDetails(null);

        taskTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showTaskDetails(newValue));
    }

    @Override
    public void onDataChanged() throws PersistException {
        updateTableView();
    }

    public void setServices(ProjectService service, EmployeeService employeeService, StatusService statusService, TaskService taskService) {
        this.service = service;
        this.employeeService = employeeService;
        this.statusService = statusService;
        this.taskService = taskService;
    }

    public void loadAssociatedObjects() throws PersistException {

        List<Status> listStat = statusService.findAll();
        obsListStat = FXCollections.observableArrayList(listStat);
        obsListStat.add(0, new Status(new SimpleIntegerProperty(0), new SimpleStringProperty("Все")));
        statusCB.setItems(obsListStat);
        statusCB.getSelectionModel().selectFirst();

    }

    public void addStatusCBFilter() {
        statusCB.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(project -> {

                if (checkStatus(project)) {
                } else {
                    return false;
                }

                if (checkText(project)) {
                } else {
                    return false;
                }

                if (checkDate(project)) {
                } else {
                    return false;
                }

                return true;
            });
            sortedData = new SortedList<>(filteredData);
        });
    }

    /**public void addDateFilter2() {
     filteredData.predicateProperty().bind(Bindings.createObjectBinding(() -> {
     LocalDate minDate = datePicker1.getValue();
     LocalDate maxDate = datePicker2.getValue();

     // get final values != null
     final LocalDate finalMin = minDate == null ? LocalDate.MIN : minDate;
     final LocalDate finalMax = maxDate == null ? LocalDate.MAX : maxDate;

     // values for openDate need to be in the interval [finalMin, finalMax]
     return ti -> !finalMin.isAfter(ti.getDateOfApplication()) && !finalMax.isBefore(ti.getDateOfCompletion());
     },
     datePicker1.valueProperty(),
     datePicker2.valueProperty()));

     applicationTable.setItems(filteredData);
     }*/

    public void addDateFilter1() {
        datePicker1.valueProperty().addListener((observable, oldDate, newDate)->{
            filteredData.setPredicate(project -> {

                if ( checkDate(project)) {
                } else {
                    return false;
                }

                if ( checkStatus(project)) {
                } else {
                    return false;
                }

                if (checkText(project)) {
                } else {
                    return false;
                }

                return true;
            });
            sortedData = new SortedList<>(filteredData);
        });
    }

    public void addDateFilter2() {
        datePicker2.valueProperty().addListener((observable, oldDate, newDate)->{
            filteredData.setPredicate(project -> {

                if (checkDate(project)) {
                } else {
                    return false;
                }

                if (checkStatus(project)) {
                } else {
                    return false;
                }

                if (checkText(project)) {
                } else {
                    return false;
                }

                return true;
            });
            sortedData = new SortedList<>(filteredData);
        });
    }

    private void addTextFilter() {

        filteredData = new FilteredList<>(obsList, t -> true);

        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(project -> {

                if (checkText(project)) {
                } else {
                    return false;
                }

                if ( checkStatus(project)) {
                } else {
                    return false;
                }

                if (checkDate(project)) {
                } else {
                    return false;
                }

                return true;
            });
        });

        sortedData = new SortedList<>(filteredData);

        //связываем отсортированные записи с таблицей
        sortedData.comparatorProperty().bind(projectTable.comparatorProperty());

        projectTable.setItems(sortedData);
    }

    private boolean checkDate(Project obj) {
        LocalDate minDate = datePicker1.getValue();
        LocalDate maxDate = datePicker2.getValue();

        // get final values != null
        final LocalDate finalMin = minDate == null ? LocalDate.MIN : minDate;
        final LocalDate finalMax = maxDate == null ? LocalDate.MAX : maxDate;

        if ( !finalMin.isAfter(obj.getDateOfStart()) && !finalMax.isBefore(obj.getDateOfStart())) {

        } else {
            return false;
        }
        return true;
    }

    private boolean checkText(Project project) {
        //if (newValue == null || newValue.isEmpty()) {
        if (searchTextField.getText().trim().length() > 0) {
        } else {
            String searchKeyword = searchTextField.getText().trim().toLowerCase();
            if (project.getTitle().toLowerCase().contains(searchKeyword)) {
            } else {
                return false;
            }
        }
        return true;
    }

    private boolean checkStatus(Project project) {
        if (statusCB.getValue().getId() == 0) {

        } else {
            if (statusCB.getValue().getId() != project.getStatus().getId()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Заполняет все текстовые поля, отображая подробности об адресате.
     * Если указанный адресат = null, то все текстовые поля очищаются.
     *
     * @param project — адресат типа Application или null
     */
    private void showProjectDetails(Project project) {
        if (project != null) {
            // Заполняем метки информацией из объекта
            descriptionTextArea.setText(project.getDescription());
        } else {
            // Если Person = null, то убираем весь текст.
            descriptionTextArea.setText("");
        }
    }

    /**
     * Заполняет все текстовые поля, отображая подробности об адресате.
     * Если указанный адресат = null, то все текстовые поля очищаются.
     *
     * @param task — адресат типа Application или null
     */
    private void showTaskDetails(Task task) {
        if (task != null) {
            // Заполняем метки информацией из объекта
            reportTextArea.setText(task.getReport());
            dateOfCompletionTaskLabel.setText(Util.localDateToString(task.getDateOfCompletion()));
        } else {
            // Если Person = null, то убираем весь текст.
            reportTextArea.setText("");
            dateOfCompletionTaskLabel.setText("");
        }
    }

    private void showTaskTable(Project project) throws PersistException {
        if (project != null) {
            // Заполняем метки информацией из объекта
            updateTaskTableView(project);
        }
    }

    public void updateTaskTableView(Project project) throws PersistException {
        if (service == null) {
            throw new IllegalStateException("Service was null");
        }

        List<Task> list = taskService.findByProject(project);
        obsListTask = FXCollections.observableArrayList(list);
        taskTable.setItems(obsListTask);

        initTaskEditButtons();
        initTaskRemoveButtons();
    }

    public void updateTableView() throws PersistException {
        if (service == null) {
            throw new IllegalStateException("Service was null");
        }

        List<Project> list = service.findAll();
        obsList = FXCollections.observableArrayList(list);
        projectTable.setItems(obsList);

        initProjectEditButtons();
        initProjectRemoveButtons();

        addTextFilter();
        addStatusCBFilter();
        addDateFilter1();
        addDateFilter2();
    }

    @FXML
    public void onBtNewAction(ActionEvent event) {
        Stage parentStage = Util.currentStage(event);
        Project obj = new Project();
        createDialogFormForProject(obj, "formProject.fxml", parentStage);
    }


    @FXML
    public void onTaskBtNewAction(ActionEvent event) {
        Stage parentStage = Util.currentStage(event);
        Task obj = new Task();
        selectProject = projectTable.getSelectionModel().getSelectedItem();
        if (selectProject != null) {
            createDialogFormForTask(selectProject, obj, "formTask.fxml", parentStage);
        } else {
            Alerts.showAlert("Заявка не выбрана", "Не выбрана заявка в таблице заявок", "Выберите существующую заявку", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void onBtUpdAction(ActionEvent event) {
        Stage parentStage = Util.currentStage(event);
        selectProject = projectTable.getSelectionModel().getSelectedItem();
        if (selectProject != null) {
            createDialogFormForProject(selectProject, "formProject.fxml", parentStage);
        } else {
            Alerts.showAlert("Проект не выбран", "Не выбран проект в таблице заявок", "Выберите существующий проект", Alert.AlertType.ERROR);
        }
    }

    private void createDialogFormForProject(Project obj, String absoluteName, Stage parentStage) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(absoluteName));
            Pane pane = loader.load();

            ProjectFormController controller = loader.getController();
            controller.setProject(obj);
            controller.setServices(new ProjectService(), new ApplicationService(), new StatusService());
            controller.loadAssociatedObjects();
            controller.subscribeDataChangeListener(this);
            controller.updateFormData();

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

    private void createDialogFormForTask(Project selectProject, Task obj, String absoluteName, Stage parentStage) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(absoluteName));
            Pane pane = loader.load();

            TaskFormController controller = loader.getController();
            controller.setTask(obj);
            controller.setProject(selectProject);
            controller.setServices(new TaskService(), new EmployeeService(), new StatusService());
            controller.loadAssociatedObjects();
            controller.subscribeDataChangeListener(this);
            controller.updateFormData();

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

    @FXML
    public void onBtDelAction(ActionEvent event) {
        Project obj = projectTable.getSelectionModel().getSelectedItem();

        Optional<ButtonType> result = Alerts.showConfirmation("Подтверждение", "Вы действительно хотите удалить проект?");

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

    public void removeProject(Project project) {
        //Project obj = projectTable.getSelectionModel().getSelectedItem();

        Optional<ButtonType> result = Alerts.showConfirmation("Подтверждение", "Вы действительно хотите удалить проект?");

        if (result.get() == ButtonType.OK) {
            if (service == null) {
                throw new IllegalStateException("Service was null");
            }
            try {
                service.delete(project);
                updateTableView();
            } catch (PersistException e) {
                Alerts.showAlert("Error removing object", null, e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    public void removeTask(Task task) {
        //Task obj = taskTable.getSelectionModel().getSelectedItem();

        Optional<ButtonType> result = Alerts.showConfirmation("Подтверждение", "Вы действительно хотите удалить задачу?");

        if (result.get() == ButtonType.OK) {
            if (taskService == null) {
                throw new IllegalStateException("Service was null");
            }
            try {
                taskService.delete(task);
                updateTaskTableView(projectTable.getSelectionModel().getSelectedItem());
            } catch (PersistException e) {
                Alerts.showAlert("Error removing object", null, e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void initProjectEditButtons() {
        editColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        editColumn.setCellFactory(param -> new TableCell<Project, Project>() {
            private final Button button = new Button("Редактировать");

            @Override
            protected void updateItem(Project obj, boolean empty) {
                super.updateItem(obj, empty);
                if (obj == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(
                        event -> createDialogFormForProject(obj, "formProject.fxml", Util.currentStage(event)));
            }
        });
    }

    private void initProjectRemoveButtons() {
        removeColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        removeColumn.setCellFactory(param -> new TableCell<Project, Project>() {
            private final Button button = new Button("Удалить");

            @Override
            protected void updateItem(Project obj, boolean empty) {
                super.updateItem(obj, empty);
                if (obj == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(event -> removeProject(obj));
            }
        });
    }

    private void initTaskEditButtons() {
        editColumnTaskTable.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        editColumnTaskTable.setCellFactory(param -> new TableCell<Task, Task>() {
            private final Button button = new Button("Редактировать");

            @Override
            protected void updateItem(Task obj, boolean empty) {
                super.updateItem(obj, empty);
                if (obj == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(
                        event -> createDialogFormForTask(projectTable.getSelectionModel().getSelectedItem(), obj, "formTask.fxml", Util.currentStage(event)));
            }
        });
    }

    private void initTaskRemoveButtons() {
        removeColumnTaskTable.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        removeColumnTaskTable.setCellFactory(param -> new TableCell<Task, Task>() {
            private final Button button = new Button("Удалить");

            @Override
            protected void updateItem(Task obj, boolean empty) {
                super.updateItem(obj, empty);
                if (obj == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(event -> removeTask(obj));
            }
        });
    }

}
