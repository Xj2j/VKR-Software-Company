package org.openjfx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.openjfx.controller.MainController;

import java.io.IOException;

public class EmployeeMainController extends MainController {

    private Stage stage;
    private Scene scene;
    private Parent parent;

    @FXML
    private Tab tasksTab;

    @FXML
    private Tab profileTab;

    @FXML
    public void initialize() {

        FXMLLoader loader = new FXMLLoader();

        AnchorPane anch1 = null;
        try {
            anch1 = loader.load(App.class.getResource("tasks.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        tasksTab.setContent(anch1);


        loader = new FXMLLoader();

        AnchorPane anch2 = null;
        try {
            anch2 = loader.load(App.class.getResource("profile.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        profileTab.setContent(anch2);
    }

    public EmployeeMainController (){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("employeeMain.fxml"));
        fxmlLoader.setController(this);
        try {
            parent = (Parent) fxmlLoader.load();
            // set height and width here for this home scene
            scene = new Scene(parent, 1839, 1079);
        } catch (IOException e) {
            // manage the exception
        }
    }

    public void displayEmployeeMainScreen(Stage stage){
        this.stage = stage;
        stage.setMinHeight(1079);
        stage.setMinWidth(1839);
        stage.setScene(scene);
        // Must write
        stage.hide();
        stage.show();
    }

}
