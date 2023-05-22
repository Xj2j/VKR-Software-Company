package org.openjfx;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.openjfx.controller.CostumerListController;
import org.openjfx.controller.MainController;
import org.openjfx.controller.ProfileController;
import org.openjfx.controller.ProjectListController;
import org.openjfx.model.PersistException;
import org.openjfx.model.services.ApplicationService;
import org.openjfx.model.services.StatusService;
import org.openjfx.util.Alerts;

import java.io.IOException;
import java.util.function.Consumer;

public class AdminMainController extends MainController {

    private Stage stage;
    private Scene scene;
    private Parent parent;

    @FXML
    private Tab applicationsTab;

    @FXML
    private Tab costumersTab;

    @FXML
    private Tab employeesTab;

    @FXML
    private Tab profileTab;

    @FXML
    private Tab projectsTab;

    @FXML
    private TabPane paneTabs;

    //AnchorPane anch;

    @FXML
    public void initialize() {


        applicationsTab.setOnSelectionChanged (e -> {
            try {
                FXMLLoader loader = new FXMLLoader(App.class.getResource("applications.fxml"));
                AnchorPane anch = loader.load();
                ApplicationListController applicationListController = loader.getController();
                applicationsTab.setContent(anch);
                if (applicationsTab.isSelected())
                                applicationListController.updateTableView();
            } catch (PersistException | IOException persistException) {
                persistException.printStackTrace();
            }
        });

        projectsTab.setOnSelectionChanged (e -> {
            try {
                FXMLLoader loader = new FXMLLoader(App.class.getResource("projects.fxml"));
                AnchorPane anch = loader.load();
                ProjectListController projectListController = loader.getController();
                projectsTab.setContent(anch);
                if (projectsTab.isSelected())
                    projectListController.updateTableView();
            } catch (PersistException | IOException persistException) {
                persistException.printStackTrace();
            }
        });

        employeesTab.setOnSelectionChanged (e -> {
            try {
                FXMLLoader loader = new FXMLLoader(App.class.getResource("employees.fxml"));
                AnchorPane anch = loader.load();
                EmployeeListController employeeListController = loader.getController();
                employeesTab.setContent(anch);
                if (employeesTab.isSelected())
                    employeeListController.updateTableView();
            } catch (PersistException | IOException persistException) {
                persistException.printStackTrace();
            }
        });

        costumersTab.setOnSelectionChanged (e -> {
            try {
                FXMLLoader loader = new FXMLLoader(App.class.getResource("costumers.fxml"));
                AnchorPane anch = loader.load();
                CostumerListController costumerListController = loader.getController();
                costumersTab.setContent(anch);
                if (costumersTab.isSelected())
                    costumerListController.updateTableView();
            } catch (PersistException | IOException persistException) {
                persistException.printStackTrace();
            }
        });

        /**profileTab.setOnSelectionChanged (e -> {
            try {
                FXMLLoader loader = new FXMLLoader(App.class.getResource("profile.fxml"));
                AnchorPane anch = loader.load();
                ProfileController profileController = loader.getController();
                profileTab.setContent(anch);
                if (profileTab.isSelected())
                    profileController.updateTableView();
            } catch (PersistException | IOException persistException) {
                persistException.printStackTrace();
            }
        });*/


        /**FXMLLoader loader = new FXMLLoader();

        AnchorPane anch1 = null;
        try {
            anch1 = loader.load(App.class.getResource("projects.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        projectsTab.setContent(anch1);

        /**loader = new FXMLLoader();

        AnchorPane anch2 = null;

         try {
             anch2 = loader.load(App.class.getResource("applications.fxml"));
             applicationListController = loader.getController();
             //controller.setServices(new ApplicationService(), new StatusService());
             //controller.updateTableView();

         } catch (IOException e) {
         e.printStackTrace();
         }
        applicationsTab.setContent(anch2);

        loadView("applications", (ApplicationListController controller) -> {
            try {
                controller.setServices(new ApplicationService(), new StatusService());
            } catch (PersistException e) {
                e.printStackTrace();
            }
            try {
                controller.updateTableView();
            } catch (PersistException e) {
                e.printStackTrace();
            }
        });

        loader = new FXMLLoader();

        AnchorPane anch3 = null;
        try {
            anch3 = loader.load(App.class.getResource("employees.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        employeesTab.setContent(anch3);


        loader = new FXMLLoader();

        AnchorPane anch4 = null;
        try {
            anch4 = loader.load(App.class.getResource("costumers.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        costumersTab.setContent(anch4);


        loader = new FXMLLoader();

        AnchorPane anch5 = null;
        try {
            anch5 = loader.load(App.class.getResource("profile.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        profileTab.setContent(anch5);*/


    }

    public AdminMainController (){
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("adminMain.fxml"));
        fxmlLoader.setController(this);
        try {
            parent = (Parent) fxmlLoader.load();
            // set height and width here for this home scene
            scene = new Scene(parent, 1839, 1079);
        } catch (IOException e) {
            // manage the exception
        }
    }

    public void displayAdminMainScreen(Stage stage){
        this.stage = stage;
        stage.setMinHeight(1079);
        stage.setMinWidth(1839);
        stage.setScene(scene);
        // Must write
        stage.hide();
        stage.show();
    }

    /**private synchronized void loadView(String fxml, Consumer<ApplicationListController> initializingAction) {
        try {
            FXMLLoader loader = new FXMLLoader();
            anch = loader.load(App.class.getResource("applications.fxml"));
            applicationsTab.setContent(anch);
            /**FXMLLoader Loader = new FXMLLoader(getClass().getResource(fxml + ".fxml"));
            Parent parent = Loader.load();
            anch = (AnchorPane) parent;
            ApplicationListController controller = Loader.getController();
            initializingAction.accept(controller);
            applicationsTab.setContent(anch);
        } catch (IOException e) {
            Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), Alert.AlertType.ERROR);
        }
    }*/

    /**@FXML
    void onSelect(ActionEvent event) throws PersistException {
        applicationListController.updateTableView();
    }*/

}
