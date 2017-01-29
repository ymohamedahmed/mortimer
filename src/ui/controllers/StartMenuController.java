package ui.controllers;

import java.io.IOException;

import core.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class StartMenuController {
	public void initialize() {
		
	}	
	public void loadGame(ActionEvent event){
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/root.fxml"));
			Parent root = loader.load();
			MainController controller = loader.<MainController>getController();
			Scene scene = new Scene(root);
			Main.primaryStage.setScene(scene);
			Main.primaryStage.setResizable(false);
			controller.handleLoadFileAction(event);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void playMortimer(){
		
	}
	private void chooseColour(){
		
	}
	public void playHuman(){
		
	}
	
}
