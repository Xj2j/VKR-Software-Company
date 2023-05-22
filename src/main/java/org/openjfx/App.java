package org.openjfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.openjfx.model.dto.Employee;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    public static Employee loggedUser;

    /**private static Scene mainScene;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        //primaryStage.setTitle("Hello World");
        mainScene = new Scene(root, 573, 400);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }*/

    public static Scene getMainScene() {
        return mainScene;
    }

    /*
    public static void main(String[] args) {
        launch(args);
    }*/

    private static Scene mainScene;

    @Override
    public void start(Stage stage) throws Exception {
        //mainScene = new Scene(loadFXML("login"));
        /**scene.getStylesheets().add(App.class.getResource("styles.css").toExternalForm());*/
        //stage.setScene(mainScene);
        //stage.show();
        new LoginController().launchLoginScene(stage);
    }

    static <T> void setRoot(String fxml) throws IOException {
        mainScene.setRoot(loadFXML(fxml));

    }

    /**public Employee getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(Employee user) {
        loggedUser = user;
    }*/

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**private static <T> Parent loadFXML(String fxml, Consumer<T> initializingAction) throws IOException {
     FXMLLoader Loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
     Parent parent = Loader.load();
     T controller = Loader.getController();
     initializingAction.accept(controller);
     return Loader.load();
     }*/

    public static void main(String[] args) {
        launch();
    }



}