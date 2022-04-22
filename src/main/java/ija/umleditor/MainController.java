package ija.umleditor;
import ija.umleditor.uml.ClassDiagram;
import ija.umleditor.uml.UMLClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static ija.umleditor.Main.classDiagram;

/**
 * MainController je hlavní třída pro ovládání GUI
 *
 * @author  Lukáš Václavek (xvacla32)
 */
public class MainController {

    private Stage stage;
    private Scene scene;
    private Parent root;
    private double classCounter = 0.0;

    @FXML
    private Label label_class1;

    @FXML
    private TextField newName;

    @FXML
    private AnchorPane mainPane;

    private double xAxis;
    private double yAxis;


    //Button that switches sequence diagram view to main (class diagram view)
    @FXML
    private void switchToMainScene(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("MainView.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    //Button that switches class diagram view to sequence diagram view
    @FXML
    private void switchToSequenceScene(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("sequenceView.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    //Select JSON file to be loaded
    @FXML
    private void loadJsonFile(ActionEvent event) throws IOException {

        String FilePath;
        FileChooser chooseFile = new FileChooser();
        chooseFile.setInitialDirectory(new File(System.getProperty("user.home")));
        chooseFile.setTitle("Select file");
        chooseFile.getExtensionFilters().add(new FileChooser.ExtensionFilter("json file", "*.json"));
        File selectedFile = chooseFile.showOpenDialog(new Stage());

        if (selectedFile != null){

            FilePath = selectedFile.getAbsolutePath();

            loadJson jsonObject = new loadJson();
            jsonObject.parseJsonToObject(jsonObject.getJSONFromFile(FilePath));

            //TODO - DISPLAY SHIT TO GUI
        }
        else {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("loadFailed.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.show();
        }
    }

    //Help popup. TODO Tutorial how to use our app (what each button does)
    @FXML
    private void helpPopupClick(ActionEvent e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("helpPopup.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        stage.show();
    }

    @FXML
    private void exitApp(ActionEvent e){
        System.exit(0);
    }

    @FXML
    private void createClassClick(ActionEvent e){
        String name = newName.getText();
        if (!name.isEmpty()) {
            classDiagram.createClass(name);
            VBox vbox = new VBox();
            TitledPane titledPane = new TitledPane(name, vbox);
            titledPane.setText(name);
            titledPane.setCollapsible(false);
            titledPane.setPrefHeight(100);
            titledPane.setPrefWidth(100);
            //TODO upravit pozici dalsich novych trid
            if (classDiagram.findClass(name) != null) {
                titledPane.setLayoutX(classDiagram.findClass(name).getXposition() + classCounter*5);
                titledPane.setLayoutY(classDiagram.findClass(name).getYposition() + classCounter*5);
                mainPane.getChildren().add(titledPane);
                classCounter++;
            }

            titledPane.setOnMousePressed(event -> {
                xAxis = event.getSceneX() - titledPane.getTranslateX();
                yAxis = event.getSceneY() - titledPane.getTranslateY();
            });
            titledPane.setOnMouseDragged(event -> {
                titledPane.setTranslateX(event.getSceneX() - xAxis);
                titledPane.setTranslateY(event.getSceneY() - yAxis);
            });
        }
    }

    @FXML
    private void createElementClick(ActionEvent e){}


}