package core;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {
	public static Stage primaryStage;

	public static void main(String[] args) {
		try {
			launch(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			Main.primaryStage = primaryStage;
			// Loads the font to be used within the UI
			Font.loadFont(getClass().getResource("/fonts/cmunrm.ttf").toExternalForm(), 1.0);
			// Loads the FXML file for the UI
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/start.fxml"));
			Parent root = loader.load();
			// Sets the title of the frame
			primaryStage.setTitle("Mortimer");
			Scene scene = new Scene(root);
			// Applies the stylesheet to the window
			scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			// Adds the logo
			primaryStage.getIcons()
					.add(new Image(getClass().getResource("/images/logo.png").toExternalForm()));
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Ensures window can be closed and stops the execution of the program
		primaryStage.setOnCloseRequest(e -> {
			primaryStage.close();
			System.exit(0);
		});
	}

}
