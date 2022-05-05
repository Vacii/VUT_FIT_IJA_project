package ija.umleditor;
import ija.umleditor.uml.*;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
    /**************/

    @FXML
    private TextField newName;

    @FXML
    private TextField attAndMethText;

    @FXML
    private TextField typeText;

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

    private boolean operatorsLoaded = false;
    private boolean undoActive = false;
    private boolean classDeleteActive = false;

    private ArrayList<String> listOfAttributes = new ArrayList<>();
    private ArrayList<String> listOfMethods = new ArrayList<>();

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

        actionsPerformed.add("load performed");

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

    private void createClassHelpMethod () {
        String name;

        if (undoActive == true) name = classesDeleted.get(classesDeleted.size()-1).getName();

        else name = newName.getText();

        if (!name.isEmpty()) {
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
                titledPane.setLayoutX(classDiagram.findClass(name).getXposition() + classCounter);
                titledPane.setLayoutY(classDiagram.findClass(name).getYposition() + classCounter);
                mainPane.getChildren().add(titledPane);
                classCounter++;
                chooseClass.getItems().add(name);
                firstClassForRelation.getItems().add(name);
                secondClassForRelation.getItems().add(name);
            }

            titledPane.setOnMousePressed(event -> {
                XpositionOfClass = event.getSceneX() - titledPane.getTranslateX();
                YpositionOfClass = event.getSceneY() - titledPane.getTranslateY();
            });
            //TODO nastavovat pozice i u trid nactenych ze souboru
            titledPane.setOnMouseDragged(event -> {
                titledPane.setTranslateX(event.getSceneX() - XpositionOfClass);
                titledPane.setTranslateY(event.getSceneY() - YpositionOfClass);
                classDiagram.findClass(name).setXposition(event.getSceneX() - XpositionOfClass);
                classDiagram.findClass(name).setYposition(event.getSceneY() - YpositionOfClass);

                List<UMLRelation> relations = classDiagram.getClassRelations(name);
                for (int i = 0; i < relations.size(); i++){
                    mainPane.getChildren().removeAll(mainPane.lookupAll(("#" + relations.get(i).getFirstClass().getName() + relations.get(i).getSecondClass().getName() + "Relation")));
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
        if (!name.isEmpty()) {
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
                    titledPane.setLayoutX(classDiagram.findInterface(name).getXposition() + classCounter*5);
                    titledPane.setLayoutY(classDiagram.findInterface(name).getYposition() + classCounter*5);
                    mainPane.getChildren().add(titledPane);
                    classCounter++;
                    chooseClass.getItems().add(name);
                    firstClassForRelation.getItems().add(name);
                    secondClassForRelation.getItems().add(name);
                }

                titledPane.setOnMousePressed(event -> {
                    XpositionOfClass = event.getSceneX() - titledPane.getTranslateX();
                    YpositionOfClass = event.getSceneY() - titledPane.getTranslateY();
                });
                titledPane.setOnMouseDragged(event -> {
                    titledPane.setTranslateX(event.getSceneX() - XpositionOfClass);
                    titledPane.setTranslateY(event.getSceneY() - YpositionOfClass);
                    classDiagram.findInterface(name).setXposition(event.getSceneX() - XpositionOfClass);
                    classDiagram.findInterface(name).setYposition(event.getSceneY() - YpositionOfClass);
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

            //creating class in diagram, entity = name of class
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

            showClassToGUI(classDiagram, cls);

            //printing data to output
        }
    }

    //TODO vypisovat tridy na spravne pozice, tusim se to neparsuje
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

        System.out.println(cls.getXposition() + "" + cls.getYposition());

        TitledPane titledPane = new TitledPane(cls.getName(), box);
        titledPane.setId(cls.getName());
        titledPane.setText(cls.getName());
        titledPane.setCollapsible(false);
        titledPane.setPrefHeight(100);
        titledPane.setPrefWidth(100);

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
            XpositionOfClass = event.getSceneX() - titledPane.getTranslateX();
            YpositionOfClass = event.getSceneY() - titledPane.getTranslateY();
        });
        titledPane.setOnMouseDragged(event -> {
            titledPane.setTranslateX(event.getSceneX() - XpositionOfClass);
            titledPane.setTranslateY(event.getSceneY() - YpositionOfClass);
            classDiagram.findClass(name).setXposition(event.getSceneX() - XpositionOfClass);
            classDiagram.findClass(name).setYposition(event.getSceneY() - YpositionOfClass);

            List<UMLRelation> relations = classDiagram.getClassRelations(name);
            for (int i = 0; i < relations.size(); i++){
                mainPane.getChildren().removeAll(mainPane.lookupAll(("#" + relations.get(i).getFirstClass().getName() + relations.get(i).getSecondClass().getName() + "Relation")));
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
    private void deleteClassHelp() {

        String nameOfRemovedClass;

        if (undoActive == true) nameOfRemovedClass = classesAdded.get(classesAdded.size() - 1).getName();

        else nameOfRemovedClass = chooseClass.getValue();
        //TODO relace

        if (!classDiagram.findClass(nameOfRemovedClass).getAttributes().isEmpty()) {

            List<UMLAttribute> arr = classDiagram.findClass(nameOfRemovedClass).getAttributes();

            for (int i = 0; i < arr.size(); i++) {

                UMLAttribute attribute = arr.get(i);
                classDiagram.findClass(nameOfRemovedClass).removeAttribute(attribute);
                attributesOfClassesDeleted.add(attribute);
                i--;
                if (classDiagram.findClass(nameOfRemovedClass).getAttributes().isEmpty()) break;
            }

        }

        if (!classDiagram.findClass(nameOfRemovedClass).getMethods().isEmpty()) {

            List<UMLOperation> arr2 = classDiagram.findClass(nameOfRemovedClass).getMethods();

            for (int i = 0; i < arr2.size(); i++) {

                UMLOperation operation = arr2.get(i);
                classDiagram.findClass(nameOfRemovedClass).removeMethod(operation);
                methodsOfClassesDeleted.add(operation);
                i--;
                if (classDiagram.findClass(nameOfRemovedClass).getMethods().isEmpty()) break;
            }
        }
        classesDeleted.add(classDiagram.findClass(nameOfRemovedClass)); //UNDO
        classDiagram.deleteClass(classDiagram.findClass(nameOfRemovedClass));
        mainPane.getChildren().remove(mainPane.lookup("#" + nameOfRemovedClass));
        chooseClass.getItems().remove(nameOfRemovedClass);

        if(!undoActive) actionsPerformed.add("removed class");

        numberOfAttributesDeleted.add(attributesOfClassesDeleted.size());
        numberOfMethodsDeleted.add(methodsOfClassesDeleted.size());

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

            if ((!name.isEmpty() && !type.isEmpty()) && chosenClass != null && !listOfAttributes.contains(name)) {

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
            //TODO listOfMethods tady znamena, ze ruzne tridy nesmi mit stejny nazev metod, to stejne u atributu
            if ((!name.isEmpty() && !type.isEmpty()) && !listOfMethods.contains(name)) {
                if (!undoActive) actionsPerformed.add("added method");
                UMLOperation newMethod = new UMLOperation(name, classDiagram.classifierForName(type));
                chosenClass.addMethod(newMethod);
                VBox methods = (VBox) mainPane.lookup("#" + chooseClass.getValue() + "Methods");
                Text method = new Text(chooseOperator.getValue() + name + ":" + type);
                method.setId(name + "Meth");
                methods.getChildren().add(method);
                chooseMethod.getItems().add(method.getText().substring(1));
                listOfMethods.add(name); //UNDO
                classOfMethodAdded.add(chosenClass); //UNDO
            }
        }
        else if(chooseClass.getValue() != null && (classDiagram.findInterface(chooseClass.getValue()) != null)){
            System.out.println("tady");
            UMLInterface chosenInterface = classDiagram.findInterface(chooseClass.getValue());
            String name = attAndMethText.getText();
            String type = typeText.getText();
            if ((!name.isEmpty() && !type.isEmpty())) {

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

            System.out.println(chosenClass);
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
            List<UMLAttribute> attributeList = chosenClass.getAttributes();
            for (UMLAttribute attribute : attributeList) {

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

                //remove if the attributes are matching
                if(name.equals(compareName) && type.equals(compareType)) {

                    removed = true;
                    currentClass.removeMethod(operation);
                    VBox methods = (VBox) mainPane.lookup("#" + currentClass.getName() + "Methods");
                    methods.getChildren().remove(methods.lookup("#" + name + "Meth"));
                    chooseMethod.getItems().remove(name + ":" + type);
                    break;

                }
            }
            //if removed add new method
            if (removed) {

                UMLOperation newOperation = new UMLOperation(newName, classDiagram.classifierForName(newType));
                currentClass.addMethod(newOperation);
                VBox methods = (VBox) mainPane.lookup("#" + currentClass.getName() + "Methods");
                Text method = new Text(chooseOperator.getValue() + newName + ":" + newType);
                method.setId(newName + "Meth");
                methods.getChildren().add(method);
                chooseMethod.getItems().add(newName + ":" + newType);


            }
        }

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
        object.put("entity", cls.getName());
        return object;

    }

    @FXML
    private void saveJsonFile(ActionEvent e) {
    //TODO nejdou otevrit ulozene soubory

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
                JSONArray arr = new JSONArray();
                FilePath = selectedFile.getAbsolutePath();

                for (UMLClass umlClass : classList) {

                    arr.put(classToJsonObject(umlClass));
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
            System.out.println(actionsPerformed);
            actionsPerformed.remove(actionsPerformed.size() - 1);
            System.out.println(actionsPerformed);
            undoActive = false;
        }
    }

    //TODO relations can be created only between 2 classes, not interface
    @FXML
    private void createRelation(ActionEvent e) throws IOException {
        String newRelName = relationName.getText();
        if (classDiagram.findInterface(firstClassForRelation.getValue()) != null || classDiagram.findInterface(secondClassForRelation.getValue()) != null){
            System.out.println("Interface relations are not inplemented yet!");
        }
        else if (classDiagram.findRelation(classDiagram.findClass(firstClassForRelation.getValue()), classDiagram.findClass(secondClassForRelation.getValue())) == null){
            if (!newRelName.isEmpty() && !Objects.equals(firstClassForRelation.getValue(), secondClassForRelation.getValue())) {
                UMLClass firstClass = classDiagram.findClass(firstClassForRelation.getValue());
                UMLClass secondClass = classDiagram.findClass(secondClassForRelation.getValue());
                UMLRelation relace = classDiagram.createRelation(firstClass, secondClass, newRelName);
                drawRelation(relace);
                System.out.println(relace.getName());
            }
        }
    }

    private void drawRelation(UMLRelation relation){
        Line line = new Line();
        line.setId(relation.getFirstClass().getName() + relation.getSecondClass().getName() + "Relation");
        line.setStartX(relation.getFirstClass().getXposition() + 80);
        line.setStartY(relation.getFirstClass().getYposition() + 80);
        line.setEndX(relation.getSecondClass().getXposition() + 80);
        line.setEndY(relation.getSecondClass().getYposition() + 80);
        mainPane.getChildren().add(0,line);
    }

    //TODO relations can be deleted only when the first class is seleceted same way as when created
    @FXML
    private void deleteRelation(ActionEvent e){
        if (classDiagram.findRelation(classDiagram.findClass(firstClassForRelation.getValue()), classDiagram.findClass(secondClassForRelation.getValue())) != null){
            classDiagram.deleteRelation(classDiagram.findRelation(classDiagram.findClass(firstClassForRelation.getValue()), classDiagram.findClass(secondClassForRelation.getValue())));
            mainPane.getChildren().remove(mainPane.lookup("#" + firstClassForRelation.getValue() + secondClassForRelation.getValue() + "Relation"));
        }
    }

    public void loadClassesToRelations(){
        String[] relationType = {"Asociation", "Inheritance", "Aggregation", "Composion"};
        chooseRelationType.setValue("Asociation");
        chooseRelationType.getItems().addAll(relationType);
    }
}

