package main.java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import main.java.controller.MartaController;
import main.java.controller.Login;
import main.java.model.User;
import main.java.util.Database;

public class MartaApp extends Application {
	FXMLLoader fxmlLoader;
	Scene scene;
	Stage stage;
	public User user;
	public static MartaApp app;

	@Override
	public void start(Stage stage) throws Exception {
		app = this;
		fxmlLoader = new FXMLLoader();
	    this.stage = stage;
	    Database.setApp(this);
	    loadController(new Login(null));
	    stage.setResizable(false);
	    stage.show();
	}

	public void loadController(MartaController controller) {
		if(scene == null) {
			scene = new Scene(controller, 800, 500);
		} else {
			scene.setRoot(controller);
		}

		stage.setTitle(controller.getTitle());
		stage.setScene(scene);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User appUser) {
		user = appUser;
	}

	public static void main(String[] args) {
	    launch(args);
	}

	public void logError(String caller, String error) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("ERROR");
		alert.setHeaderText("'"+ caller+"' has encountered an error:");
		alert.setContentText(error);
		alert.showAndWait();
	}

}
