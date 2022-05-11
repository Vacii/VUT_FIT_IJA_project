package ija.umleditor;

import ija.umleditor.uml.UMLClass;
import ija.umleditor.uml.UMLMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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

    @FXML
    private ChoiceBox<String> chooseFirstClassForMsg;

    @FXML
    private ChoiceBox<String> chooseMsgType;
    private boolean typesLoaded = false;

    @FXML
    private ChoiceBox<String> chooseMsgOperation;

    @FXML
    private ChoiceBox<String> chooseSecondClassForMsg;
    //TODO chceck better pane options
    @FXML
    private ScrollPane sequencePane;
    @FXML
    private AnchorPane classPane;
    @FXML
    private Button classLoadBtn;

    public Label nameOfSeqDiagram;

    @FXML
    private TextField lenghtOfCom;


    public boolean wasInitialized = false;
    private double pubHeight;
    private double comPosition;
    private double XpositionOfClass;
    private double newXpositionOfClass;
    private double orgTranslateX;
    private double orgTranslateY;


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
                chooseFirstClassForMsg.getItems().add(name);
                chooseFirstClassForMsg.setValue(name);

                if (!classDiagram.findClass(chooseFirstClassForMsg.getValue()).getNamesOfMethods().isEmpty()){
                    chooseMsgOperation.getItems().removeAll(chooseMsgOperation.getItems());
                    chooseMsgOperation.getItems().addAll(classDiagram.findClass(chooseFirstClassForMsg.getValue()).getNamesOfMethods());
                    chooseMsgOperation.setValue(classDiagram.findClass(chooseFirstClassForMsg.getValue()).getNamesOfMethods().get(0));
                }

                chooseSecondClassForMsg.getItems().add(name);
                drawDashedLine(name);
                classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).addClass(classDiagram.findClass(name));
                wasInitialized = true;
                if (!typesLoaded){
                    loadMsgTypes();
                }
            }
            label.setOnMousePressed(event -> {
                XpositionOfClass = event.getSceneX();
                orgTranslateX = ((Label) (event.getSource())).getTranslateX();
            });
            label.setOnMouseDragged(event -> {
                ((Label) (event.getSource())).setTranslateX(orgTranslateX + event.getSceneX() - XpositionOfClass);

                //TODO temp position setting
                classDiagram.findClass(name).setSeqPos(orgTranslateX + event.getSceneX() - XpositionOfClass);
                classPane.getChildren().remove(classPane.lookup(("#" + name + "DashedLine")));
                drawDashedLine(name);

                List<Double> heights = classDiagram.findClass(name).getHeightsOfCom();
                for (int i = 0; i < heights.size(); i++){
                    classPane.getChildren().removeAll(classPane.lookupAll(("#" + name + "Communication")));
                    Rectangle rectangle = new Rectangle();
                    drawComRectangle(classDiagram.findClass(name), heights.get(i), rectangle);
                }
            });
        }
    }

    private void showClass (String name) {

        if (name != null && classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).findClass(name) == null){
            Label label = new Label();
            label.setText(name);
            label.setId(name + "label");
            if (classDiagram.findClass(name) != null) {
                label.setLayoutX(classDiagram.findClass(name).getSeqPos());
                label.setLayoutY(20);
                classPane.getChildren().add(label);
                chooseFirstClassForMsg.getItems().add(name);
                chooseFirstClassForMsg.setValue(name);
                if (!classDiagram.findClass(chooseFirstClassForMsg.getValue()).getNamesOfMethods().isEmpty()){
                    chooseMsgOperation.getItems().removeAll(chooseMsgOperation.getItems());
                    chooseMsgOperation.getItems().addAll(classDiagram.findClass(chooseFirstClassForMsg.getValue()).getNamesOfMethods());
                    chooseMsgOperation.setValue(classDiagram.findClass(chooseFirstClassForMsg.getValue()).getNamesOfMethods().get(0));
                }
                chooseSecondClassForMsg.getItems().add(name);
                drawDashedLine(name);
                classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).addClass(classDiagram.findClass(name));
                wasInitialized = true;
                if (!typesLoaded){
                    loadMsgTypes();
                }
            }
            label.setOnMousePressed(event -> {
                XpositionOfClass = event.getSceneX();
                orgTranslateX = ((Label) (event.getSource())).getTranslateX();
            });
            label.setOnMouseDragged(event -> {
                ((Label) (event.getSource())).setTranslateX(orgTranslateX + event.getSceneX() - XpositionOfClass);
                //TODO temp position setting
                classDiagram.findClass(name).setSeqPos(orgTranslateX + event.getSceneX() - XpositionOfClass);

                classPane.getChildren().remove(classPane.lookup(("#" + name + "DashedLine")));
                drawDashedLine(name);

                List<Double> heights = classDiagram.findClass(name).getHeightsOfCom();
                for (int i = 0; i < heights.size(); i++){
                    classPane.getChildren().removeAll(classPane.lookupAll(("#" + name + "Communication")));
                    Rectangle rectangle = new Rectangle();
                    drawComRectangle(classDiagram.findClass(name), heights.get(i), rectangle);
                }
            });
        }

    }

    @FXML
    private void deleteClass(ActionEvent e){
        String name = chooseClass.getValue();
        if (name != null && classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).findClass(name) != null){
            if(classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).removeCleass(classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).findClass(name))){
                classPane.getChildren().remove(classPane.lookup(("#" + name + "label")));
                classPane.getChildren().remove(classPane.lookup(("#" + name + "DashedLine")));
                List<Double> heights = classDiagram.findClass(name).getHeightsOfCom();
                for (int i = 0; i < heights.size(); i++){
                    classPane.getChildren().removeAll(classPane.lookupAll(("#" + name + "Communication")));
                }
                //Remove communication rectangles
                classDiagram.findClass(name).removeHeightsOfCom();
                //Setup default position of class
                classDiagram.findClass(name).setSeqPos(20);
                //Remove classes from Choiceboxes

                chooseFirstClassForMsg.getItems().remove(name);
                chooseSecondClassForMsg.getItems().remove(name);
            }

        }
    }

    public void loadClasses(){
        for (int i = 0; i < classDiagram.getClasses().size(); i++){
            if (!chooseClass.getItems().contains(classes.get(i).getName())){
                chooseClass.getItems().add(classes.get(i).getName());
            }
        }
        chooseClass.setValue(classes.get(0).getName());
        classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).setOpened(true);
    }

    @FXML
    private void closeSeqView(ActionEvent e){
        Stage stage = (Stage) classLoadBtn.getScene().getWindow();
        classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).setOpened(false);
        stage.close();
    }

    private void drawDashedLine(String name){
        Line line = new Line();
        line.setId(name + "DashedLine");
        line.setStartX(classDiagram.findClass(name).getSeqPos() + 6);
        line.setStartY(35);
        line.setEndX(classDiagram.findClass(name).getSeqPos() + 6);
        line.setEndY(1000);
        line.getStrokeDashArray().addAll(25d, 10d);
        classPane.getChildren().add(0,line);
    }

    //TODO rectangles could be loaded better in future. (automaticaly when message is created)
    private void drawComRectangle(UMLClass aClass, double classHeight, Rectangle rectangle){
        //TODO add counter, ID is same for all of them right now
        rectangle.setId(aClass.getName() + "Communication");
        System.out.println(classDiagram.findClass(aClass.getName()).getSeqPos());
        rectangle.setX(classDiagram.findClass(aClass.getName()).getSeqPos());
        List<Double> positionsOfCom = classDiagram.findClass(aClass.getName()).getPosOfCom();
        //TODO 0 temporary, that means only 1 rectangle is supported
        rectangle.setY(positionsOfCom.get(0));
        rectangle.setWidth(10);
        rectangle.setHeight(classHeight);
        rectangle.setFill(Color.WHITESMOKE);
        rectangle.setStroke(Color.BLACK);
        classPane.getChildren().add(rectangle);
    }

    //TODO when class is moved, the dotetd line is the right position and class not
    public void loadSavedDiagram(String name){
        for (int i = 0; i < classDiagram.findSeqDiagram(name).getClasses().size(); i++){
            Label label = new Label();
            UMLClass obj = classDiagram.findSeqDiagram(name).getClasses().get(i);
            label.setText(obj.getName());
            label.setId(obj.getName() + "label");
            label.setLayoutX(obj.getSeqPos());
            label.setLayoutY(20);
            classPane.getChildren().add(label);
            chooseFirstClassForMsg.getItems().add(obj.getName());
            chooseFirstClassForMsg.setValue(obj.getName());
            if (!classDiagram.findClass(chooseFirstClassForMsg.getValue()).getNamesOfMethods().isEmpty()){
                chooseMsgOperation.getItems().removeAll(chooseMsgOperation.getItems());
                chooseMsgOperation.getItems().addAll(classDiagram.findClass(chooseFirstClassForMsg.getValue()).getNamesOfMethods());
                chooseMsgOperation.setValue(classDiagram.findClass(chooseFirstClassForMsg.getValue()).getNamesOfMethods().get(0));
            }
            chooseSecondClassForMsg.getItems().add(obj.getName());

            if (!typesLoaded){
                loadMsgTypes();
            }

            List<Double> heights = obj.getHeightsOfCom();
            drawDashedLine(obj.getName());
            if (!heights.isEmpty()){
                for (int j = 0; j < heights.size(); j++){
                    Rectangle rectangle = new Rectangle();
                    drawComRectangle(obj, heights.get(j), rectangle);
                }
            }

            label.setOnMousePressed(event1 -> {
                XpositionOfClass = event1.getSceneX();
                orgTranslateX = ((Label) (event1.getSource())).getTranslateX();
            });
            label.setOnMouseDragged(event1 -> {
                ((Label) (event1.getSource())).setTranslateX(orgTranslateX + event1.getSceneX() - XpositionOfClass);
                //TODO idk whats wrong
                obj.setSeqPos(orgTranslateX + event1.getSceneX() - XpositionOfClass);
                classPane.getChildren().remove(classPane.lookup(("#" + obj.getName() + "DashedLine")));
                drawDashedLine(obj.getName());

                for (int k = 0; k < heights.size(); k++){
                    classPane.getChildren().removeAll(classPane.lookupAll(("#" + obj.getName() + "Communication")));
                    Rectangle rectangle = new Rectangle();
                    drawComRectangle(obj, heights.get(k), rectangle);
                }
            });

        }
    }

    private void loadMsgTypes(){
        String[] types = {"Synchronous", "Asynchronous", "Reply", "Create object", "Remove object"};
        chooseMsgType.setValue("Synchronous");
        chooseMsgType.getItems().addAll(types);
        typesLoaded = true;

        chooseFirstClassForMsg.setOnAction(event -> {
            chooseMsgOperation.getItems().removeAll(chooseMsgOperation.getItems());
            if (!classDiagram.findClass(chooseFirstClassForMsg.getValue()).getNamesOfMethods().isEmpty() && (chooseFirstClassForMsg.getItems().size() != 0)){
                chooseMsgOperation.getItems().addAll(classDiagram.findClass(chooseFirstClassForMsg.getValue()).getNamesOfMethods());
                chooseMsgOperation.setValue(classDiagram.findClass(chooseFirstClassForMsg.getValue()).getNamesOfMethods().get(0));
            }
        });
    }

    @FXML
    private void createCom(ActionEvent event) {
        double height = 0;
        try {
            height = Double.parseDouble(lenghtOfCom.getText());
        } catch (Exception e) {
            System.out.println("You need to enter double value!");
        }
        if (height != 0){
            classDiagram.findClass(chooseClass.getValue()).addHeight(height);
            classDiagram.findClass(chooseClass.getValue()).addPosOfCom(45);
            Rectangle rectangle = new Rectangle();
            drawComRectangle(classDiagram.findClass(chooseClass.getValue()), height, rectangle);

            rectangle.setOnMousePressed(event1 -> {
                comPosition = event1.getSceneX();
                orgTranslateY = ((Rectangle) (event1.getSource())).getTranslateY();
            });
            double finalHeight = height;
            rectangle.setOnMouseDragged(event1 -> {
                ((Rectangle) (event1.getSource())).setTranslateY(orgTranslateY + event1.getSceneY() - comPosition);
                rectangle.setTranslateY(orgTranslateY + event1.getSceneY() - comPosition);
                //TODO movable rectangle
                classPane.getChildren().removeAll(classPane.lookupAll(("#" + classDiagram.findClass(chooseClass.getValue()).getName() + "Communication")));
                drawComRectangle(classDiagram.findClass(chooseClass.getValue()), finalHeight, rectangle);
                classDiagram.findClass(chooseClass.getValue()).setPositionOfCom(0,orgTranslateY + event1.getSceneY() - comPosition);
            });
        }
    }

    @FXML
    private void createMessage(){
        if (!chooseFirstClassForMsg.getValue().isEmpty() && !chooseSecondClassForMsg.getValue().isEmpty() && !chooseMsgType.getValue().isEmpty() && !chooseMsgOperation.getValue().isEmpty()){
            UMLClass firstClass = classDiagram.findClass(chooseFirstClassForMsg.getValue());
            UMLClass secondClass = classDiagram.findClass(chooseSecondClassForMsg.getValue());
            UMLMessage message;
            String name = ("msg" + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCounter());
            message = classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).createMessage(name, firstClass.getName(),   "Instance " + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getInstaceCounter() + " of " + secondClass.getName(), chooseMsgType.getValue(), chooseMsgOperation.getValue());
            classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).incInstanceCounter();

            classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).incMsgCounter();

            drawMessage(message);

        }
    }

    private void drawMessage(UMLMessage message){
        Line line;

    }

    @FXML
    private void loadSeqJson(ActionEvent e) {

        String FilePath;
        FileChooser chooseFile = new FileChooser();
        chooseFile.setInitialDirectory(new File(System.getProperty("user.home")));
        chooseFile.setTitle("Select file");
        chooseFile.getExtensionFilters().add(new FileChooser.ExtensionFilter("json file", "*.json"));
        File selectedFile = chooseFile.showOpenDialog(new Stage());

        if (selectedFile != null){

            FilePath = selectedFile.getAbsolutePath();

            parseJsonToObject(getJsonFromFile(FilePath));

        }
    }


    private String getJsonFromFile (String filename) {

        String jsonText = "";

        try {

            //reading through file and appending each line to our empty string jsonText
            //when we reach the EOF we close the buffer and return JSON file as a string
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = bufferedReader.readLine()) != null) {

                jsonText += line + "\n";
            }

            bufferedReader.close();
        }

        catch (Exception e) {

            e.printStackTrace();
        }

        return jsonText;

    }

    private void parseJsonToObject (String jsonStr) {

        JSONArray jsonArray = new JSONArray(jsonStr);

        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject jsonObject = jsonArray.getJSONObject(i);

            String entity = jsonObject.getString("entity"); //název classy

            //teď mám její název - můžu displaynout

            showClass(entity);
        }

    }
}
