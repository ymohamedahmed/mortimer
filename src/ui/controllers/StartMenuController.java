package ui.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import core.CoreConstants;
import core.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;

public class StartMenuController {
	private MainController controller;
	public void initialize() {

	}
	
	private void setupController(){
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/root.fxml"));
			Parent root = loader.load();
			controller = loader.<MainController>getController();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
			Main.primaryStage.setScene(scene);
			Main.primaryStage.setResizable(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void playMortimer() {
		setupController();
		chooseColour();
		controller.setupGame();
		controller.playGame();
	}

	private void chooseColour() {
		String choice = new String();
		List<String> choices = new ArrayList<>();
		choices.add("White");
		choices.add("Black");

		ChoiceDialog<String> dialog = new ChoiceDialog<>("White", choices);
		dialog.setTitle("Colour Choice");
		dialog.setContentText("Choose colour you wish to play with");
		Optional<String> result = dialog.showAndWait();

		if (result.isPresent()) {
			choice = result.get();
		} else {
			chooseColour();
		}
		switch (choice) {
		case "White":
			controller.setPlayerColour(CoreConstants.WHITE);
			controller.setAIColour(CoreConstants.BLACK);
			break;
		case "Black":
			controller.setPlayerColour(CoreConstants.BLACK);
			controller.setAIColour(CoreConstants.WHITE);
			break;
		}
	}

	public void playHuman() {
		setupController();
		controller.setupGame();
		controller.setPlayingAI(false);
		controller.playGame();
	}

}
