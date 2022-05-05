module ija.umleditor {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;

    opens ija.umleditor to javafx.fxml;
    exports ija.umleditor;
    exports ija.umleditor.uml;
    opens ija.umleditor.uml to javafx.fxml;
}