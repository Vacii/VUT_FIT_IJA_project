package ija.umleditor;

import ija.umleditor.uml.UMLClass;
import ija.umleditor.uml.UMLRelation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;

import java.io.IOException;

import static ija.umleditor.Main.classDiagram;
// NOT USED ATM, ALL RELATIONS ARE EDITED IN MAIN VIEW
public class RelationController {
    @FXML
    public AnchorPane relatonPane;

    @FXML
    private ChoiceBox<String> firstClassForRelation;

    @FXML
    private ChoiceBox<String> secondClassForRelation;

    @FXML
    private ChoiceBox<String> chooseRelationType;

    @FXML
    private TextField relationName;

    @FXML
    private void createRelation(ActionEvent e) throws IOException {
        String newRelName = relationName.getText();

        if (!newRelName.isEmpty()) {
            UMLClass firstClass = classDiagram.findClass(firstClassForRelation.getValue());
            UMLClass secondClass = classDiagram.findClass(secondClassForRelation.getValue());
//            UMLRelation relace = classDiagram.createRelation(firstClass, secondClass, newRelName);
            Line line = new Line();
//            line.setStartX(firstClass.getXposition());
//            line.setStartY(firstClass.getYposition());
//            line.setEndX(secondClass.getXposition());
//            line.setEndY(secondClass.getYposition());
            line.setStartX(100.0);
            line.setStartY(150.0);
            line.setEndX(500.0);
            line.setEndY(150.0);
            System.out.println(line);
        }


    }

    @FXML
    private void deleteRelation(ActionEvent e){
        //TODO implement
    }

    public void loadClassesToRelations(){
        String[] relationType = {"Asociation", "Inheritance", "Aggregation", "Composion"};
        chooseRelationType.setValue("Asociation");
        chooseRelationType.getItems().addAll(relationType);
//        firstClassForRelation.getItems().addAll(MainController.classNames);
//        secondClassForRelation.getItems().addAll(MainController.classNames);
    }
}
