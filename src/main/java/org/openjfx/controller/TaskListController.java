package org.openjfx.controller;

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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskListController implements DataChangeListener {

    @FXML
    private DatePicker datePicker1;

    @FXML
    private DatePicker datePicker2;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private TextArea reportTextArea;

    @FXML
    private TableColumn<Task, Task> editColumn;

    @FXML
    private Label headLabel;

    @FXML
    private TableColumn<Task, Integer> idColumn;

    @FXML
    private Label newTasksLabel;

    @FXML
    private Label overdueTasksLabel;

    @FXML
    private Label projectTitleLabel;

    @FXML
    private TableColumn<Task, Task> removeColumn;

    @FXML
    private TextField searchTextField;

    @FXML
    private ComboBox<Status> statusCB;

    @FXML
    private TableColumn<Task, String> statusColumn;

    @FXML
    private Label statusLabel;

    @FXML
    private TableColumn<Task, String> titleColumn;

    @FXML
    private TableColumn<Task, LocalDate> dateOfCompletionColumn;

    @FXML
    private TableColumn<Task, LocalDate> dateOfLastColumn;

    @FXML
    private TableColumn<Task, LocalDate> dateOfStartColumn;

    @FXML
    private Label workTasksLabel;

    @FXML
    private TableView<Task> taskTable;

    private App mainApp;

    private TaskService service;

    private Task selectTask;

    private ObservableList<Task> obsList;

    private FilteredList<Task> filteredData;

    private SortedList<Task> sortedData;

    private StatusService statusService;

    private ObservableList<Status> obsListStatuses;

    private ProjectService projectService;

    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();


    @FXML
    public void initialize() throws PersistException {

        //idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        //nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        try {
            setServices(new TaskService(), new ProjectService(), new StatusService());
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
        titleColumn.setCellValueFactory(
                cellData -> cellData.getValue().titleProperty());
        statusColumn.setCellValueFactory(
                cellData -> cellData.getValue().getStatus().titleProperty());
        dateOfStartColumn.setCellValueFactory(
                cellData -> cellData.getValue().dateOfStartProperty());
        dateOfLastColumn.setCellValueFactory(
                cellData -> cellData.getValue().dateOfLastProperty());
        dateOfCompletionColumn.setCellValueFactory(
                cellData -> cellData.getValue().dateOfCompletionProperty());

        // Очистка дополнительной информации об адресате.
        showTaskDetails(null);

        // Слушаем изменения выбора, и при изменении отображаем
        // дополнительную информацию об адресате.
        taskTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    try {
                        showTaskDetails(newValue);
                    } catch (PersistException e) {
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void onDataChanged() throws PersistException {
        updateTableView();
    }

    public void setServices(TaskService service, ProjectService projectService, StatusService statusService) {
        this.service = service;
        this.projectService = projectService;
        this.statusService = statusService;
    }

    public void loadAssociatedObjects() throws PersistException {
        if (statusService == null) {
            throw new IllegalStateException("statusService was null");
        }
        List<Status> listDep = statusService.findAll();
        obsListStatuses = FXCollections.observableArrayList(listDep);
        obsListStatuses.add(0, new Status(new SimpleIntegerProperty(0), new SimpleStringProperty("Все")));
        statusCB.setItems(obsListStatuses);
        statusCB.getSelectionModel().selectFirst();

    }

    @FXML
    void onBtnGetStarted(ActionEvent event) {
        selectTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectTask.getStatus().getId() == obsListStatuses.get(1).getId()) {
            Optional<ButtonType> result = Alerts.showConfirmation("Подтверждение", "Вы хотите принять задачу?");

            if (result.get() == ButtonType.OK) {
                if (service == null) {
                    throw new IllegalStateException("Service was null");
                }
                try {
                    selectTask.setStatus(obsListStatuses.get(2));
                    selectTask.setDateOfCompletion(LocalDate.now());
                    service.saveOrUpdate(selectTask);
                    updateTableView();
                } catch (PersistException e) {
                    Alerts.showAlert("Error finish object", null, e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        } else {
            Alerts.showAlert("Задача не доступна", "", "Принять можно только новую задачу", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onBtnFinish(ActionEvent event) {
        selectTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectTask.getStatus().getId() == obsListStatuses.get(2).getId()) {
            Optional<ButtonType> result = Alerts.showConfirmation("Подтверждение", "Вы хотите завершить задачу?");

            if (result.get() == ButtonType.OK) {
                if (service == null) {
                    throw new IllegalStateException("Service was null");
                }
                try {
                    selectTask.setStatus(obsListStatuses.get(3));
                    selectTask.setDateOfCompletion(LocalDate.now());
                    service.saveOrUpdate(selectTask);
                    updateTableView();
                } catch (PersistException e) {
                    Alerts.showAlert("Error finish object", null, e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        } else {
            Alerts.showAlert("Задача не доступна", "", "Завершить можно задачу над которой вы работаете", Alert.AlertType.ERROR);
        }
    }

    public void addStatusCBFilter() {
        statusCB.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(task -> {

                if (statusCB.getValue().getId() == 0) {

                } else {
                    if (statusCB.getValue().getId() != task.getStatus().getId()) {
                        return false;
                    }
                }

                if (searchTextField.getText().trim().length() > 0) {
                    String searchKeyword = searchTextField.getText().trim().toLowerCase();
                    if (task.getTitle().toLowerCase().contains(searchKeyword)) {
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
            filteredData.setPredicate(task -> {

                if (newValue == null || newValue.isEmpty()) {
                } else {
                    String searchKeyword = newValue.toLowerCase();
                    if (task.getTitle().toLowerCase().contains(searchKeyword)){
                    } else {
                        return false;
                    }
                }

                if (statusCB.getValue().getId() == 0) {

                } else {
                    if (statusCB.getValue().getId() != task.getStatus().getId()) {
                        return false;
                    }
                }
                return true;
            });
        });

        sortedData = new SortedList<>(filteredData);

        //связываем отсортированные записи с таблицей
        sortedData.comparatorProperty().bind(taskTable.comparatorProperty());

        taskTable.setItems(sortedData);
    }

    /**
     * Заполняет все текстовые поля, отображая подробности об адресате.
     * Если указанный адресат = null, то все текстовые поля очищаются.
     *
     * @param task — адресат типа Employee или null
     */
    private void showTaskDetails(Task task) throws PersistException {
        if (task != null) {
            // Заполняем метки информацией из объекта
            projectTitleLabel.setText(task.getProject().getTitle());
            headLabel.setText(projectService.findOne(task.getProject().getId()).getHead().getName());
            descriptionTextArea.setText(task.getDescription());
            reportTextArea.setText(task.getReport());
        } else {
            // Если Person = null, то убираем весь текст.
            projectTitleLabel.setText("");
            headLabel.setText("");
            descriptionTextArea.setText("");
            reportTextArea.setText("");
        }
    }

    public void updateTableView() throws PersistException {
        if (service == null) {
            throw new IllegalStateException("Service was null");
        }

        List<Task> list = service.findAll();
        obsList = FXCollections.observableArrayList(list);
        taskTable.setItems(obsList);

        updateTasksInfo();

        addTextFilter();
        addStatusCBFilter();

        initEditButtons();
    }

    public void updateTasksInfo() throws PersistException {
        if (service == null) {
            throw new IllegalStateException("Service was null");
        }
        //newTasksLabel.setText(Integer.toString(service.getCountTasks(1, App.loggedUser)));
        //workTasksLabel.setText(Integer.toString(service.getCountTasks(2, App.loggedUser)));
        //overdueTasksLabel.setText(Integer.toString(service.getCountTasks(2, App.loggedUser)));
        List<Integer> countList = service.getCountTasks(App.loggedUser);
        newTasksLabel.setText(Integer.toString(countList.get(0)));
        workTasksLabel.setText(Integer.toString(countList.get(1)));
        overdueTasksLabel.setText(Integer.toString(countList.get(2)));
    }


    @FXML
    public void onBtUpdAction(ActionEvent event) {
        Stage parentStage = Util.currentStage(event);
        selectTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectTask != null) {
            createDialogForm(selectTask, "formTaskForEmp.fxml", parentStage);
        } else {
            Alerts.showAlert("Задача не выбрана", "Не выбрана задача в таблице задач", "Выберите существующую задачу", Alert.AlertType.ERROR);
        }
    }

    private void createDialogForm(Task obj, String absoluteName, Stage parentStage) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(absoluteName));
            Pane pane = loader.load();

            TaskFormController controller = loader.getController();
            controller.setTask(obj);
            controller.setServices(new TaskService(), new ProjectService(), new StatusService());
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

    //if (taskTable.getSelectionModel().getSelectedItem().getStatus().getId() == obsListStatuses.get(2).getId()

    private void initEditButtons() {
        editColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        editColumn.setCellFactory(param -> new TableCell<Task, Task>() {
            private final Button button = new Button("Редактировать");


            @Override
            protected void updateItem(Task obj, boolean empty) {
                super.updateItem(obj, empty);
                if (obj == null) {
                    setGraphic(null);
                    return;
                }
                if (obj.getStatus().getId() == 3) button.setDisable(true);
                setGraphic(button);
                button.setOnAction(
                        event ->  createDialogForm(obj, "formTaskForEmp.fxml", Util.currentStage(event))
                );
            }
        });
    }

}