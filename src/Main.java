import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

import core.BitBoard;
import core.MoveGen;

public class Main extends Application {
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/root.fxml"));
            Parent root = loader.load();
            primaryStage.setTitle("Mortimer");
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
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
    public void initialiseLookup(){
		MoveGen moveGen = new MoveGen();
		moveGen.initialiseKnightLookupTable();
		moveGen.initialiseKingLookupTable();
		moveGen.initialisePawnLookupTable();
		moveGen.generateMoveDatabase(true);
		moveGen.generateMoveDatabase(false);
		BitBoard.initialiseZobrist();
    }

}
