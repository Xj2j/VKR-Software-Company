package org.openjfx.controller;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainController {

    private Stage stage;
    private Scene scene;

    public void displayMainScreen(Stage stage){
        this.stage = stage;
        stage.setMinHeight(760);
        stage.setMinWidth(1120);
        stage.setScene(scene);
        stage.hide();
        stage.show();
    }

}
