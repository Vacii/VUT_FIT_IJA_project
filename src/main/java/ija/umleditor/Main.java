package ija.umleditor;

import ija.umleditor.uml.ClassDiagram;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main JavaFX scene
 */
public class Main extends Application {
    public static Scene scene;
    public static ClassDiagram classDiagram;

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml"));
        stage.setTitle("UML Editor");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void main(String[] args) {
        classDiagram = new ClassDiagram("Diagram");
        launch();
    }
}