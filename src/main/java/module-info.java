module ija.umleditor {
    requires javafx.controls;
    requires javafx.fxml;


    opens ija.umleditor to javafx.fxml;
    exports ija.umleditor;
}