package ija.umleditor;
import ija.umleditor.uml.ClassDiagram;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Main JavaFX scene builder
 * Vytváří hlavní scénu a stage pro GUI aplikace
 * Zároveň vytvoří nový diagram
 *
 * @author  Lukáš Václavek (xvacla32)
 */
public class Main extends Application {
    public static Scene scene;
    public static ClassDiagram classDiagram;

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml"));
        stage.setTitle("UML Editor");
        stage.setScene(new Scene(root));
        Image icon = new Image("file:icon.png");
        stage.getIcons().add(icon);
        stage.show();
    }

    public static void main(String[] args) {
        classDiagram = new ClassDiagram("Diagram");
        launch();
    }
}