package ser.bal.pyro;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;


import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage primaryStage) throws IOException {
    	try {
    		
    	Parent root = FXMLLoader.load(getClass().getResource("Launcher.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Pyrocurrent Program v1.05");
		primaryStage.setResizable(false);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("pyroImage.png")));
		primaryStage.show();
		
	//___________________________________
		//Logout/close program method
		primaryStage.setOnCloseRequest(e -> {
				e.consume();
				loguot(primaryStage);
				});
		//____________________________________
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	//___________________________________
	//Logout/close method
	public void loguot (Stage stage) {
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Close window");
		alert.setHeaderText("Do you want to close this program");
		//alert.setContentText("Do you want to close this program");
		
		if(alert.showAndWait().get() == ButtonType.OK) {
		stage.close();
		}	
    }
   
			
	
    public static void main(String[] args) {
        launch();
    }

}