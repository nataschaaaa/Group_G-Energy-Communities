module at.fhtw.energyjavafxgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires java.net.http;

    opens at.fhtw.energyjavafxgui to javafx.fxml;
    exports at.fhtw.energyjavafxgui;
}