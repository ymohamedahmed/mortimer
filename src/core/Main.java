package core;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/start.fxml"));
			Parent root = loader.load();
			primaryStage.setTitle("Mortimer");
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.getIcons().add(new Image(getClass().getResource("/images/logo.png").toExternalForm()));
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

		primaryStage.setOnCloseRequest(e -> {
			primaryStage.close();
			System.exit(0);
		});
	}


}
