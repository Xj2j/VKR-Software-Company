package org.openjfx;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.openjfx.model.services.EmployeeService;
import java.io.IOException;

public class LoginController {

    private Stage stage;
    private Scene scene;
    private Parent parent;
    private App mainApp;
    @FXML
    private Button cancelButton;
    @FXML
    private Label loginMessageLabel;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordPasswordField;

    private EmployeeService employeeService;

    public void loginButtonOnAction(ActionEvent e) throws Exception {

        if (usernameTextField.getText().isBlank() == false && passwordPasswordField.getText().isBlank() == false) {
            validateLogin(usernameTextField.getText(), passwordPasswordField.getText());
        } else {
            loginMessageLabel.setText("Введите логин и пароль");
        }

    }

    public void cancelButtonOnAction(ActionEvent e) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
    }

    public void validateLogin(String login, String password) throws Exception {

        employeeService = new EmployeeService();

        mainApp.loggedUser = employeeService.getLoggedUser(login, password);

        if (mainApp.loggedUser != null) {
            if (mainApp.loggedUser.getIsAdmin()) {
                loginMessageLabel.setText("Добро пожаловать");
                new AdminMainController().displayAdminMainScreen(stage);
            }
            else {
                loginMessageLabel.setText("Добро пожаловать");
                new EmployeeMainController().displayEmployeeMainScreen(stage);
            }
        } else {
            loginMessageLabel.setText("Неправильный логин или пароль");
        }
    }

    //private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) {
        //try {

            /**mainScene = new Scene(loadFXML("login"));
            //scene.getStylesheets().add(App.class.getResource("styles.css").toExternalForm());
            stage.setScene(mainScene);
            stage.show();*/

            //FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));

            //AnchorPane newVBox = loader.load();

            //Scene mainScene = App.getMainScene();
            //AnchorPane mainPane = (AnchorPane) ((ScrollPane) mainScene.getRoot()).getContent();

            //Node mainMenu = mainPane.getChildren().get(0);
            //mainPane.getChildren().clear();
            //mainPane.getChildren().add(mainMenu);
            //mainPane.getChildren().addAll(newVBox.getChildren());

            //T controller = loader.getController();
            //initializingAction.accept(controller);
        //}
        //catch (IOException e) {
           // Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), Alert.AlertType.ERROR);
       // }
   // }*/

    public LoginController() throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        fxmlLoader.setController(this);
        try {
            parent = (Parent) fxmlLoader.load();
            // set height and width here for this login scene
            scene = new Scene(parent, 574, 400);
        } catch (IOException ex) {
            System.out.println("Error displaying login window");
            throw new RuntimeException(ex);
        }
    }

    // create a launcher method for this. Here I am going to take like below--
    public void launchLoginScene(Stage stage) {
        this.stage = stage;
        stage.setScene(scene);
        stage.setResizable(true);

        stage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                setCurrentWidthToStage(number2);
            }
        });

        stage.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                setCurrentHeightToStage(number2);
            }
        });

        //Don't forget to add below code in every controller
        stage.hide();
        stage.show();

    }

    private void setCurrentWidthToStage(Number number2) {
        stage.setWidth((double) number2);
    }

    private void setCurrentHeightToStage(Number number2) {
        stage.setHeight((double) number2);
    }

}
