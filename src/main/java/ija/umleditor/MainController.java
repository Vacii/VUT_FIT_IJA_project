package ija.umleditor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;

/**
 * MainController je hlavní třída pro ovládání GUI
 *
 * @author  Lukáš Václavek (xvacla32)
 */
public class MainController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private Label label_class1;


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
    private void loadJsonFile(ActionEvent event){
        FileChooser chooseFile = new FileChooser();
        chooseFile.setTitle("Select file");
        File selectedFile = chooseFile.showOpenDialog(new Stage());
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
    private void createClassClick(ActionEvent e){}

    @FXML
    private void createElementClick(ActionEvent e){}


}