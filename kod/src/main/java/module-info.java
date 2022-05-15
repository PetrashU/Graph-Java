module com.example.graphjava {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.desktop;

    opens Kratka to javafx.fxml;
    exports Kratka;
}