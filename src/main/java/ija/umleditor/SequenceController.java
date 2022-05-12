package ija.umleditor;

import ija.umleditor.uml.UMLClass;
import ija.umleditor.uml.UMLInterface;
import ija.umleditor.uml.UMLMessage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import static ija.umleditor.Main.classDiagram;
import static javafx.application.Application.setUserAgentStylesheet;

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
    private double msgTranslateY;
    private double orgHeight;

    private ArrayList<String> classesToSave = new ArrayList();
    private ArrayList<String> messagesToDisplay = new ArrayList<>();


    @FXML
    private void showClass(ActionEvent e){
        String nameOfSeqDia = nameOfSeqDiagram.getText();
        String name = chooseClass.getValue();
        if (name != null && classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).findClass(name) == null){
            Label label = new Label();
            label.setText(name);
            label.setId(name + "label");
            if (!classesToSave.contains(name)) classesToSave.add(name);

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
                //vzal jsem to co máš nahoře, ale dělá to nějaký špatný věci xd

                List<UMLMessage> messages = classDiagram.findSeqDiagram(nameOfSeqDia).getMessages();
                for (int i = 0; i < messages.size(); i++){
                        classPane.getChildren().remove(classPane.lookup("#" + messages.get(i).getName() + "MessageUpArr" + (i+1)));
                        classPane.getChildren().remove(classPane.lookup("#" + messages.get(i).getName() + "MessageDownArr" + (i+1)));
                        classPane.getChildren().remove(classPane.lookup("#" + messages.get(i).getName() + "MessageTriangle" + (i+1)));
                        classPane.getChildren().remove(classPane.lookup("#" + messages.get(i).getName() + "Message" + (i+1)));
                        classPane.getChildren().remove(classPane.lookup("#" + messages.get(i).getName() + (i+1)));
                        redrawMessage(messages.get(i));

                }
            });
        }
    }

    private void showClass (String name) {

        if (name != null && classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).findClass(name) == null){
            Label label = new Label();
            label.setText(name);
            label.setId(name + "label");
            if (!classesToSave.contains(name)) classesToSave.add(name);

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

                List<UMLMessage> messages = classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMessages();
                System.out.println(messages);
                for (int i = 0; i < messages.size(); i++){
                    classPane.getChildren().remove(classPane.lookup("#" + messages.get(i).getName() + "MessageUpArr" + (i+1)));
                    classPane.getChildren().remove(classPane.lookup("#" + messages.get(i).getName() + "MessageDownArr" + (i+1)));
                    classPane.getChildren().remove(classPane.lookup("#" + messages.get(i).getName() + "MessageTriangle" + (i+1)));
                    classPane.getChildren().remove(classPane.lookup("#" + messages.get(i).getName() + "Message" + (i+1)));
                    classPane.getChildren().remove(classPane.lookup("#" + messages.get(i).getName() + (i+1)));
                    redrawMessage(messages.get(i));

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
                classesToSave.remove(name);
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
        String nameOfSeqDia = nameOfSeqDiagram.getText();
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
                List<UMLMessage> messages = classDiagram.findSeqDiagram(nameOfSeqDia).getMessages();
                for (int l = 0; l < messages.size(); l++){
                    classPane.getChildren().remove(classPane.lookup("#" + messages.get(l).getName() + "MessageUpArr" + (l+1)));
                    classPane.getChildren().remove(classPane.lookup("#" + messages.get(l).getName() + "MessageDownArr" + (l+1)));
                    classPane.getChildren().remove(classPane.lookup("#" + messages.get(l).getName() + "MessageTriangle" + (l+1)));
                    classPane.getChildren().remove(classPane.lookup("#" + messages.get(l).getName() + "Message" + (l+1)));
                    classPane.getChildren().remove(classPane.lookup("#" + messages.get(l).getName() + (l+1)));
                    redrawMessage(messages.get(l));

                }
            });

        }
    }

    private void loadMsgTypes(){
        String[] types = {"Synchronous", "Asynchronous", "Return", "Create object", "Remove object"};
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

        chooseMsgType.setOnAction(event -> {
            if (chooseMsgType.getValue().equals("Return")){
                chooseMsgOperation.getItems().removeAll(chooseMsgOperation.getItems());
                chooseMsgOperation.getItems().add("Return");
                chooseMsgOperation.setValue("Return");
            }
            else {
                chooseMsgOperation.getItems().removeAll(chooseMsgOperation.getItems());
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
            UMLMessage message = classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).createMessage(chooseMsgOperation.getValue(), firstClass.getName(), secondClass.getName(), chooseMsgType.getValue(), chooseMsgOperation.getValue());

            //TODO message cannot be moved on Y axis
            Line line = new Line();
            Line arrUp = new Line();
            Line arrDown = new Line();
            Label label = new Label();

            classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).incrementMsgCount();

            drawMessage(message);

            line.setOnMousePressed(event -> {
                msgTranslateY = event.getSceneY();
                orgTranslateY = ((Line) (event.getSource())).getTranslateY();
                orgHeight = message.getHeight();
            });

            line.setOnMouseDragged(event -> {
                double offsetY = event.getSceneY() - msgTranslateY;
                double newTranslateY = orgTranslateY + offsetY;

                ((Line) (event.getSource())).setTranslateY(newTranslateY);
                line.setTranslateY(newTranslateY);
                label.setTranslateY(newTranslateY);
                message.setHeight(orgHeight + offsetY);
                classPane.getChildren().remove(classPane.lookup(("#" + message.getName() + "Message")));
                drawMessage(message);
            });
        }
    }

    private void drawMessage(UMLMessage message){
        Line line = new Line();
        Line arrUp = new Line();
        Line arrDown = new Line();
        Label label = new Label();

        message.setOrder(classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner());

        switch (message.getType()){
            case "Asynchronous":
                line.setId(message.getName() + "Message" + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner());
                arrUp.setId(message.getName() + "MessageUpArr"  + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner());
                arrDown.setId(message.getName() + "MessageDownArr"  + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner());
                line.setStartX(classDiagram.findClass(message.getClass1()).getSeqPos() + 6);
                line.setStartY(message.getHeight() + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner() * 20);
                line.setEndX(classDiagram.findClass(message.getClass2()).getSeqPos() + 6);
                line.setEndY(message.getHeight() + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner() * 20);

                if ((classDiagram.findClass(message.getClass1()).getSeqPos() + 6) > (classDiagram.findClass(message.getClass2()).getSeqPos() + 6)) {
                    arrUp.setStartX(classDiagram.findClass(message.getClass2()).getSeqPos() + 12);
                    arrUp.setStartY(message.getHeight() - 5 + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner() * 20);
                    arrUp.setEndX(classDiagram.findClass(message.getClass2()).getSeqPos() + 6);
                    arrUp.setEndY(message.getHeight() + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner() * 20);
                    arrDown.setStartX(classDiagram.findClass(message.getClass2()).getSeqPos() + 12);
                    arrDown.setStartY(message.getHeight() + 5 + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner() * 20);
                    arrDown.setEndX(classDiagram.findClass(message.getClass2()).getSeqPos() + 6);
                    arrDown.setEndY(message.getHeight() + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner() * 20);
                }
                else{
                    arrUp.setStartX(classDiagram.findClass(message.getClass2()).getSeqPos());
                    arrUp.setStartY(message.getHeight() - 5 + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner() * 20);
                    arrUp.setEndX(classDiagram.findClass(message.getClass2()).getSeqPos() + 6);
                    arrUp.setEndY(message.getHeight() + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner() * 20);
                    arrDown.setStartX(classDiagram.findClass(message.getClass2()).getSeqPos());
                    arrDown.setStartY(message.getHeight() + 5 + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner() * 20);
                    arrDown.setEndX(classDiagram.findClass(message.getClass2()).getSeqPos() + 6);
                    arrDown.setEndY(message.getHeight() + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner() * 20);
                }
                classPane.getChildren().add(0,line);
                classPane.getChildren().add(0,arrDown);
                classPane.getChildren().add(0,arrUp);
                break;
            case "Synchronous":
                line.setId(message.getName() + "Message"  + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner());

                line.setStartX(classDiagram.findClass(message.getClass1()).getSeqPos() + 6);
                line.setStartY(message.getHeight() + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner() * 20);
                line.setEndX(classDiagram.findClass(message.getClass2()).getSeqPos() + 6);
                line.setEndY(message.getHeight() + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner() * 20);
                Polygon polygon = new Polygon();

                if ((classDiagram.findClass(message.getClass1()).getSeqPos() + 6) > (classDiagram.findClass(message.getClass2()).getSeqPos() + 6)) {

                    polygon.getPoints().addAll(classDiagram.findClass(message.getClass2()).getSeqPos() + 12, message.getHeight() - 5 + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner() * 20,
                            classDiagram.findClass(message.getClass2()).getSeqPos() + 6, message.getHeight() + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner() * 20,
                            classDiagram.findClass(message.getClass2()).getSeqPos() + 12, message.getHeight() + 5 + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner() * 20);
                }
                else{
                    polygon.getPoints().addAll(classDiagram.findClass(message.getClass2()).getSeqPos(), message.getHeight() - 5 + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner() * 20,
                            classDiagram.findClass(message.getClass2()).getSeqPos() + 6, message.getHeight() + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner() * 20,
                            classDiagram.findClass(message.getClass2()).getSeqPos(), message.getHeight() + 5 + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner() * 20);
                }

                polygon.setId(message.getName() + "MessageTriangle"  + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner());
                classPane.getChildren().add(0, polygon);
                classPane.getChildren().add(0,line);
                break;
            case "Return":
                line.setId(message.getName() + "Message"  + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner());
                arrUp.setId(message.getName() + "MessageUpArr"  + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner());
                arrDown.setId(message.getName() + "MessageDownArr"  + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner());

                line.setStartX(classDiagram.findClass(message.getClass1()).getSeqPos() + 6);
                line.setStartY(message.getHeight() + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner() * 20);
                line.setEndX(classDiagram.findClass(message.getClass2()).getSeqPos() + 6);
                line.setEndY(message.getHeight() + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner() * 20);


                if ((classDiagram.findClass(message.getClass1()).getSeqPos() + 6) > (classDiagram.findClass(message.getClass2()).getSeqPos() + 6)) {
                    arrUp.setStartX(classDiagram.findClass(message.getClass2()).getSeqPos() + 12);
                    arrUp.setStartY(message.getHeight() - 5 + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner() * 20);
                    arrUp.setEndX(classDiagram.findClass(message.getClass2()).getSeqPos() + 6);
                    arrUp.setEndY(message.getHeight() + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner() * 20);
                    arrDown.setStartX(classDiagram.findClass(message.getClass2()).getSeqPos() + 12);
                    arrDown.setStartY(message.getHeight() + 5 + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner() * 20);
                    arrDown.setEndX(classDiagram.findClass(message.getClass2()).getSeqPos() + 6);
                    arrDown.setEndY(message.getHeight() + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner() * 20);
                }
                else{
                    arrUp.setStartX(classDiagram.findClass(message.getClass2()).getSeqPos());
                    arrUp.setStartY(message.getHeight() - 5 + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner() * 20);
                    arrUp.setEndX(classDiagram.findClass(message.getClass2()).getSeqPos() + 6);
                    arrUp.setEndY(message.getHeight() + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner() * 20);
                    arrDown.setStartX(classDiagram.findClass(message.getClass2()).getSeqPos());
                    arrDown.setStartY(message.getHeight() + 5 + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner() * 20);
                    arrDown.setEndX(classDiagram.findClass(message.getClass2()).getSeqPos() + 6);
                    arrDown.setEndY(message.getHeight() + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner() * 20);
                }

                line.getStrokeDashArray().addAll(10d, 10d);

                classPane.getChildren().add(0,line);
                classPane.getChildren().add(0,arrDown);
                classPane.getChildren().add(0,arrUp);
                break;
        }
        message.setHeight(message.getHeight() + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner() * 20);
        label.setLayoutX(classDiagram.findClass(message.getClass1()).getSeqPos() + (((classDiagram.findClass(message.getClass2()).getSeqPos() + 6) - (classDiagram.findClass(message.getClass1()).getSeqPos() + 6))/2));
        label.setLayoutY(message.getHeight()-20);
        label.setText(message.getName());
        label.setId(message.getName() + classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).getMsgCouner());
        classPane.getChildren().add(1, label);
    }

    private void redrawMessage(UMLMessage message){
        Line line = new Line();
        Line arrUp = new Line();
        Line arrDown = new Line();
        Label label = new Label();

        switch (message.getType()){
            case "Asynchronous":
                line.setId(message.getName() + "Message" + message.getOrder());
                arrUp.setId(message.getName() + "MessageUpArr" + message.getOrder());
                arrDown.setId(message.getName() + "MessageDownArr" + message.getOrder());
                line.setStartX(classDiagram.findClass(message.getClass1()).getSeqPos() + 6);
                line.setStartY(message.getHeight());
                line.setEndX(classDiagram.findClass(message.getClass2()).getSeqPos() + 6);
                line.setEndY(message.getHeight());

                if ((classDiagram.findClass(message.getClass1()).getSeqPos() + 6) < (classDiagram.findClass(message.getClass2()).getSeqPos() + 6)){
                    arrUp.setStartX(classDiagram.findClass(message.getClass2()).getSeqPos());
                    arrUp.setStartY(message.getHeight()-5);
                    arrUp.setEndX(classDiagram.findClass(message.getClass2()).getSeqPos() + 6);
                    arrUp.setEndY(message.getHeight());
                    arrDown.setStartX(classDiagram.findClass(message.getClass2()).getSeqPos());
                    arrDown.setStartY(message.getHeight()+5);
                    arrDown.setEndX(classDiagram.findClass(message.getClass2()).getSeqPos() + 6);
                    arrDown.setEndY(message.getHeight());
                }
                else{
                    arrUp.setStartX(classDiagram.findClass(message.getClass2()).getSeqPos() + 12);
                    arrUp.setStartY(message.getHeight()-5);
                    arrUp.setEndX(classDiagram.findClass(message.getClass2()).getSeqPos() + 6);
                    arrUp.setEndY(message.getHeight());
                    arrDown.setStartX(classDiagram.findClass(message.getClass2()).getSeqPos() + 12);
                    arrDown.setStartY(message.getHeight()+5);
                    arrDown.setEndX(classDiagram.findClass(message.getClass2()).getSeqPos() + 6);
                    arrDown.setEndY(message.getHeight());
                }

                classPane.getChildren().add(0,line);
                classPane.getChildren().add(0,arrDown);
                classPane.getChildren().add(0,arrUp);
                break;
            case "Synchronous":
                line.setId(message.getName() + "Message" + message.getOrder());

                line.setStartX(classDiagram.findClass(message.getClass1()).getSeqPos() + 6);
                line.setStartY(message.getHeight());
                line.setEndX(classDiagram.findClass(message.getClass2()).getSeqPos() + 6);
                line.setEndY(message.getHeight());

                Polygon polygon = new Polygon();
                if ((classDiagram.findClass(message.getClass1()).getSeqPos() + 6) < (classDiagram.findClass(message.getClass2()).getSeqPos() + 6)) {
                    polygon.getPoints().addAll(classDiagram.findClass(message.getClass2()).getSeqPos(), message.getHeight() - 5,
                            classDiagram.findClass(message.getClass2()).getSeqPos() + 6, message.getHeight(),
                            classDiagram.findClass(message.getClass2()).getSeqPos(), message.getHeight() + 5);
                }
                else{
                    polygon.getPoints().addAll(classDiagram.findClass(message.getClass2()).getSeqPos() + 12, message.getHeight() - 5,
                            classDiagram.findClass(message.getClass2()).getSeqPos() + 6, message.getHeight(),
                            classDiagram.findClass(message.getClass2()).getSeqPos() + 12, message.getHeight() + 5);
                }
                polygon.setId(message.getName() + "MessageTriangle" + message.getOrder());
                classPane.getChildren().add(0, polygon);
                classPane.getChildren().add(0,line);
                break;
            case "Return":
                line.setId(message.getName() + "Message"  + message.getOrder());
                arrUp.setId(message.getName() + "MessageUpArr" + message.getOrder());
                arrDown.setId(message.getName() + "MessageDownArr" + message.getOrder());

                line.setStartX(classDiagram.findClass(message.getClass1()).getSeqPos() + 6);
                line.setStartY(message.getHeight());
                line.setEndX(classDiagram.findClass(message.getClass2()).getSeqPos() + 6);
                line.setEndY(message.getHeight());

                line.getStrokeDashArray().addAll(10d, 10d);

                if ((classDiagram.findClass(message.getClass1()).getSeqPos() + 6) < (classDiagram.findClass(message.getClass2()).getSeqPos() + 6)) {
                    arrUp.setStartX(classDiagram.findClass(message.getClass2()).getSeqPos());
                    arrUp.setStartY(message.getHeight() - 5);
                    arrUp.setEndX(classDiagram.findClass(message.getClass2()).getSeqPos() + 6);
                    arrUp.setEndY(message.getHeight());
                    arrDown.setStartX(classDiagram.findClass(message.getClass2()).getSeqPos());
                    arrDown.setStartY(message.getHeight() + 5);
                    arrDown.setEndX(classDiagram.findClass(message.getClass2()).getSeqPos() + 6);
                    arrDown.setEndY(message.getHeight());
                }
                else{
                    arrUp.setStartX(classDiagram.findClass(message.getClass2()).getSeqPos() + 12);
                    arrUp.setStartY(message.getHeight()-5);
                    arrUp.setEndX(classDiagram.findClass(message.getClass2()).getSeqPos() + 6);
                    arrUp.setEndY(message.getHeight());
                    arrDown.setStartX(classDiagram.findClass(message.getClass2()).getSeqPos() + 12);
                    arrDown.setStartY(message.getHeight()+5);
                    arrDown.setEndX(classDiagram.findClass(message.getClass2()).getSeqPos() + 6);
                    arrDown.setEndY(message.getHeight());
                }
                classPane.getChildren().add(0,line);
                classPane.getChildren().add(0,arrDown);
                classPane.getChildren().add(0,arrUp);
                break;
        }
        label.setLayoutX(classDiagram.findClass(message.getClass1()).getSeqPos() + (((classDiagram.findClass(message.getClass2()).getSeqPos() + 6) - (classDiagram.findClass(message.getClass1()).getSeqPos() + 6))/2));
        label.setLayoutY(message.getHeight()-20);
        label.setText(message.getName());
        label.setId(message.getName() + message.getOrder());
        classPane.getChildren().add(1, label);
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

            //messages

            JSONObject messageObject = (JSONObject) jsonObject.get("messages");

            JSONArray keys = messageObject.names();

            if (keys != null) {

                for (int j = 0; j < keys.length(); j++) {

                    String key = keys.getString(j);
                    String value = messageObject.getString(key);

                    System.out.println(key + " " + value);
                    messagesToDisplay.add(key + "@" + value); //operation@class1@class2@typ

                }
            }
            showClass(entity);
        }


        for (String message : messagesToDisplay) {

            String [] parts = message.split("@");
            String operationName = parts [0];
            String nameOfFirstClass = parts [1];
            String nameOfSecondClass = parts [2];
            String typeOfMessage = parts [3];

            System.out.println(operationName + " " + nameOfFirstClass + " " + nameOfSecondClass + " " + typeOfMessage );

            UMLMessage newMessage = classDiagram.findSeqDiagram(nameOfSeqDiagram.getText()).createMessage(operationName, nameOfFirstClass, nameOfSecondClass, typeOfMessage, operationName);

            if (newMessage != null) {

                drawMessage(newMessage);

            }

        }

    }


    @FXML
    private void saveJsonFile (ActionEvent e) {

        String FilePath;
        FileChooser chooseFile = new FileChooser();
        chooseFile.setInitialDirectory(new File(System.getProperty("user.home")));
        chooseFile.setTitle("Save file");
        chooseFile.getExtensionFilters().add(new FileChooser.ExtensionFilter("json file", "*.json"));
        chooseFile.setInitialFileName(nameOfSeqDiagram.getText());

        try {
            File selectedFile = chooseFile.showSaveDialog(new Stage());

            if (selectedFile != null) {

                JSONArray arr = new JSONArray();
                FilePath = selectedFile.getAbsolutePath();

                //tady musím získat všechny věci ze sekvenčního a hodit je do json array

                System.out.println(classesToSave);
                for (String className : classesToSave) {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("entity", className);
                    arr.put(jsonObject);
                }

                String jsonText = arr.toString();
                try {

                    File file = new File(FilePath);
                    file.createNewFile();

                    FileWriter fw = new FileWriter(file);
                    fw.write(jsonText);
                    fw.close();
                } catch (Exception exc) {

                    throw new RuntimeException();
                }
            }
        }

        catch (Exception exception) {

            throw new RuntimeException();
        }

    }
}

