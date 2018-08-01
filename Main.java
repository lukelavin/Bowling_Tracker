/**
 * Created by Luke Lavin 7/30/18
 */
package Bowling_Tracker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Bowling Tracker");
        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("bowling_icon.png")));
        primaryStage.setScene(new Scene(root, 770, 620));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
