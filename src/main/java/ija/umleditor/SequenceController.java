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

    //TODO add classes to SeqDiagram backend
    @FXML
    private void showClass(ActionEvent e){
        String name = chooseClass.getValue();
        if (name != null && classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).findClass(name) == null){
            Label label = new Label();
            label.setText(name);
            label.setId(name + "label");
            if (classDiagram.findClass(name) != null) {
                label.setLayoutX(20);
                label.setLayoutY(20);
                classPane.getChildren().add(label);
                classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).addClass(classDiagram.findClass(name));
            }
            label.setOnMousePressed(event -> {
                XpositionOfClass = event.getSceneX() - label.getTranslateX();
            });
            label.setOnMouseDragged(event -> {
                label.setTranslateX(event.getSceneX() - XpositionOfClass);
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
