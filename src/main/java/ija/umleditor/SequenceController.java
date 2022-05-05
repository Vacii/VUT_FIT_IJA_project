package ija.umleditor;

import ija.umleditor.uml.UMLClass;
import ija.umleditor.uml.UMLRelation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

import static ija.umleditor.Main.classDiagram;

public class SequenceController {
    public List<UMLClass> classes = classDiagram.getClasses();

    @FXML
    private ChoiceBox<String> chooseClass;
    @FXML
    private ScrollPane sequencePane;
    @FXML
    private AnchorPane classPane;
    @FXML
    private Button classLoadBtn;

    public Label nameOfSeqDiagram;



    private double XpositionOfClass;
    private double YpositionOfClass;

    @FXML
    private void showClass(ActionEvent e){
        String name = chooseClass.getValue();
        if (!name.isEmpty()){
            VBox vbox = new VBox();
            TitledPane titledPane = new TitledPane(name, vbox);
            titledPane.setText(name);
            titledPane.setId(name);
            titledPane.setCollapsible(false);
//            titledPane.setPrefHeight(100);
//            titledPane.setPrefWidth(100);
            if (classDiagram.findClass(name) != null) {
                titledPane.setLayoutX(0);
                titledPane.setLayoutY(0);
                classPane.getChildren().add(titledPane);
            }
            titledPane.setOnMousePressed(event -> {
                XpositionOfClass = event.getSceneX() - titledPane.getTranslateX();
                YpositionOfClass = event.getSceneY() - titledPane.getTranslateY();
            });
            //TODO nastavovat pozice i u trid nactenych ze souboru
            titledPane.setOnMouseDragged(event -> {
                titledPane.setTranslateX(event.getSceneX() - XpositionOfClass);
                titledPane.setTranslateY(event.getSceneY() - YpositionOfClass);
            });
        }
    }

    public void loadClasses(){
        for (int i = 0; i < classDiagram.getClasses().size(); i++){
            chooseClass.getItems().add(classes.get(i).getName());
        }
        chooseClass.setValue(classes.get(0).getName());
    }

    @FXML
    private void closeSeqView(ActionEvent e){
        Stage stage = (Stage) classLoadBtn.getScene().getWindow();
        stage.close();
    }
}
