package ser.bal.pyro;

import java.io.IOException;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class MainController {
	
	@FXML
	Button pyroCurrentButton;
	
	private Stage stage;
	private Scene scene;
	private Parent root;

	public void startPyro(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("PyroMainScene.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		stage = new Stage();
		stage.setResizable(false);
		stage.setTitle("Pyrocurrent Program v1.05");
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();

		try{
			stage.setOnCloseRequest(e -> {
				e.consume();
				loguot(stage);
			});

		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
	}

	public void loguot (Stage stage) {

		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Close window");
		alert.setHeaderText("Do you want to close this program");

		if(alert.showAndWait().get() == ButtonType.OK) {
			stage.close();
		}
	}
}
