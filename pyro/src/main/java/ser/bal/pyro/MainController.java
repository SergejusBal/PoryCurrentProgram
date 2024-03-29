package ser.bal.pyro;

import java.io.IOException;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
		stage.setTitle("Pyrocurrent Program v1.01");
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		
		
	}
}
