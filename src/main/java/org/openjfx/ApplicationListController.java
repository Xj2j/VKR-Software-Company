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
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.openjfx.controller.ApplicationFormController;
import org.openjfx.model.PersistException;
import org.openjfx.model.dto.*;
import org.openjfx.model.services.*;
import org.openjfx.util.Alerts;
import org.openjfx.util.Util;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ApplicationListController implements DataChangeListener, Initializable {

    @FXML
    private Button addBtn;

    @FXML
    private DatePicker datePicker1;

    @FXML
    private DatePicker datePicker2;

    @FXML
    private Button deleteBtn;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private TextField searchTextField;

    @FXML
    private Button updateBtn;

    @FXML
    private Label updateLabel;

    @FXML
    private TableColumn<Application, String> costumerColumn;

    @FXML
    private TableColumn<Application, LocalDate> dateOfApplicationColumn;

    @FXML
    private TableColumn<Application, LocalDate> dateOfCompletionColumn;

    @FXML
    private TableColumn<Application, Integer> idColumn;

    @FXML
    private TableColumn<Application, Integer> priceColumn;

    @FXML
    private ComboBox<Status> statusCB;

    @FXML
    private TableColumn<Application, String> statusColumn;

    @FXML
    private TableColumn<Application, String> titleColumn;

    @FXML
    private TableView<Application> applicationTable;

    @FXML
    private TableColumn<Application, Application> removeColumn;

    @FXML
    private TableColumn<Application, Application> editColumn;

    private App mainApp;

    private ApplicationService service;

    private Application selectApplication;

    private ObservableList<Application> obsList;

    private FilteredList<Application> filteredData;

    private SortedList<Application> sortedData;

    private StatusService statusService;

    private ObservableList<Status> obsListStat;


    @FXML
    public void initialize(URL url, ResourceBundle rb) {

        try {
            setServices(new ApplicationService(), new StatusService());
        } catch (PersistException e) {
            e.printStackTrace();
        }

        try {
            loadAssociatedObjects();
        } catch (PersistException e) {
            e.printStackTrace();
        }

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
        costumerColumn.setCellValueFactory(
                cellData -> cellData.getValue().getCostumer().nameProperty());
        dateOfApplicationColumn.setCellValueFactory(
                cellData -> cellData.getValue().dateOfApplicationProperty());
        dateOfCompletionColumn.setCellValueFactory(
                cellData -> cellData.getValue().dateOfCompletionProperty());
        priceColumn.setCellValueFactory(
                cellData -> cellData.getValue().priceProperty().asObject());
        statusColumn.setCellValueFactory(
                cellData -> cellData.getValue().getStatus().titleProperty());

        // Очистка дополнительной информации об адресате.
        showApplicationDetails(null);

        // Слушаем изменения выбора, и при изменении отображаем
        // дополнительную информацию об адресате.
        applicationTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showApplicationDetails(newValue));
    }

    @Override
    public void onDataChanged() throws PersistException {
        updateTableView();
    }

    public void setServices(ApplicationService service, StatusService statusService) {
        this.service = service;
        this.statusService = statusService;
    }

    public void loadAssociatedObjects() throws PersistException {
        if (statusService == null) {
            throw new IllegalStateException("statusService was null");
        }

        List<Status> listStat = statusService.findAll();
        obsListStat = FXCollections.observableArrayList(listStat);
        obsListStat.add(0, new Status(new SimpleIntegerProperty(0), new SimpleStringProperty("Все")));
        statusCB.setItems(obsListStat);
        statusCB.getSelectionModel().selectFirst();

    }

    public void addStatusCBFilter() {
        statusCB.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(application -> {

                if (checkStatus(application)) {
                } else {
                    return false;
                }

                if (checkText(application)) {
                } else {
                    return false;
                }

                if (checkDate(application)) {
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
            filteredData.setPredicate(application -> {

                if ( checkDate(application)) {
                } else {
                    return false;
                }

                if ( checkStatus(application)) {
                } else {
                    return false;
                }

                if (checkText(application)) {
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
            filteredData.setPredicate(application -> {

                if (checkDate(application)) {
                } else {
                    return false;
                }

                if (checkStatus(application)) {
                } else {
                    return false;
                }

                if (checkText(application)) {
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
            filteredData.setPredicate(application -> {

                if (checkText(application)) {
                } else {
                    return false;
                }

                if ( checkStatus(application)) {
                } else {
                    return false;
                }

                if (checkDate(application)) {
                } else {
                    return false;
                }

                return true;
            });
        });

        sortedData = new SortedList<>(filteredData);

        //связываем отсортированные записи с таблицей
        sortedData.comparatorProperty().bind(applicationTable.comparatorProperty());

        applicationTable.setItems(sortedData);
    }

    private boolean checkDate(Application obj) {
        LocalDate minDate = datePicker1.getValue();
        LocalDate maxDate = datePicker2.getValue();

        // get final values != null
        final LocalDate finalMin = minDate == null ? LocalDate.MIN : minDate;
        final LocalDate finalMax = maxDate == null ? LocalDate.MAX : maxDate;

        if ( !finalMin.isAfter(obj.getDateOfApplication()) && !finalMax.isBefore(obj.getDateOfApplication())) {

        } else {
            return false;
        }
        return true;
    }

    private boolean checkText(Application application) {
        //if (newValue == null || newValue.isEmpty()) {
        if (searchTextField.getText().trim().length() > 0) {
        } else {
            String searchKeyword = searchTextField.getText().trim().toLowerCase();
            if (application.getTitle().toLowerCase().contains(searchKeyword) ||
                    application.getCostumer().getName().toLowerCase().contains(searchKeyword) ||
                    Integer.toString(application.getPrice()).toLowerCase().contains(searchKeyword) ||
                    application.getStatus().getTitle().toLowerCase().contains(searchKeyword)) {
            } else {
                return false;
            }
        }
        return true;
    }

    private boolean checkStatus(Application application) {
        if (statusCB.getValue().getId() == 0) {

        } else {
            if (statusCB.getValue().getId() != application.getStatus().getId()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Заполняет все текстовые поля, отображая подробности об адресате.
     * Если указанный адресат = null, то все текстовые поля очищаются.
     *
     * @param application — адресат типа Application или null
     */
    private void showApplicationDetails(Application application) {
        if (application != null) {
            // Заполняем метки информацией из объекта
            descriptionTextArea.setText(application.getDescription());
        } else {
            // Если Person = null, то убираем весь текст.
            descriptionTextArea.setText("");
        }
    }

    public void updateTableView() throws PersistException {
        if (service == null) {
            throw new IllegalStateException("Service was null");
        }

        List<Application> list = service.findAll();
        obsList = FXCollections.observableArrayList(list);
        applicationTable.setItems(obsList);

        initEditButtons();
        initRemoveButtons();

        addTextFilter();
        addStatusCBFilter();
        addDateFilter1();
        addDateFilter2();
    }

    @FXML
    public void onBtNewAction(ActionEvent event) {
        Stage parentStage = Util.currentStage(event);
        Application obj = new Application();
        createDialogForm(obj, "formApplication.fxml", parentStage);
    }

    @FXML
    public void onBtUpdAction(ActionEvent event) {
        Stage parentStage = Util.currentStage(event);
        selectApplication = applicationTable.getSelectionModel().getSelectedItem();
        if (selectApplication != null) {
            createDialogForm(selectApplication, "formApplication.fxml", parentStage);
        } else {
            Alerts.showAlert("Заявка не выбрана", "Не выбрана заявка в таблице заявок", "Выберите существующую заявку", Alert.AlertType.ERROR);
        }
    }

    private void createDialogForm(Application obj, String absoluteName, Stage parentStage) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(absoluteName));
            Pane pane = loader.load();

            ApplicationFormController controller = loader.getController();
            controller.setApplication(obj);
            controller.setServices(new ApplicationService(), new CostumerService(), new StatusService());
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
        Application obj = applicationTable.getSelectionModel().getSelectedItem();

        Optional<ButtonType> result = Alerts.showConfirmation("Подтверждение", "Вы действительно хотите удалить заявку?");

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

    public void removeApplication(Application application) {
        Application obj = applicationTable.getSelectionModel().getSelectedItem();

        Optional<ButtonType> result = Alerts.showConfirmation("Подтверждение", "Вы действительно хотите удалить заявку?");

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
        editColumn.setCellFactory(param -> new TableCell<Application, Application>() {
            private final Button button = new Button("Редактировать");

            @Override
            protected void updateItem(Application obj, boolean empty) {
                super.updateItem(obj, empty);
                if (obj == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(
                        event -> createDialogForm(obj, "formApplication.fxml", Util.currentStage(event)));
            }
        });
    }

    private void initRemoveButtons() {
        removeColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        removeColumn.setCellFactory(param -> new TableCell<Application, Application>() {
            private final Button button = new Button("Удалить");

            @Override
            protected void updateItem(Application obj, boolean empty) {
                super.updateItem(obj, empty);
                if (obj == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(event -> removeApplication(obj));
            }
        });
    }

}