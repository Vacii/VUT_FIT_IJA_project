package ija.umleditor;

import ija.umleditor.uml.UMLClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.List;

import static ija.umleditor.Main.classDiagram;

/**
 * SequenceController je hlavní třída pro ovládání GUI sekvenčních diagramů
 *
 * @author  Lukáš Václavek (xvacla32)
 * @author  Roman Marek (xmarek74)
 */
public class SequenceController {
    public List<UMLClass> classes = classDiagram.getClasses();

    @FXML
    private ChoiceBox<String> chooseClass;
    //TODO chceck better pane options
    @FXML
    private ScrollPane sequencePane;
    @FXML
    private AnchorPane classPane;
    @FXML
    private Button classLoadBtn;

    public Label nameOfSeqDiagram;


    public boolean wasInitialized = false;
    private double XpositionOfClass;
    private double YpositionOfClass;

    //TODO save created diagram to backend - edits in SequenceDiagram will be needed
    @FXML
    private void showClass(ActionEvent e){
        String name = chooseClass.getValue();
        if (name != null && classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).findClass(name) == null){
            Label label = new Label();
            label.setText(name);
            label.setId(name + "label");
            if (classDiagram.findClass(name) != null) {
                label.setLayoutX(classDiagram.findClass(name).getSeqPos());
                label.setLayoutY(20);
                classPane.getChildren().add(label);
                drawDashedLine(name);
                classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).addClass(classDiagram.findClass(name));
                wasInitialized = true;
            }
            label.setOnMousePressed(event -> {
                XpositionOfClass = event.getSceneX() - label.getTranslateX();
            });
            label.setOnMouseDragged(event -> {
                label.setTranslateX(event.getSceneX() - XpositionOfClass);
                //TODO temp position setting
                classDiagram.findClass(name).setSeqPos(event.getSceneX() - XpositionOfClass + 26);

                classPane.getChildren().remove(classPane.lookup(("#" + name + "DashedLine")));
                drawDashedLine(name);
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

    private void drawDashedLine(String name){
        Line line = new Line();
        line.setId(name + "DashedLine");
        line.setStartX(classDiagram.findClass(name).getSeqPos() + 6);
        line.setStartY(30);
        line.setEndX(classDiagram.findClass(name).getSeqPos() + 6);
        line.setEndY(500);
        line.getStrokeDashArray().addAll(25d, 10d);
        classPane.getChildren().add(0,line);
    }

    public void loadSavedDiagram(String name){
        for (int i = 0; i < classDiagram.findSeqDiagram(name).getClasses().size(); i++){
            Label label = new Label();
            UMLClass obj = classDiagram.findSeqDiagram(name).getClasses().get(i);
            label.setText(obj.getName());
            label.setId(obj.getName() + "label");
            label.setLayoutX(obj.getSeqPos());
            label.setLayoutY(20);
            classPane.getChildren().add(label);
            drawDashedLine(obj.getName());
        }
    }
}
