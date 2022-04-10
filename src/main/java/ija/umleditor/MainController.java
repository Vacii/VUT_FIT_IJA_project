package ija.umleditor;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void switchToMainScene(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("MainView.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToSequenceScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("sequenceView.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    public void helpPopupClick(ActionEvent e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("helpPopup.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        stage.show();
    }

    public void createClassClick(ActionEvent e){}

    public void createInterfaceClick(ActionEvent e){}

    public void createElementClick(ActionEvent e){}


}