package ija.umleditor;
import ija.umleditor.uml.ClassDiagram;
import ija.umleditor.uml.UMLAttribute;
import ija.umleditor.uml.UMLClass;
import ija.umleditor.uml.UMLOperation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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

    @FXML
    private Label label_class1;

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

            parseJsonToObject(getJSONFromFile(FilePath));

            //TODO - DISPLAY SHIT TO GUI
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
            VBox attributes = new VBox();
            vbox.getChildren().add(attributes);
            attributes.setId(name + "Attributes");
            TitledPane titledPane = new TitledPane(name, vbox);
            titledPane.setText(name);
            titledPane.setId(name);
            titledPane.setCollapsible(false);
            titledPane.setPrefHeight(100);
            titledPane.setPrefWidth(100);
            if (classDiagram.findClass(name) != null) {
                titledPane.setLayoutX(classDiagram.findClass(name).getXposition() + classCounter*5);
                titledPane.setLayoutY(classDiagram.findClass(name).getYposition() + classCounter*5);
                mainPane.getChildren().add(titledPane);
                classCounter++;
                chooseClass.getItems().add(name);
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
        ClassDiagram d = new ClassDiagram("Class Model");

        for (int i = 0; i < jsonArray.length(); i++) {

            //iterating over JSON array -> this way we can select all elements and its attributes
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            //in "i" object select the entity key word in JSON -> corresponds to elements name
            String entity = jsonObject.getString("entity");

            //creating class in diagram, entity = name of class
            UMLClass cls = d.createClass(entity);

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
                UMLAttribute attributeObject = new UMLAttribute(key, d.classifierForName(value));

                //adding attribute to class where it belongs
                cls.addAttribute(attributeObject);

            }

            //methods are the same as attributes mentioned above
            //constructor takes name of the method and value that the method returns
            //TO DO - what about the arguments?

            JSONObject jsonMethodObject = (JSONObject) jsonObject.get("methods");
            JSONArray keys2 = jsonMethodObject.names();

            ArrayList<UMLOperation> array = new ArrayList<>();

            for (int j = 0; j < keys2.length(); j++) {

                String key = keys2.getString(j);
                String value = jsonMethodObject.getString(key);

                //creating method
                UMLOperation operationObject = UMLOperation.create(key, d.classifierForName(value));
                cls.addMethod(operationObject);
                //TO DO - how to add method to certain class?

            }

            showClassToGUI(d, cls);

            //printing data to output
            System.out.println(cls);
            List<UMLAttribute> arr = cls.getAttributes();
            System.out.println(arr);
            System.out.println(cls.getMethods());
        }
    }

    private void showClassToGUI(ClassDiagram d, UMLClass cls) {
        VBox box = new VBox();

        //vbox v classe, ve kterým mají být metody, nastavuju mu id, abych se na něj pak mohl odkázat dole v loopu

        VBox methods = new VBox();
        box.getChildren().add(methods);
        String name = cls.getName();
        System.out.println(name);
        methods.setId(name+"Methods");

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

            //TODO TADY JE PROBLÉM, NECHCE SE TO NALINKOVAT NA TEN VBOX NAHOŘE - TRIED EVERYTHING STILL AINT WORKING
            //      VBox operationBox = ((VBox) mainPane.lookup("#" + cls.getName() + "Methods"));
            //pokud se to správně nalinkuje tak tady už jen vytvářím místo pro ten text v tom příslušným vboxu
            Text method = new Text (name + ":" + type);
            //nastavení id do budoucna, kdy ho budu chtít znát
            method.setId(operationName+"Method");
            //musí být zakomentovaný, aby jel zbytek
            //      operationBox.getChildren().add(method);

        }

        if (d.findClass(cls.getName()) != null) {
            titledPane.setLayoutX(cls.getXposition() + classCounter*300);
            titledPane.setLayoutY(cls.getYposition());
            mainPane.getChildren().add(titledPane); //adding class to main window so its visible
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


    @FXML
    private void deleteClass(ActionEvent e){
        String nameOfRemovedClass = chooseClass.getValue();
        //TODO nez se vymaze trida, tak se musi vymazat vsechny jeji atributy (pozdeji metody, relace atd.)
        classDiagram.deleteClass(classDiagram.findClass(nameOfRemovedClass));
        mainPane.getChildren().remove(mainPane.lookup("#" + nameOfRemovedClass));
        chooseClass.getItems().remove(nameOfRemovedClass);
    }

    @FXML
    private void addAttribute(ActionEvent e){
        if (chooseClass.getValue() != null){
            UMLClass chosenClass = classDiagram.findClass(chooseClass.getValue());
            String name = attAndMethText.getText();
            String type = typeText.getText();
            if (!name.isEmpty() || !type.isEmpty()) {
                UMLAttribute newAttribute = new UMLAttribute(name, classDiagram.classifierForName(type));
                chosenClass.addAttribute(newAttribute);
                System.out.println(newAttribute.toString());
            }
        }
    }

    @FXML
    private void addMethod(ActionEvent e){
        if (chooseClass.getValue() != null){
            UMLClass chosenClass = classDiagram.findClass(chooseClass.getValue());
            String name = attAndMethText.getText();
        }
    }


    private JSONObject classToJsonObject (UMLClass cls) {

        JSONObject object = new JSONObject();
        object.put("entity", cls.getName());
        return object;

    }

    @FXML
    private void saveJsonFile(ActionEvent e) {

        List<UMLClass> classList = classDiagram.getClasses();
        JSONArray arr = new JSONArray();

        for (UMLClass umlClass : classList) {

            arr.put(classToJsonObject(umlClass));
        }

        String jsonText = arr.toString();
        try {

            File file = new File("try.json");
            file.createNewFile();

            FileWriter fw = new FileWriter(file);
            fw.write(jsonText);
            fw.close();
            System.out.println(arr);
        }

        catch (Exception exc) {

            throw new RuntimeException();
        }

    }

}