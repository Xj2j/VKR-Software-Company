package org.openjfx.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
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
import org.openjfx.model.dto.Costumer;
import org.openjfx.model.dto.Employee;
import org.openjfx.model.services.ApplicationService;
import org.openjfx.model.services.CostumerService;
import org.openjfx.util.Alerts;
import org.openjfx.util.Util;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class CostumerListController implements DataChangeListener {

    @FXML
    private Label addressLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private TableColumn<Costumer, Integer> idColumn;

    @FXML
    private TableColumn<Costumer, String> titleColumn;

    @FXML
    private Label numberLabel;

    @FXML
    private TextField searchTextField;

    @FXML
    private TableView<Costumer> costumerTable;

    @FXML
    private TableColumn<Costumer, Costumer> editColumn;

    @FXML
    private TableColumn<Costumer, Costumer> removeColumn;

    private App mainApp;

    private CostumerService service;

    private Costumer selectCostumer;

    private ObservableList<Costumer> obsList;

    private FilteredList<Costumer> filteredData;

    private SortedList<Costumer> sortedData;

    @FXML
    public void initialize() throws PersistException {

        try {
            setServices(new CostumerService());
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
                cellData -> cellData.getValue().nameProperty());

        // Очистка дополнительной информации об адресате.
        showApplicationDetails(null);

        // Слушаем изменения выбора, и при изменении отображаем
        // дополнительную информацию об адресате.
        costumerTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showApplicationDetails(newValue));

    }

    @Override
    public void onDataChanged() throws PersistException {
        updateTableView();
    }

    public void setServices(CostumerService service) {
        this.service = service;
    }

    private void addTextFilter() {

        filteredData = new FilteredList<>(obsList, t -> true);

        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(costumer -> {

                if (newValue == null || newValue.isEmpty()) {
                } else {
                    String searchKeyword = newValue.toLowerCase();
                    if (costumer.getName().toLowerCase().contains(searchKeyword) ||
                            costumer.getTelephoneNumber().toLowerCase().contains(searchKeyword) ||
                            costumer.getEmail().toLowerCase().contains(searchKeyword) ||
                            costumer.getAddress().toLowerCase().contains(searchKeyword)) {
                    } else {
                        return false;
                    }
                }

                return true;
            });
        });

        sortedData = new SortedList<>(filteredData);

        //связываем отсортированные записи с таблицей
        sortedData.comparatorProperty().bind(costumerTable.comparatorProperty());

        costumerTable.setItems(sortedData);
    }

    /**
     * Заполняет все текстовые поля, отображая подробности об адресате.
     * Если указанный адресат = null, то все текстовые поля очищаются.
     *
     * @param costumer — адресат типа Application или null
     */
    private void showApplicationDetails(Costumer costumer) {
        if (costumer != null) {
            // Заполняем метки информацией из объекта
            numberLabel.setText(costumer.getTelephoneNumber());
            emailLabel.setText(costumer.getEmail());
            addressLabel.setText(costumer.getAddress());
        } else {
            // Если costumer = null, то убираем весь текст.
            numberLabel.setText("");
            emailLabel.setText("");
            addressLabel.setText("");
        }
    }

    public void updateTableView() throws PersistException {
        if (service == null) {
            throw new IllegalStateException("Service was null");
        }

        List<Costumer> list = service.findAll();
        obsList = FXCollections.observableArrayList(list);
        costumerTable.setItems(obsList);

        initEditButtons();
        initRemoveButtons();

        addTextFilter();
    }

    @FXML
    public void onBtNewAction(ActionEvent event) {
        Stage parentStage = Util.currentStage(event);
        Costumer obj = new Costumer();
        createDialogForm(obj, "formCostumer.fxml", parentStage);
    }

    @FXML
    public void onBtUpdAction(ActionEvent event) {
        Stage parentStage = Util.currentStage(event);
        selectCostumer = costumerTable.getSelectionModel().getSelectedItem();
        if (selectCostumer != null) {
            createDialogForm(selectCostumer, "formCostumer.fxml", parentStage);
        } else {
            Alerts.showAlert("Заказчик не выбран", "Не выбран заказчик в таблице заказчиков", "Выберите существующего заказчика", Alert.AlertType.ERROR);
        }
    }

    private void createDialogForm(Costumer obj, String absoluteName, Stage parentStage) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(absoluteName));
            Pane pane = loader.load();

            CostumerFormController controller = loader.getController();
            controller.setCostumer(obj);
            controller.setServices(new CostumerService());
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

    public void removeCostumer(Costumer obj) {

        Optional<ButtonType> result = Alerts.showConfirmation("Подтверждение", "Вы действительно хотите удалить заказчика из базы?");

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
        editColumn.setCellFactory(param -> new TableCell<Costumer, Costumer>() {
            private final Button button = new Button("Редактировать");

            @Override
            protected void updateItem(Costumer obj, boolean empty) {
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
        removeColumn.setCellFactory(param -> new TableCell<Costumer, Costumer>() {
            private final Button button = new Button("Удалить");

            @Override
            protected void updateItem(Costumer obj, boolean empty) {
                super.updateItem(obj, empty);
                if (obj == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(event -> removeCostumer(obj));
            }
        });
    }
}