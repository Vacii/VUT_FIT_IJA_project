package ija.umleditor;
import ija.umleditor.uml.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ija.umleditor.Main.classDiagram;

/**
 * MainController je hlavní třída pro ovládání GUI
 *
 * @author  Lukáš Václavek (xvacla32)
 * @author  Roman Marek (xmarek74)
 */
public class MainController {

    private Stage stage;
    private Scene scene;
    private Parent root;
    private static double classCounter = 0.0;

    /* UNDO STUFF */
    private ArrayList<String> actionsPerformed = new ArrayList<>();
    //class
    private ArrayList<UMLClass> classesDeleted = new ArrayList<>();
    private ArrayList<UMLAttribute> attributesOfClassesDeleted = new ArrayList<>();
    private ArrayList<UMLOperation> methodsOfClassesDeleted = new ArrayList<>();
    private ArrayList<Integer> numberOfAttributesDeleted = new ArrayList<>();
    private ArrayList<Integer> numberOfMethodsDeleted = new ArrayList<>();
    private ArrayList<UMLClass> classesAdded = new ArrayList<>();
    //attributes
    private ArrayList<UMLAttribute> attributesDeleted = new ArrayList<>();
    private ArrayList<UMLClass> classOfAttributeDeleted = new ArrayList<>();
    private ArrayList<UMLAttribute> attributesAdded = new ArrayList<>();
    private ArrayList<UMLClass> classOfAttributeAdded = new ArrayList<>();
    //methods
    private ArrayList<UMLOperation> methodsDeleted = new ArrayList<>();
    private ArrayList<UMLClass> classOfMethodDeleted = new ArrayList<>();
    private ArrayList<UMLOperation> methodsAdded = new ArrayList<>();
    private ArrayList<UMLClass> classOfMethodAdded = new ArrayList<>();
    //edit attr
    private ArrayList<UMLAttribute> editedAttributes = new ArrayList<>();
    private ArrayList<UMLClass> classOfEditedAttribute = new ArrayList<>();
    //edit meth
    private ArrayList<UMLOperation> editedMethods = new ArrayList<>();
    private ArrayList<UMLClass> classOfEditedMethod = new ArrayList<>();
    //edit class
    private ArrayList<UMLClass> editedClass = new ArrayList<>();
    /**************/

    @FXML
    private TextField newName;

    @FXML
    private TextField attAndMethText;

    @FXML
    private TextField typeText;

    //TODO classes can be mover outside of mainPane - SplitPane is probably better choice
    @FXML
    public AnchorPane mainPane;

    @FXML
    public ChoiceBox<String> chooseClass;
    @FXML
    public ChoiceBox<String> chooseOperator;
    @FXML
    public Button undoButton;

    private double XpositionOfClass;
    private double YpositionOfClass;
    double orgTranslateX, orgTranslateY;

    private boolean operatorsLoaded = false;
    private boolean undoActive = false;
    private boolean classDeleteActive = false;
    private boolean editingClass = false;
    private boolean undoingClass = false;

    private ArrayList<String> listOfAttributes = new ArrayList<>();
    private ArrayList<String> listOfMethods = new ArrayList<>();
    private ArrayList<String> relationsToDisplay= new ArrayList<>();

    @FXML
    private ChoiceBox<String> firstClassForRelation;

    @FXML
    private ChoiceBox<String> secondClassForRelation;

    @FXML
    private ChoiceBox<String> chooseRelationType;

    @FXML
    private TextField relationName;
    @FXML
    private ChoiceBox<String> chooseAttribute;
    @FXML
    private ChoiceBox<String> chooseMethod;
    @FXML
    private TextField editType;
    @FXML
    private TextField editName;
    @FXML
    private TextField editClassText;

//  Sequence diagram
    @FXML
    private TextField nameOfNewSeqDiagram;
    @FXML
    private ChoiceBox<String> chooseSeqDiagram;



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

            parseJsonToObject(getJSONFromFile(FilePath));

        }
        else {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("loadFailed.fxml"));
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.show();
        }
    }

    //Help popup. TODO Tutorial how to use our app (what each button does)
    @FXML
    private void helpPopupClick(ActionEvent e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("helpPopup.fxml"));
        Parent root1 = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        stage.show();
    }

    private void loadOperatorsToChoiceBox(){
        String[] operators = {"+", "-", "#", "~"};
        chooseOperator.setValue("+");
        chooseOperator.getItems().addAll(operators);
        operatorsLoaded = true;
        loadClassesToRelations();
    }

    @FXML
    private void exitApp(ActionEvent e){
        System.exit(0);
    }

    //TODO update seqDia classes?
    //TODO position dost not work correctly
    //TODO check if all funcionality is equal in imported classes/interfaces
    private void createClassHelpMethod () {
        String name;

        if (undoActive) name = classesDeleted.get(classesDeleted.size()-1).getName();

        else name = newName.getText();

        if (!name.isEmpty() && classDiagram.findClass(name) == null) {
            classDiagram.createClass(name);
            VBox vbox = new VBox();

            VBox attributes = new VBox();
            vbox.getChildren().add(attributes);
            attributes.setId(name + "Attributes");

            vbox.getChildren().add(new Separator());

            VBox methods = new VBox();
            vbox.getChildren().add(methods);
            methods.setId(name + "Methods");

            TitledPane titledPane = new TitledPane(name, vbox);
            titledPane.setText(name);
            titledPane.setId(name);
            titledPane.setCollapsible(false);
            titledPane.setPrefHeight(100);
            titledPane.setPrefWidth(100);
            if (classDiagram.findClass(name) != null) {
                if (!undoActive) actionsPerformed.add("added class");
                titledPane.setLayoutX(classDiagram.findClass(name).getXposition());
                titledPane.setLayoutY(classDiagram.findClass(name).getYposition());
                mainPane.getChildren().add(titledPane);
                classCounter++;
                chooseClass.getItems().add(name);
                chooseClass.setValue(name);
                firstClassForRelation.getItems().add(name);
                secondClassForRelation.getItems().add(name);

            }

            titledPane.setOnMousePressed(event -> {
                XpositionOfClass = event.getSceneX();
                YpositionOfClass = event.getSceneY();
                orgTranslateX = ((TitledPane) (event.getSource())).getTranslateX();
                orgTranslateY = ((TitledPane) (event.getSource())).getTranslateY();
            });
            titledPane.setOnMouseDragged(event -> {
                ((TitledPane) (event.getSource())).setTranslateX(orgTranslateX + event.getSceneX() - XpositionOfClass);
                ((TitledPane) (event.getSource())).setTranslateY(orgTranslateY + event.getSceneY() - YpositionOfClass);

                classDiagram.findClass(name).setXposition(((TitledPane) (event.getSource())).getLayoutX() + orgTranslateX + event.getSceneX() - XpositionOfClass);
                classDiagram.findClass(name).setYposition(((TitledPane) (event.getSource())).getLayoutY() + orgTranslateY + event.getSceneY() - YpositionOfClass);

                List<UMLRelation> relations = classDiagram.getClassRelations(name);
                for (int i = 0; i < relations.size(); i++){
                    relations.get(i).updateRelationNamePosition();
                    if(relations.get(i).getFirstClass() != null && relations.get(i).getSecondClass() != null){
                        mainPane.getChildren().removeAll(mainPane.lookupAll("#" + relations.get(i).getFirstClass().getName() + relations.get(i).getSecondClass().getName() + "Relation"));
                        mainPane.getChildren().removeAll(mainPane.lookupAll("#" + relations.get(i).getFirstClass().getName() + relations.get(i).getSecondClass().getName() + "RelationName"));
                    }
                    else if (relations.get(i).getFirstInterface() != null){
                        mainPane.getChildren().removeAll(mainPane.lookupAll("#" + relations.get(i).getFirstInterface().getName() + relations.get(i).getSecondClass().getName() + "Relation"));
                        mainPane.getChildren().removeAll(mainPane.lookupAll("#" + relations.get(i).getFirstInterface().getName() + relations.get(i).getSecondClass().getName() + "RelationName"));
                    }
                    else{
                        mainPane.getChildren().removeAll(mainPane.lookupAll("#" + relations.get(i).getFirstClass().getName() + relations.get(i).getSecondInterface().getName() + "Relation"));
                        mainPane.getChildren().removeAll(mainPane.lookupAll("#" + relations.get(i).getFirstClass().getName() + relations.get(i).getSecondInterface().getName() + "RelationName"));
                    }
                    drawRelation(relations.get(i));
                }
            });
            if (!operatorsLoaded){
                loadOperatorsToChoiceBox();
            }

            if(classDeleteActive) {

                for (int i = 0; i < numberOfAttributesDeleted.get(numberOfAttributesDeleted.size() -1); i++) {

                    classDiagram.findClass(name).addAttribute(attributesOfClassesDeleted.get(attributesOfClassesDeleted.size() -1));

                    Text attribute = new Text(attributesOfClassesDeleted.get(attributesOfClassesDeleted.size() - 1).getName() + ":"
                    + attributesOfClassesDeleted.get(attributesOfClassesDeleted.size() -1).getType().toString().replaceAll("\\([^\\)]*\\)\\s*", ""));

                    attribute.setId(attributesOfClassesDeleted.get(attributesOfClassesDeleted.size() - 1).getName() + "Attr");
                    attributes.getChildren().add(attribute);
                    attributesOfClassesDeleted.remove(attributesOfClassesDeleted.size() - 1);

                }

                for (int i = 0; i < numberOfMethodsDeleted.get(numberOfMethodsDeleted.size() - 1); i++) {

                    classDiagram.findClass(name).addMethod(methodsOfClassesDeleted.get(methodsOfClassesDeleted.size() - 1));
                    Text method = new Text (methodsOfClassesDeleted.get(methodsOfClassesDeleted.size()- 1).getName() + ":"
                            + methodsOfClassesDeleted.get(methodsOfClassesDeleted.size()-  1).getType().toString().replaceAll("\\([^\\)]*\\)\\s*", ""));
                    method.setId(methodsOfClassesDeleted.get(methodsOfClassesDeleted.size() - 1).getName() + "Meth");
                    methods.getChildren().add(method);
                    methodsOfClassesDeleted.remove(methodsOfClassesDeleted.size() -1);
                }
            }
            classesAdded.add(classDiagram.findClass(name));
        }
    }

    @FXML
    private void createClass(ActionEvent e){

        createClassHelpMethod();
    }

    @FXML
    private void createInterface(ActionEvent e){
        String name = newName.getText();
        if (!name.isEmpty() && classDiagram.findInterface(name) == null) {
            if (classDiagram.createInterface(name) != null){
                VBox vbox = new VBox();

                VBox methods = new VBox();
                vbox.getChildren().add(methods);
                methods.setId(name + "Methods");

                TitledPane titledPane = new TitledPane(name, vbox);
                titledPane.setText(name);
                titledPane.setId(name);
                titledPane.setCollapsible(false);
                titledPane.setPrefHeight(60);
                titledPane.setPrefWidth(100);
                if (classDiagram.findInterface(name) != null) {
                    titledPane.setLayoutX(classDiagram.findInterface(name).getXposition());
                    titledPane.setLayoutY(classDiagram.findInterface(name).getYposition());
                    mainPane.getChildren().add(titledPane);
                    classCounter++;
                    chooseClass.getItems().add(name);
                    chooseClass.setValue(name);
                    firstClassForRelation.getItems().add(name);
                    secondClassForRelation.getItems().add(name);
                }

                titledPane.setOnMousePressed(event -> {
                    XpositionOfClass = event.getSceneX();
                    YpositionOfClass = event.getSceneY();
                    orgTranslateX = ((TitledPane) (event.getSource())).getTranslateX();
                    orgTranslateY = ((TitledPane) (event.getSource())).getTranslateY();
                });
                titledPane.setOnMouseDragged(event -> {
                    ((TitledPane) (event.getSource())).setTranslateX(orgTranslateX + event.getSceneX() - XpositionOfClass);
                    ((TitledPane) (event.getSource())).setTranslateY(orgTranslateY + event.getSceneY() - YpositionOfClass);
                    classDiagram.findInterface(name).setXposition(((TitledPane) (event.getSource())).getLayoutX() + orgTranslateX + event.getSceneX() - XpositionOfClass);
                    classDiagram.findInterface(name).setYposition(((TitledPane) (event.getSource())).getLayoutY() + orgTranslateY + event.getSceneY() - YpositionOfClass);

                    List<UMLRelation> relations = classDiagram.getInterfaceRelations(name);
                    for (int i = 0; i < relations.size(); i++){
                        relations.get(i).updateRelationNamePosition();
                        if(relations.get(i).getFirstInterface() != null && relations.get(i).getSecondInterface() != null){
                            mainPane.getChildren().removeAll(mainPane.lookupAll("#" + relations.get(i).getFirstInterface().getName() + relations.get(i).getSecondInterface().getName() + "Relation"));
                            mainPane.getChildren().removeAll(mainPane.lookupAll("#" + relations.get(i).getFirstInterface().getName() + relations.get(i).getSecondInterface().getName() + "RelationName"));

                        }
                        else if (relations.get(i).getFirstClass() != null){
                            mainPane.getChildren().removeAll(mainPane.lookupAll("#" + relations.get(i).getFirstClass().getName() + relations.get(i).getSecondInterface().getName() + "Relation"));
                            mainPane.getChildren().removeAll(mainPane.lookupAll("#" + relations.get(i).getFirstClass().getName() + relations.get(i).getSecondInterface().getName() + "RelationName"));
                        }
                        else{
                            mainPane.getChildren().removeAll(mainPane.lookupAll("#" + relations.get(i).getFirstInterface().getName() + relations.get(i).getSecondClass().getName() + "Relation"));
                            mainPane.getChildren().removeAll(mainPane.lookupAll("#" + relations.get(i).getFirstInterface().getName() + relations.get(i).getSecondClass().getName() + "RelationName"));
                        }

                        drawRelation(relations.get(i));
                    }
                });
                if (!operatorsLoaded){
                    loadOperatorsToChoiceBox();
                }
            }
        }
    }

    public String getJSONFromFile(String filename) {

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

    public void parseJsonToObject(String JSONStr) {

        JSONArray jsonArray = new JSONArray(JSONStr);

        //creating class diagram
        //    ClassDiagram d = new ClassDiagram("Class Model");

        for (int i = 0; i < jsonArray.length(); i++) {

            //iterating over JSON array -> this way we can select all elements and its attributes
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            //in "i" object select the entity key word in JSON -> corresponds to elements name
            String entity = jsonObject.getString("entity");

            String isInterface = jsonObject.getString("interface");

            //creating class in diagram, entity = name of class

            if (isInterface.equals("false")) {

                UMLClass cls = classDiagram.createClass(entity);
                //same as entity, only now we are choosing attributes -> it's an array
                //need to iterate through that one more time
                JSONObject jsonAttributeObject = (JSONObject) jsonObject.get("attributes");

                //we don't know the keyword for attributes -> every element has different names
                //for its attributes -> object.names() solves the issue
                JSONArray keys = jsonAttributeObject.names();

                //iterating through attribute array, on "j" key we store the key (name of our attribute)
                //and value, which is type in our case -> creating new Attribute object that requires UMLClassifier
                //object for its type
                //TO DO - userDefined - usage?


                for (int j = 0; j < keys.length(); j++) {

                    String key = keys.getString(j);
                    String value = jsonAttributeObject.getString(key);

                    //creating attribute
                    UMLAttribute attributeObject = new UMLAttribute(key, classDiagram.classifierForName(value));

                    //adding attribute to class where it belongs
                    cls.addAttribute(attributeObject);

                }

                //methods are the same as attributes mentioned above
                //constructor takes name of the method and value that the method returns
                //TO DO - what about the arguments?

                JSONObject jsonMethodObject = (JSONObject) jsonObject.get("methods");
                JSONArray keys2 = jsonMethodObject.names();


                for (int j = 0; j < keys2.length(); j++) {

                    String key = keys2.getString(j);
                    String value = jsonMethodObject.getString(key);

                    //creating method
                    UMLOperation operationObject = UMLOperation.create(key, classDiagram.classifierForName(value));
                    cls.addMethod(operationObject);

                }

                JSONObject jsonPositionObject = (JSONObject) jsonObject.get("position");
                JSONArray keys3 = jsonPositionObject.names();

                for (int j = 0; j < keys3.length(); j++) {

                    String key = keys3.getString(j);
                    Integer value = jsonPositionObject.getInt(key);

                    //setting position
                    if (key.equals("x")) cls.setXposition(value);
                    else cls.setYposition(value);

                }

                JSONObject jsonRelationObject = (JSONObject) jsonObject.get("relations");
                JSONArray keys4 = jsonRelationObject.names();

                for (int j = 0; j < keys4.length(); j++) {

                    String key = keys4.getString(j);
                    String value = jsonRelationObject.getString(key);

                    //String ve tvaru class1@class2

                    relationsToDisplay.add(key + "@" + value); //tady přidávám každou relaci pro konkrétní třídu do listu
                    //to si pak beru tam, kde je zobrazuju - 532

                }

                showClassToGUI(classDiagram, cls);

                //printing data to output
            } else {

                //interface nemá atributy

                UMLInterface umlInterface = classDiagram.createInterface(entity);

                JSONObject jsonMethodObject = (JSONObject) jsonObject.get("methods");
                JSONArray keys2 = jsonMethodObject.names();


                for (int j = 0; j < keys2.length(); j++) {

                    String key = keys2.getString(j);
                    String value = jsonMethodObject.getString(key);

                    //creating method
                    UMLOperation operationObject = UMLOperation.create(key, classDiagram.classifierForName(value));
                    umlInterface.addMethod(operationObject);

                }

                JSONObject jsonPositionObject = (JSONObject) jsonObject.get("position");
                JSONArray keys3 = jsonPositionObject.names();

                for (int j = 0; j < keys3.length(); j++) {

                    String key = keys3.getString(j);
                    Integer value = jsonPositionObject.getInt(key);

                    //setting position
                    if (key.equals("x")) umlInterface.setXposition(value);
                    else umlInterface.setYposition(value);

                }

                //zde ukládání relací


                showInterfaceToGui(classDiagram, umlInterface);
            }
        }

            //v tento moment existují všechny classy z jsonu - je třeba je napojit relacema

            for (String nameOfRelation : relationsToDisplay) {

                String[] parts = nameOfRelation.split("@");
                String relationName = parts[0];
                String nameOfFirstClass = parts[1];
                String nameOfSecondClass = parts[2];

                // TODO - nejde vytvořit relaci - třídy jsou null, ale už jsou vytvořený a jsou i v classDiagram.getclasses() - how??
                UMLRelation newRelation = classDiagram.createRelation(nameOfFirstClass, nameOfSecondClass, relationName, "Asociation (black)");

                if (newRelation != null) {

                    Line line = new Line();
                    Label label = new Label();

                    label.setId(nameOfFirstClass + nameOfSecondClass + "RelationName");
                    label.setLayoutX(newRelation.getXpositionOfText());
                    label.setLayoutY(newRelation.getYpositionOfText() - 20);
                    label.setText(newRelation.getName());

                    line.setStartX(newRelation.getFirstClass().getXposition() + 55);
                    line.setStartY(newRelation.getFirstClass().getYposition() + 55);
                    line.setEndX(newRelation.getSecondClass().getXposition() + 55);
                    line.setEndY(newRelation.getSecondClass().getYposition() + 55);
                    line.setId(newRelation.getFirstClass().getName() + newRelation.getSecondClass().getName() + "Relation");

                    mainPane.getChildren().add(0, line);
                    mainPane.getChildren().add(1,label);

                }
            }

            System.out.println(relationsToDisplay);

    }

    private void showInterfaceToGui(ClassDiagram d, UMLInterface umlInterface) {
        VBox box = new VBox();


        String name = umlInterface.getName();

        box.getChildren().add(new Separator()); // to jsem ti ukradl

        VBox methods = new VBox();
        box.getChildren().add(methods);
        methods.setId(name + "Methods");

        TitledPane titledPane = new TitledPane(umlInterface.getName(), box);
        titledPane.setId(umlInterface.getName());
        titledPane.setText(umlInterface.getName());
        titledPane.setCollapsible(false);
        titledPane.setPrefHeight(100);
        titledPane.setPrefWidth(100);

        for (int i = 0; i < umlInterface.getMethods().size(); i++) {

            List<UMLOperation> tempArray = umlInterface.getMethods();
            UMLOperation currentOperation = tempArray.get(i);

            String operationString = currentOperation.toString();
            //jenom osekání, aby to bylo korektně zapsaný
            String [] parts = operationString.split(":");
            String operationName = parts[0];
            String type = parts[1];
            type = type.replaceAll("\\([^\\)]*\\)\\s*", "");

            //pokud se to správně nalinkuje tak tady už jen vytvářím místo pro ten text v tom příslušným vboxu
            Text method = new Text (operationName + ":" + type);
            //nastavení id do budoucna, kdy ho budu chtít znát
            method.setId(operationName + "Meth");
            methods.getChildren().add(method);
            chooseMethod.getItems().add(operationName + ":" + type);
        }

        if (d.findInterface(umlInterface.getName()) != null) {
            titledPane.setLayoutX(umlInterface.getXposition());
            titledPane.setLayoutY(umlInterface.getYposition());
            mainPane.getChildren().add(titledPane); //adding interface to main window so its visible
            classCounter++;
            chooseClass.getItems().add(name);
            firstClassForRelation.getItems().add(name);
            secondClassForRelation.getItems().add(name);
        }

        titledPane.setOnMousePressed(event -> {
            XpositionOfClass = event.getSceneX();
            YpositionOfClass = event.getSceneY();
            orgTranslateX = ((TitledPane) (event.getSource())).getTranslateX();
            orgTranslateY = ((TitledPane) (event.getSource())).getTranslateY();
        });
        titledPane.setOnMouseDragged(event -> {
            ((TitledPane) (event.getSource())).setTranslateX(orgTranslateX + event.getSceneX() - XpositionOfClass);
            ((TitledPane) (event.getSource())).setTranslateY(orgTranslateY + event.getSceneY() - YpositionOfClass);

            classDiagram.findInterface(name).setXposition(((TitledPane) (event.getSource())).getLayoutX() + orgTranslateX + event.getSceneX() - XpositionOfClass);
            classDiagram.findInterface(name).setYposition(((TitledPane) (event.getSource())).getLayoutY() + orgTranslateY + event.getSceneY() - YpositionOfClass);

            List<UMLRelation> relations = classDiagram.getInterfaceRelations(name);
            for (int i = 0; i < relations.size(); i++){
                relations.get(i).updateRelationNamePosition();
                if(relations.get(i).getFirstInterface() != null && relations.get(i).getSecondInterface() != null){
                    mainPane.getChildren().removeAll(mainPane.lookupAll("#" + relations.get(i).getFirstInterface().getName() + relations.get(i).getSecondInterface().getName() + "Relation"));
                    mainPane.getChildren().removeAll(mainPane.lookupAll("#" + relations.get(i).getFirstInterface().getName() + relations.get(i).getSecondInterface().getName() + "RelationName"));

                }
                else if (relations.get(i).getFirstClass() != null){
                    mainPane.getChildren().removeAll(mainPane.lookupAll("#" + relations.get(i).getFirstClass().getName() + relations.get(i).getSecondInterface().getName() + "Relation"));
                    mainPane.getChildren().removeAll(mainPane.lookupAll("#" + relations.get(i).getFirstClass().getName() + relations.get(i).getSecondInterface().getName() + "RelationName"));
                }
                else{
                    mainPane.getChildren().removeAll(mainPane.lookupAll("#" + relations.get(i).getFirstInterface().getName() + relations.get(i).getSecondClass().getName() + "Relation"));
                    mainPane.getChildren().removeAll(mainPane.lookupAll("#" + relations.get(i).getFirstInterface().getName() + relations.get(i).getSecondClass().getName() + "RelationName"));
                }

                drawRelation(relations.get(i));
            }
        });
        if (!operatorsLoaded){
            loadOperatorsToChoiceBox();
        }

        //relace ?
    }

    private void showClassToGUI(ClassDiagram d, UMLClass cls) {
        VBox box = new VBox();

        //vbox v classe, ve kterým mají být metody, nastavuju mu id, abych se na něj pak mohl odkázat dole v loopu

        String name = cls.getName();

        VBox attributes = new VBox();
        box.getChildren().add(attributes);
        attributes.setId(name + "Attributes");

        box.getChildren().add(new Separator()); // to jsem ti ukradl

        VBox methods = new VBox();
        box.getChildren().add(methods);
        methods.setId(name + "Methods");

        TitledPane titledPane = new TitledPane(cls.getName(), box);
        titledPane.setId(cls.getName());
        titledPane.setText(cls.getName());
        titledPane.setCollapsible(false);
        titledPane.setPrefHeight(100);
        titledPane.setPrefWidth(100);
        titledPane.setLayoutX(cls.getXposition());
        titledPane.setLayoutY(cls.getYposition());

        //procházím všechny metody dané classy a chci je printnout do toho příslušnýho vboxu, kterej má každá classa
        for (int i = 0; i < cls.getMethods().size(); i++) {

            List<UMLOperation> tempArray = cls.getMethods();
            UMLOperation currentOperation = tempArray.get(i);

            String operationString = currentOperation.toString();
            //jenom osekání, aby to bylo korektně zapsaný
            String [] parts = operationString.split(":");
            String operationName = parts[0];
            String type = parts[1];
            type = type.replaceAll("\\([^\\)]*\\)\\s*", "");

            //pokud se to správně nalinkuje tak tady už jen vytvářím místo pro ten text v tom příslušným vboxu
            Text method = new Text (operationName + ":" + type);
            //nastavení id do budoucna, kdy ho budu chtít znát
            method.setId(operationName + "Meth");
            methods.getChildren().add(method);
            chooseMethod.getItems().add(operationName + ":" + type);

        }

        for (int i = 0; i < cls.getAttributes().size(); i++) {

            List<UMLAttribute> tempArray = cls.getAttributes();
            UMLAttribute currentAttribute = tempArray.get(i);

            String attributeString = currentAttribute.toString();
            String [] parts = attributeString.split(":");
            String attributeName = parts[0];
            String type = parts[1].replaceAll("\\([^\\)]*\\)\\s*", "");

            Text attribute = new Text (attributeName + ":" + type);
            attribute.setId(attributeName + "Attr");
            attributes.getChildren().add(attribute);
            chooseAttribute.getItems().add(attributeName + ":" + type);
        }

        if (d.findClass(cls.getName()) != null) {
            titledPane.setLayoutX(cls.getXposition());
            titledPane.setLayoutY(cls.getYposition());
            mainPane.getChildren().add(titledPane); //adding class to main window so its visible
            classCounter++;
            chooseClass.getItems().add(name);
            firstClassForRelation.getItems().add(name);
            secondClassForRelation.getItems().add(name);
        }

        titledPane.setOnMousePressed(event -> {
            XpositionOfClass = event.getSceneX();
            YpositionOfClass = event.getSceneY();
            orgTranslateX = ((TitledPane) (event.getSource())).getTranslateX();
            orgTranslateY = ((TitledPane) (event.getSource())).getTranslateY();
        });
        titledPane.setOnMouseDragged(event -> {
            ((TitledPane) (event.getSource())).setTranslateX(orgTranslateX + event.getSceneX() - XpositionOfClass);
            ((TitledPane) (event.getSource())).setTranslateY(orgTranslateY + event.getSceneY() - YpositionOfClass);

            classDiagram.findClass(name).setXposition(((TitledPane) (event.getSource())).getLayoutX() + orgTranslateX + event.getSceneX() - XpositionOfClass);
            classDiagram.findClass(name).setYposition(((TitledPane) (event.getSource())).getLayoutY() + orgTranslateY + event.getSceneY() - YpositionOfClass);

            List<UMLRelation> relations = classDiagram.getClassRelations(name);
            for (int i = 0; i < relations.size(); i++){
                relations.get(i).updateRelationNamePosition();
                if(relations.get(i).getFirstClass() != null && relations.get(i).getSecondClass() != null){
                    mainPane.getChildren().removeAll(mainPane.lookupAll("#" + relations.get(i).getFirstClass().getName() + relations.get(i).getSecondClass().getName() + "Relation"));
                    mainPane.getChildren().removeAll(mainPane.lookupAll("#" + relations.get(i).getFirstClass().getName() + relations.get(i).getSecondClass().getName() + "RelationName"));
                }
                else if (relations.get(i).getFirstInterface() != null){
                    mainPane.getChildren().removeAll(mainPane.lookupAll("#" + relations.get(i).getFirstInterface().getName() + relations.get(i).getSecondClass().getName() + "Relation"));
                    mainPane.getChildren().removeAll(mainPane.lookupAll("#" + relations.get(i).getFirstInterface().getName() + relations.get(i).getSecondClass().getName() + "RelationName"));
                }
                else{
                    mainPane.getChildren().removeAll(mainPane.lookupAll("#" + relations.get(i).getFirstClass().getName() + relations.get(i).getSecondInterface().getName() + "Relation"));
                    mainPane.getChildren().removeAll(mainPane.lookupAll("#" + relations.get(i).getFirstClass().getName() + relations.get(i).getSecondInterface().getName() + "RelationName"));
                }
                drawRelation(relations.get(i));
            }
        });
        if (!operatorsLoaded){
            loadOperatorsToChoiceBox();
        }
    }


    @FXML
    private void deleteClass(ActionEvent e){

        deleteClassHelp();
    }

    //TODO remove deleted class from relations list
    //TODO delete realtion when class deleted
    //TODO update seqDia classes?
    private void deleteClassHelp() {

        String nameOfRemovedClass;

        if (undoingClass) nameOfRemovedClass = editedClass.get(editedClass.size() - 1).getName();

        else if (undoActive) nameOfRemovedClass = classesAdded.get(classesAdded.size() - 1).getName();

        else nameOfRemovedClass = chooseClass.getValue();
        //TODO relace

        if (classDiagram.findClass(nameOfRemovedClass) != null){

            if (!classDiagram.findClass(nameOfRemovedClass).getAttributes().isEmpty()) {

                List<UMLAttribute> arr = classDiagram.findClass(nameOfRemovedClass).getAttributes();

                for (int i = 0; i < arr.size(); i++) {

                    UMLAttribute attribute = arr.get(i);
                    classDiagram.findClass(nameOfRemovedClass).removeAttribute(attribute);
                    if(!editingClass) attributesOfClassesDeleted.add(attribute);
                    i--;
                    if (classDiagram.findClass(nameOfRemovedClass).getAttributes().isEmpty()) break;
                }

            }

            if (!classDiagram.findClass(nameOfRemovedClass).getMethods().isEmpty()) {

                List<UMLOperation> arr2 = classDiagram.findClass(nameOfRemovedClass).getMethods();

                for (int i = 0; i < arr2.size(); i++) {

                    UMLOperation operation = arr2.get(i);
                    classDiagram.findClass(nameOfRemovedClass).removeMethod(operation);
                    if (!editingClass) methodsOfClassesDeleted.add(operation);
                    i--;
                    if (classDiagram.findClass(nameOfRemovedClass).getMethods().isEmpty()) break;
                }
            }

            //relace
            UMLClass currentClass = classDiagram.findClass(nameOfRemovedClass);
            System.out.println(currentClass.getRelations());

            for (int i = 0; i < currentClass.getRelations().size(); i++) {

                    UMLRelation relation = currentClass.getRelations().get(i);
                    //smazat teď v gui
                    classDiagram.deleteRelation(relation);
                    mainPane.getChildren().remove(mainPane.lookup("#" + relation.getFirstClass().getName() + relation.getSecondClass().getName() + "Relation"));
                    i--;
                    if (currentClass.getRelations().isEmpty()) break; //deleted from backend

            }

            System.out.println(currentClass.getRelations());

            if (!editingClass) classesDeleted.add(classDiagram.findClass(nameOfRemovedClass)); //UNDO
            classDiagram.deleteClass(classDiagram.findClass(nameOfRemovedClass));
            mainPane.getChildren().remove(mainPane.lookup("#" + nameOfRemovedClass));
            chooseClass.getItems().remove(nameOfRemovedClass);

            if(!undoActive && !editingClass) actionsPerformed.add("removed class");

            if (!editingClass) {

                numberOfAttributesDeleted.add(attributesOfClassesDeleted.size());
                numberOfMethodsDeleted.add(methodsOfClassesDeleted.size());
            }
        }
        else{
            if (!classDiagram.findInterface(nameOfRemovedClass).getMethods().isEmpty()) {

                List<UMLOperation> arr2 = classDiagram.findInterface(nameOfRemovedClass).getMethods();

                for (int i = 0; i < arr2.size(); i++) {

                    UMLOperation operation = arr2.get(i);
                    classDiagram.findInterface(nameOfRemovedClass).removeMethod(operation);
                    //TODO Undo needs check
//                    methodsOfClassesDeleted.add(operation);
                    i--;
                    if (classDiagram.findInterface(nameOfRemovedClass).getMethods().isEmpty()) break;
                }
            }
//            classesDeleted.add(classDiagram.findClass(nameOfRemovedClass)); //UNDO
            classDiagram.deleteInterface(classDiagram.findInterface(nameOfRemovedClass));
            mainPane.getChildren().remove(mainPane.lookup("#" + nameOfRemovedClass));
            chooseClass.getItems().remove(nameOfRemovedClass);

            if(!undoActive) actionsPerformed.add("removed class");

            numberOfAttributesDeleted.add(attributesOfClassesDeleted.size());
            numberOfMethodsDeleted.add(methodsOfClassesDeleted.size());
        }
        firstClassForRelation.getItems().remove(nameOfRemovedClass);
        secondClassForRelation.getItems().remove(nameOfRemovedClass);
    }

    @FXML
    private void addAttribute(ActionEvent e){

        addAttributeHelp();
    }

    private void addAttributeHelp() {


        if (chooseClass.getValue() != null || classDeleteActive){

            String name, type;
            UMLClass chosenClass;

            if (classDeleteActive) {

                name = attributesOfClassesDeleted.get(attributesOfClassesDeleted.size() - 1).getName();
                type = attributesOfClassesDeleted.get(attributesOfClassesDeleted.size() - 1).getType().toString().replaceAll("\\([^\\)]*\\)\\s*", "");
                chosenClass = classesDeleted.get(classesDeleted.size() - 1);
            }

            else if (undoActive) {

                name = attributesDeleted.get(attributesDeleted.size() - 1).getName();
                type = attributesDeleted.get(attributesDeleted.size() - 1).getType().toString().replaceAll("\\([^\\)]*\\)\\s*", "");
                chosenClass = classOfAttributeDeleted.get(classOfAttributeDeleted.size() - 1);

            }

            else {
                chosenClass = classDiagram.findClass(chooseClass.getValue());
                name = attAndMethText.getText();
                type = typeText.getText();
            }

            if ((!name.isEmpty() && !type.isEmpty()) && chosenClass != null && !chosenClass.getNamesOfAttributes().contains(name)) {

                if (!undoActive) actionsPerformed.add("added attribute");
                UMLAttribute newAttribute = new UMLAttribute(name, classDiagram.classifierForName(type));
                chosenClass.addAttribute(newAttribute);
                VBox attributes = (VBox) mainPane.lookup("#" + chosenClass.getName() + "Attributes");
                Text attribute = new Text(chooseOperator.getValue() + name + ":" + type);
                attribute.setId(name + "Attr");
                attributes.getChildren().add(attribute);
                listOfAttributes.add(name);
                chooseAttribute.getItems().add(attribute.getText().substring(1));
                attributesAdded.add(newAttribute); //UNDO
                classOfAttributeAdded.add(chosenClass); //UNDO
            }
        }
    }

    @FXML
    private void addMethod(ActionEvent e){

        addMethodHelp();

    }

    private void addMethodHelp () {

        if (chooseClass.getValue() != null && (classDiagram.findClass(chooseClass.getValue()) != null)){
            UMLClass chosenClass = classDiagram.findClass(chooseClass.getValue());
            String name = attAndMethText.getText();
            String type = typeText.getText();


            if ((!name.isEmpty() && !type.isEmpty()) && !chosenClass.getNamesOfMethods().contains(name)) {
                if (!undoActive) actionsPerformed.add("added method");
                UMLOperation newMethod = new UMLOperation(name, classDiagram.classifierForName(type));
                chosenClass.addMethod(newMethod);
                VBox methods = (VBox) mainPane.lookup("#" + chooseClass.getValue() + "Methods");
                Text method = new Text(chooseOperator.getValue() + name + ":" + type);
                method.setId(name + "Meth");
                if (classDiagram.findClass(chooseClass.getValue()).getRelations() != null){
                    List<UMLRelation> relations = classDiagram.findClass(chooseClass.getValue()).getRelations();
                    for (int i = 0; i < relations.size(); i++){
                        if (relations.get(i).getSecondClass().equals(classDiagram.findClass(chooseClass.getValue()))){
                            if (relations.get(i).getFirstClass().getNamesOfMethods().contains(name)){
                                method.setStroke(Color.PURPLE);
                            }
                        }

                    }
                }
                methods.getChildren().add(method);
                chooseMethod.getItems().add(method.getText().substring(1));
                listOfMethods.add(name);
                classOfMethodAdded.add(chosenClass); //UNDO
                methodsAdded.add(newMethod); //UNDO

            }
        }
        else if(chooseClass.getValue() != null && (classDiagram.findInterface(chooseClass.getValue()) != null)){
            UMLInterface chosenInterface = classDiagram.findInterface(chooseClass.getValue());
            String name = attAndMethText.getText();
            String type = typeText.getText();
            if ((!name.isEmpty() && !type.isEmpty()) && !chosenInterface.getNamesOfMethods().contains(name)) {
//                if (!undoActive) actionsPerformed.add("added method");
                UMLOperation newMethod = new UMLOperation(name, classDiagram.classifierForName(type));
                chosenInterface.addMethod(newMethod);
                VBox methods = (VBox) mainPane.lookup("#" + chooseClass.getValue() + "Methods");
                Text method = new Text(chooseOperator.getValue() + name + ":" + type);
                method.setId(name + "Meth");
                methods.getChildren().add(method);
//                methodsAdded.add(newMethod); //UNDO
                //classOfMethodAdded.add(chosenInterface); //UNDO not ready for interfaces
            }
        }

    }

    @FXML
    private void removeMethod(ActionEvent e) {

            removeMethodHelp();
    }

    private void removeMethodHelp () {

        String name, type;
        UMLClass chosenClass;


        if (chooseClass.getValue() != null) {

            if (undoActive == true) {

                chosenClass = classOfMethodAdded.get(classOfMethodAdded.size() - 1);
                name = methodsAdded.get(methodsAdded.size() - 1).getName();
                type = methodsAdded.get(methodsAdded.size() - 1).getType().toString().replaceAll("\\([^\\)]*\\)\\s*", "");

            }
            else {
                chosenClass = classDiagram.findClass(chooseClass.getValue());
                name = attAndMethText.getText();
                type = typeText.getText();
            }

            List<UMLOperation> operationList = chosenClass.getMethods();
            for (UMLOperation operation : operationList) {

                String operationString = operation.toString();
                String [] parts = operationString.split(":");
                String compareType = parts[1].replaceAll("\\([^\\)]*\\)\\s*", "");
                String compareName = parts[0];

                if (name.equals(compareName) && type.equals(compareType)) {

                    if(!undoActive == true) actionsPerformed.add("removed method");

                    chosenClass.removeMethod(operation); //deleted from backend data
                    listOfMethods.remove(operation.getName());

                    VBox methods = (VBox) mainPane.lookup("#" + chosenClass.getName() + "Methods");
                    methods.getChildren().remove(methods.lookup("#" + name + "Meth"));
                    methodsDeleted.add(operation);
                    classOfMethodDeleted.add(chosenClass);
                    chooseMethod.getItems().remove(name + ":" + type);
                    break;
                }
            }

        }

    }

    @FXML
    private void removeAttribute (ActionEvent e) {

        removeAttributeHelp();
    }

    private void removeAttributeHelp () {

        String name, type;
        UMLClass chosenClass;

        if (chooseClass.getValue() != null) {

            if (undoActive == true) {

                name = attributesAdded.get(attributesAdded.size() - 1).getName();
                type = attributesAdded.get(attributesAdded.size() - 1).getType().toString().replaceAll("\\([^\\)]*\\)\\s*", "");
                chosenClass = classOfAttributeAdded.get(classOfAttributeAdded.size() - 1);
            }
            else {
                chosenClass = classDiagram.findClass(chooseClass.getValue());
                name = attAndMethText.getText();
                type = typeText.getText();
            }
            System.out.println(classDiagram.getClasses());
            List<UMLAttribute> attributeList = chosenClass.getAttributes(); //TADY TO DÁVÁ 0 PŘI UNDO Z EDITU CLASSE
            for (UMLAttribute attribute : chosenClass.getAttributes()) {

                String attributeString = attribute.toString();
                String [] parts = attributeString.split(":");
                String compareType = parts[1].replaceAll("\\([^\\)]*\\)\\s*", "");
                String compareName = parts[0];

                if (name.equals(compareName) && type.equals(compareType)) {

                    if (!undoActive == true) actionsPerformed.add("removed attribute");
                    chosenClass.removeAttribute(attribute);
                    listOfAttributes.remove(attribute.getName());

                    VBox attributes = (VBox) mainPane.lookup("#" + chosenClass.getName() + "Attributes");
                    attributes.getChildren().remove(attributes.lookup("#" + name + "Attr"));

                    attributesDeleted.add(attribute);
                    classOfAttributeDeleted.add(chosenClass);
                    chooseAttribute.getItems().remove(name + ":" + type);
                    break;
                }
            }

        }
    }

    @FXML
    public void editAttribute(ActionEvent e) {

        //musí být zvolená classa, abych věděl, kde hledat
        //prvně atribut vytáhnu z menu - podívám se, jestli pro danou classu existuje a odstraním
        //potom ho musím přidat do stejné classy s novýma datama z textfieldů

        if (chooseClass.getValue() != null && chooseAttribute != null && editName != null && editType != null) {

            String className = chooseClass.getValue();
            UMLClass currentClass = classDiagram.findClass(className);
            String newName = editName.getText();
            String newType = editType.getText();
            String attributeInformation = chooseAttribute.getValue();

            String [] parts = attributeInformation.split(":");
            String name = parts[0];
            String type = parts[1];

            boolean removed = false;

            for (UMLAttribute attribute : currentClass.getAttributes()){

                    String compareType = attribute.getType().toString().replaceAll("\\([^\\)]*\\)\\s*", "");
                    String compareName = attribute.getName();

                    //remove if the attributes are matching
                    if(name.equals(compareName) && type.equals(compareType)) {

                        removed = true;
                        currentClass.removeAttribute(attribute);
                        listOfAttributes.remove(attribute.getName());

                        VBox attributes = (VBox) mainPane.lookup("#" + currentClass.getName() + "Attributes");
                        attributes.getChildren().remove(attributes.lookup("#" + name + "Attr"));
                        chooseAttribute.getItems().remove(name + ":" + type);
                        //uložit old atribut do pole při undo
                        editedAttributes.add(attribute); //po zavolání funkce bude zeditovaný prvek na předposledním místě
                        break;

                    }
            }

            //if attribute was removed we need to add the new version to the same class
            if (removed) {

                UMLAttribute newAttribute = new UMLAttribute(newName, classDiagram.classifierForName(newType));
                currentClass.addAttribute(newAttribute);
                VBox attributes = (VBox) mainPane.lookup("#" + currentClass.getName() + "Attributes");
                Text attribute = new Text(chooseOperator.getValue() + newName + ":" + newType);
                attribute.setId(newName + "Attr");
                attributes.getChildren().add(attribute);
                chooseAttribute.getItems().add(newName + ":" + newType);
                actionsPerformed.add("edited attribute");
                editedAttributes.add(newAttribute); //na posledním místě v listu je nový zeditovaný prvek
                classOfEditedAttribute.add(currentClass);
            }

        }

    }

    @FXML
    public void editMethod(ActionEvent e) {

        if (chooseMethod.getValue() != null && chooseAttribute != null && editName != null && editType != null) {

            String className = chooseClass.getValue();
            UMLClass currentClass = classDiagram.findClass(className);
            String newName = editName.getText();
            String newType = editType.getText();
            String attributeInformation = chooseMethod.getValue();

            String [] parts = attributeInformation.split(":");
            String name = parts[0];
            String type = parts[1];

            boolean removed = false;

            for (UMLOperation operation : currentClass.getMethods()) {


                String compareType = operation.getType().toString().replaceAll("\\([^\\)]*\\)\\s*", "");
                String compareName = operation.getName();

                //remove if the methods are matching
                if(name.equals(compareName) && type.equals(compareType)) {

                    removed = true;
                    currentClass.removeMethod(operation);
                    listOfMethods.remove(operation.getName());
                    VBox methods = (VBox) mainPane.lookup("#" + currentClass.getName() + "Methods");
                    methods.getChildren().remove(methods.lookup("#" + name + "Meth"));
                    chooseMethod.getItems().remove(name + ":" + type);
                    editedMethods.add(operation); //předposlední prvek bude operace před editem
                    break;

                }
            }
            //if removed add new method
            if (removed) {

                UMLOperation newOperation = new UMLOperation(newName, classDiagram.classifierForName(newType));
                currentClass.addMethod(newOperation);
                listOfMethods.add(newOperation.getName());
                VBox methods = (VBox) mainPane.lookup("#" + currentClass.getName() + "Methods");
                Text method = new Text(chooseOperator.getValue() + newName + ":" + newType);
                method.setId(newName + "Meth");
                methods.getChildren().add(method);
                editedMethods.add(newOperation); //nový prvek (po editu) bude poslední v listu
                chooseMethod.getItems().add(newName + ":" + newType);
                classOfEditedMethod.add(currentClass);
            }

            actionsPerformed.add("edited method");
        }

    }

    @FXML
    private void editClass (ActionEvent e) {

        //classa, kterou vyberu se musí vymazat - s tím i atributy + metody, který si uložím bokem
        //potom vytvořím classu s novým jménem, ale přidělím mu ty uložený atributy + metody

        if (chooseClass.getValue() != null) {


            String className = chooseClass.getValue();
            UMLClass classToEdit = classDiagram.findClass(className);
            String newClassName = editClassText.getText();

            List<UMLAttribute> attributesOfClass = new ArrayList<>();
            List<UMLOperation> operationsOfClass = new ArrayList<>();

            for (UMLAttribute attribute : classToEdit.getAttributes()) attributesOfClass.add(attribute);

            for (UMLOperation operation : classToEdit.getMethods())  operationsOfClass.add(operation);

            editedClass.add(classToEdit); // stará třída skrz undo bude předposlední v seznamu

            editingClass = true;
            deleteClassHelp(); //odstraní classu a všechny její věci
            editingClass = false;
            //teď založit classu se stejnýma atributama + metodama a displaynout do GUI

            UMLClass newClass = classDiagram.createClass(newClassName);

            for (UMLAttribute attribute : attributesOfClass) {

                UMLAttribute attributeObject = new UMLAttribute(attribute.getName(), classDiagram.classifierForName(attribute.getType().toString()));
                newClass.addAttribute(attributeObject);
            }

            for (UMLOperation operation : operationsOfClass) {

                UMLOperation operationObject = UMLOperation.create(operation.getName(), classDiagram.classifierForName(operation.getType().toString()));
                newClass.addMethod(operationObject);

            }

            //TODO RELACE

            showClassToGUI(classDiagram, newClass);
            actionsPerformed.add("edited class");
            editedClass.add(newClass); // skrz undo, nově vytvořená třída je po editu na předposledním místě v listu UNDO
            chooseClass.getItems().remove(className);
        }

    }

    private JSONObject interfaceToJsonObject (UMLInterface umlInterface) {

        JSONObject object = new JSONObject();
        JSONObject position = new JSONObject();
        JSONObject methods = new JSONObject();

        position.put("x", umlInterface.getXposition());
        position.put("y", umlInterface.getYposition());

        List<UMLOperation> operationList = umlInterface.getMethods();
        for (UMLOperation operation : operationList) {

            String operationString = operation.toString();
            String [] parts = operationString.split(":");
            String operationName = parts[0];
            String type = parts[1];
            type = type.replaceAll("\\([^\\)]*\\)\\s*", "");
            methods.put(operationName, type);
        }


        object.put("position", position);
        object.put("methods", methods);
        object.put("entity", umlInterface.getName());
        object.put("interface", "true");
        return object;

    }

    private JSONObject classToJsonObject (UMLClass cls) {

        JSONObject object = new JSONObject();
        JSONObject position = new JSONObject();
        JSONObject methods = new JSONObject();
        JSONObject attributes = new JSONObject();

        //methods n attributes cannot be empty atm - TODO

        position.put("x", cls.getXposition());
        position.put("y", cls.getYposition());

        List<UMLOperation> operationList = cls.getMethods();
        for (UMLOperation operation : operationList) {

            String operationString = operation.toString();
            String [] parts = operationString.split(":");
            String operationName = parts[0];
            String type = parts[1];
            type = type.replaceAll("\\([^\\)]*\\)\\s*", "");
            methods.put(operationName, type);
        }

        List<UMLAttribute> attributeList = cls.getAttributes();
        for (UMLAttribute attribute : attributeList) {

            String attributeString = attribute.toString();
            String [] parts = attributeString.split(":");
            String attributeName = parts[0];
            String type = parts[1].replaceAll("\\([^\\)]*\\)\\s*", "");
            attributes.put(attributeName, type);
        }


        object.put("position", position);
        object.put("methods", methods);
        object.put("attributes", attributes);
        object.put("interface", "false");
        object.put("entity", cls.getName());
        return object;

    }

    @FXML
    private void saveJsonFile(ActionEvent e) {
    //TODO nejdou otevrit ulozene soubory - nesmí být prázdný metody a atributy

        String FilePath;
        FileChooser chooseFile = new FileChooser();
        chooseFile.setInitialDirectory(new File(System.getProperty("user.home")));
        chooseFile.setTitle("Save file");
        chooseFile.getExtensionFilters().add(new FileChooser.ExtensionFilter("json file", "*.json"));
        chooseFile.setInitialFileName(classDiagram.getName());

        try {
            File selectedFile = chooseFile.showSaveDialog(new Stage());

            if (selectedFile != null) {

                List<UMLClass> classList = classDiagram.getClasses();
                List<UMLInterface> interfaceList = classDiagram.getInterfaces();
                JSONArray arr = new JSONArray();
                FilePath = selectedFile.getAbsolutePath();

                for (UMLClass umlClass : classList) {

                    arr.put(classToJsonObject(umlClass));
                }

                for (UMLInterface umlInterface : interfaceList) {

                    arr.put(interfaceToJsonObject(umlInterface));

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

    @FXML
    private void undoAction (ActionEvent e) {

        //all possibilities that user can do - adding/removing/editing - attributes/methods/classes + relations when done
        //loading file -> undo -> new screen (deleting everything that has loaded)


        if (!actionsPerformed.isEmpty()) {

            //1. attribute was removed - needs to be added
            undoActive = true;
            System.out.println(actionsPerformed);

            if (actionsPerformed.get(actionsPerformed.size() - 1).equals("removed attribute")) {

                addAttributeHelp();
                attributesDeleted.remove(attributesDeleted.size() - 1);
                classOfAttributeDeleted.remove(classOfAttributeDeleted.size() - 1);

            }

            //2. attribute was added - needs to be removed

            else if (actionsPerformed.get(actionsPerformed.size() - 1).equals("added attribute")) {

                removeAttributeHelp();
                attributesAdded.remove(attributesAdded.size() - 1);
                classOfAttributeAdded.remove(classOfAttributeAdded.size() - 1);

            }

            //3. method was removed - needs to be added

            else if (actionsPerformed.get(actionsPerformed.size() - 1).equals("removed method")) {

                addMethodHelp();
                methodsDeleted.remove(methodsDeleted.size() - 1);
                classOfMethodDeleted.remove(classOfMethodDeleted.size() - 1);

            }

            //4. method was added - needs to be removed

            else if (actionsPerformed.get(actionsPerformed.size() - 1).equals("added method")) {

                removeMethodHelp();
                methodsAdded.remove(methodsAdded.size() - 1);
                classOfMethodAdded.remove(classOfMethodAdded.size() - 1);
            }

            //5. class was removed - needs to be added with attributes n methods

            else if (actionsPerformed.get(actionsPerformed.size() - 1).equals("removed class")) {

                classDeleteActive = true;
                createClassHelpMethod();

                classesDeleted.remove(classesDeleted.size() - 1);
                numberOfMethodsDeleted.remove(numberOfMethodsDeleted.size() -1);
                numberOfAttributesDeleted.remove(numberOfAttributesDeleted.size() -1);
                classDeleteActive = false;
            }

            //6. class was added - needs to be removed with attributes n methods

            else if (actionsPerformed.get(actionsPerformed.size() - 1).equals("added class")) {

                deleteClassHelp();
                classesAdded.remove(classesAdded.size() - 1);
            }

            //7. attribute was edited - we need to get old data - before attribute was changed and set it as current
            //basically im removing new one and adding old one

            else if (actionsPerformed.get(actionsPerformed.size() - 1).equals("edited attribute")) {

                //remove
                UMLAttribute attributeToRemove = editedAttributes.get(editedAttributes.size() -1);
                UMLClass classOfAttributeToRemove = classOfEditedAttribute.get(classOfEditedAttribute.size() - 1);
                listOfAttributes.remove(attributeToRemove.getName());

                VBox attributes = (VBox) mainPane.lookup("#" + classOfAttributeToRemove.getName() + "Attributes");
                attributes.getChildren().remove(attributes.lookup("#" + attributeToRemove.getName() + "Attr"));
                chooseAttribute.getItems().remove(attributeToRemove.getName() + ":" + attributeToRemove.getType().toString().replaceAll("\\([^\\)]*\\)\\s*", ""));
                //add

                UMLAttribute newAttribute = new UMLAttribute(editedAttributes.get(editedAttributes.size() - 2).getName(),
                        classDiagram.classifierForName(editedAttributes.get(editedAttributes.size() - 2).getType().toString()));
                classOfAttributeToRemove.addAttribute(newAttribute);
                VBox attributes2 = (VBox) mainPane.lookup("#" + classOfAttributeToRemove.getName() + "Attributes");
                Text attribute = new Text(chooseOperator.getValue() + newAttribute.getName() + ":" + newAttribute.getType().toString().replaceAll("\\([^\\)]*\\)\\s*", ""));
                attribute.setId(newAttribute.getName() + "Attr");
                attributes2.getChildren().add(attribute);
                chooseAttribute.getItems().add(newAttribute.getName() + ":" + newAttribute.getType().toString().replaceAll("\\([^\\)]*\\)\\s*", ""));
                listOfAttributes.add(newAttribute.getName());

                for (int i = 0; i < 2; i++) editedAttributes.remove(editedAttributes.size() -1);
                classOfEditedAttribute.remove(classOfEditedAttribute.size() -1);

            }


            //8. method was edited - same as above only for methods - doesnt change back

            else if (actionsPerformed.get(actionsPerformed.size() - 1).equals("edited method")) {

                //remove
                UMLOperation operationToRemove = editedMethods.get(editedMethods.size() - 1);
                UMLClass classOfMethodToRemove = classOfEditedMethod.get(classOfEditedMethod.size() - 1);
                listOfMethods.remove(operationToRemove.getName());
                VBox methods = (VBox) mainPane.lookup("#" + classOfMethodToRemove.getName() + "Methods");
                methods.getChildren().remove(methods.lookup("#" + operationToRemove.getName() + "Meth"));
                chooseMethod.getItems().remove(operationToRemove.getName() + ":" + operationToRemove.getType().toString().replaceAll("\\([^\\)]*\\)\\s*", ""));

                //add
                UMLOperation newOperation = new UMLOperation(editedMethods.get(editedMethods.size() - 2).getName(),
                        classDiagram.classifierForName(editedMethods.get(editedMethods.size() - 2).getType().toString()));
                classOfMethodToRemove.addMethod(newOperation);
                VBox methods2 = (VBox) mainPane.lookup("#" + classOfMethodToRemove.getName() + "Methods");
                Text method = new Text(chooseOperator.getValue() + newOperation.getName() + ":" + newOperation.getType().toString().replaceAll("\\([^\\)]*\\)\\s*", ""));
                method.setId(newOperation.getName() + "Meth");
                methods2.getChildren().add(method);
                chooseMethod.getItems().add(newOperation.getName() + ":" + newOperation.getType().toString().replaceAll("\\([^\\)]*\\)\\s*", ""));
                listOfMethods.add(newOperation.getName());

                for (int i = 0; i < 2; i++) editedMethods.remove(editedMethods.size() -1); //removes both old and new edited methods
                classOfEditedMethod.remove(classOfEditedMethod.size() -1);
            }

            //9. TODO - class was edited
            // editoval jsem classu - tzn. musím odstranit novou a zobrazit do gui starou

            else if (actionsPerformed.get(actionsPerformed.size() - 1).equals("edited class")) {

                undoingClass = true;
                deleteClassHelp(); //smažu novou classu

                //přidám starou
                String nameOfClassToAdd = editedClass.get(editedClass.size() -2).getName();
                classDiagram.createClass(nameOfClassToAdd);

                //přidat atributy a metody ze staré classy
                //když volám deleteClassHelp tak se ukládají odstraněný atributy a metody do attributesOfClassesDeleted

                for (int i =0; i < numberOfAttributesDeleted.size(); i++) {

                    classDiagram.findClass(nameOfClassToAdd).addAttribute(attributesOfClassesDeleted.get(attributesOfClassesDeleted.size() -1));
                    attributesOfClassesDeleted.remove(attributesOfClassesDeleted.size() - 1);
                }

                System.out.println(classDiagram.findClass(nameOfClassToAdd).getAttributes());
                classesDeleted.remove(classesDeleted.size() - 1);
                numberOfMethodsDeleted.remove(numberOfMethodsDeleted.size() -1);
                numberOfAttributesDeleted.remove(numberOfAttributesDeleted.size() -1);

                for (int i = 0; i < 2; i++) editedClass.remove(editedClass.size() -1);
                undoingClass = false;
                showClassToGUI(classDiagram, classDiagram.findClass(nameOfClassToAdd));
            }
            actionsPerformed.remove(actionsPerformed.size() - 1);
            undoActive = false;
        }
    }

    //TODO relations can be created only between 2 classes, not interface
    //TODO show names of realtions
    @FXML
    private void createRelation(ActionEvent e){
        String newRelName = relationName.getText();
        if (classDiagram.findInterface(firstClassForRelation.getValue()) != null && classDiagram.findInterface(secondClassForRelation.getValue()) != null){
            if (!newRelName.isEmpty() && !Objects.equals(firstClassForRelation.getValue(), secondClassForRelation.getValue())) {
                UMLRelation relace = classDiagram.createRelation(firstClassForRelation.getValue(), secondClassForRelation.getValue(), newRelName, chooseRelationType.getValue());
                if (relace != null) {
                    drawRelation(relace);
                }
            }
        }
        else if (classDiagram.findClass(firstClassForRelation.getValue()) != null && classDiagram.findClass(secondClassForRelation.getValue()) != null){
            if (!newRelName.isEmpty() && !Objects.equals(firstClassForRelation.getValue(), secondClassForRelation.getValue())) {
                UMLRelation relace = classDiagram.createRelation(firstClassForRelation.getValue(), secondClassForRelation.getValue(), newRelName, chooseRelationType.getValue());
                if (relace != null) {
                    drawRelation(relace);
                }
            }
        }
        else if (classDiagram.findClass(firstClassForRelation.getValue()) != null && classDiagram.findInterface(secondClassForRelation.getValue()) != null){
            if (!newRelName.isEmpty()) {
                UMLRelation relace = classDiagram.createRelation(firstClassForRelation.getValue(), secondClassForRelation.getValue(), newRelName, chooseRelationType.getValue());
                if (relace != null) {
                    drawRelation(relace);
                }
            }
        }
        else if (classDiagram.findInterface(firstClassForRelation.getValue()) != null && classDiagram.findClass(secondClassForRelation.getValue()) != null){
            if (!newRelName.isEmpty()) {
                UMLRelation relace = classDiagram.createRelation(firstClassForRelation.getValue(), secondClassForRelation.getValue(), newRelName, chooseRelationType.getValue());
                if (relace != null) {
                    drawRelation(relace);
                }
            }
        }
    }

    private void drawRelation(UMLRelation relation){
        Line line = new Line();
        Label label = new Label();
        if (relation.getFirstClass() != null){
            line.setStartX(relation.getFirstClass().getXposition() + 55);
            line.setStartY(relation.getFirstClass().getYposition() + 55);
            if (relation.getSecondClass() != null){
                line.setId(relation.getFirstClass().getName() + relation.getSecondClass().getName() + "Relation");
                label.setId(relation.getFirstClass().getName() + relation.getSecondClass().getName() + "RelationName");
            }
        }
        else{
            line.setStartX(relation.getFirstInterface().getXposition() + 55);
            line.setStartY(relation.getFirstInterface().getYposition() + 55);
            if (relation.getSecondInterface() != null){
                line.setId(relation.getFirstInterface().getName() + relation.getSecondInterface().getName() + "Relation");
                label.setId(relation.getFirstInterface().getName() + relation.getSecondInterface().getName() + "RelationName");
            }
        }
        if (relation.getSecondClass() != null){
            line.setEndX(relation.getSecondClass().getXposition() + 55);
            line.setEndY(relation.getSecondClass().getYposition() + 55);
            if (relation.getFirstInterface() != null){
                line.setId(relation.getFirstInterface().getName() + relation.getSecondClass().getName() + "Relation");
                label.setId(relation.getFirstInterface().getName() + relation.getSecondClass().getName() + "RelationName");
            }
        }
        else {
            line.setEndX(relation.getSecondInterface().getXposition() + 55);
            line.setEndY(relation.getSecondInterface().getYposition() + 55);
            if (relation.getFirstClass() != null){
                line.setId(relation.getFirstClass().getName() + relation.getSecondInterface().getName() + "Relation");
                label.setId(relation.getFirstClass().getName() + relation.getSecondInterface().getName() + "RelationName");
            }
        }
        label.setLayoutX(relation.getXpositionOfText());
        label.setLayoutY(relation.getYpositionOfText() - 20);
        label.setText(relation.getName());

        switch (relation.getType()){
            case "Asociation (black)":
                line.setStroke(Color.BLACK);
                break;
            case "Generalization (purple)":
                line.setStroke(Color.PURPLE);
                break;
            case "Aggregation (green)":
                line.setStroke(Color.GREEN);
                break;
            case "Composion (blue)":
                line.setStroke(Color.BLUE);
                break;
        }

        mainPane.getChildren().add(0,line);
        mainPane.getChildren().add(1, label);
    }

    @FXML
    private void deleteRelation(ActionEvent e){
        if (classDiagram.findRelation(firstClassForRelation.getValue(), secondClassForRelation.getValue()) != null){
            classDiagram.deleteRelation(classDiagram.findRelation(firstClassForRelation.getValue(), secondClassForRelation.getValue()));
            mainPane.getChildren().remove(mainPane.lookup("#" + firstClassForRelation.getValue() + secondClassForRelation.getValue() + "Relation"));
            mainPane.getChildren().remove(mainPane.lookup("#" + firstClassForRelation.getValue() + secondClassForRelation.getValue() + "RelationName"));
        }
        else if(classDiagram.findRelation(secondClassForRelation.getValue(), firstClassForRelation.getValue()) != null){
            classDiagram.deleteRelation(classDiagram.findRelation(secondClassForRelation.getValue(), firstClassForRelation.getValue()));
            mainPane.getChildren().remove(mainPane.lookup("#" + secondClassForRelation.getValue() + firstClassForRelation.getValue() + "Relation"));
            mainPane.getChildren().remove(mainPane.lookup("#" + secondClassForRelation.getValue() + firstClassForRelation.getValue() + "RelationName"));
        }
    }

    public void loadClassesToRelations(){
        String[] relationType = {"Asociation (black)", "Generalization (purple)", "Aggregation (green)", "Composion (blue)"};
        chooseRelationType.setValue("Asociation (black)");
        chooseRelationType.getItems().addAll(relationType);
    }


    /*
    * SEQUENCE DIAGRAM*/
    @FXML
    private void createSeqDiagram(ActionEvent e){
        if (!nameOfNewSeqDiagram.getText().isEmpty()){
            if (classDiagram.createSeqDiagram(nameOfNewSeqDiagram.getText()) != null) {
                chooseSeqDiagram.getItems().add(classDiagram.findSeqDiagram(nameOfNewSeqDiagram.getText()).getName());
                chooseSeqDiagram.setValue(classDiagram.findSeqDiagram(nameOfNewSeqDiagram.getText()).getName());
            }
            else {
                System.out.println("Sequence diagram " + nameOfNewSeqDiagram.getText() + " has not been created!");
            }
        }
    }

    @FXML
    private void loadSeqDiagram(ActionEvent event){
        if (!(chooseSeqDiagram.getValue() == null)){
            if (classDiagram.findSeqDiagram(nameOfNewSeqDiagram.getText()).isOpened()) {
                System.out.println("Diagram is already opened!");
                return;
            }
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("SequenceView.fxml"));
                Parent root = (Parent) loader.load();
                SequenceController sq = loader.getController();
                sq.nameOfSeqDiagram.setText(classDiagram.findSeqDiagram(chooseSeqDiagram.getValue()).getName());
                if (!chooseClass.getItems().isEmpty()){
                    sq.loadClasses();
                }
                if (classDiagram.findSeqDiagram(chooseSeqDiagram.getValue()).isInitialized()){
                    sq.loadSavedDiagram(chooseSeqDiagram.getValue());
                }
                Stage stage = new Stage();
                stage.setTitle("Sequence diagram");
                stage.setOnCloseRequest(e -> classDiagram.findSeqDiagram(nameOfNewSeqDiagram.getText()).setOpened(false));
                stage.setScene( new Scene(root));
                stage.show();
            } catch (Exception e){
                System.out.println("Failed to load Sequence View");
            }
        }
        else {
            System.out.println("You need to choose a diagram to open sequence diagram view");
        }
    }

    //TODO pos of loaded classes are same after delete and create of the same named deqDia
    @FXML
    private void deleteSeqDiagram(ActionEvent e){
        if (!chooseSeqDiagram.getValue().isEmpty()) {
            classDiagram.removeSeqDiagram(chooseSeqDiagram.getValue());
            chooseSeqDiagram.getItems().remove(chooseSeqDiagram.getValue());
        }
    }
}

