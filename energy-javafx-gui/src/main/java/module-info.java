module at.fhtw.energyjavafxgui {
    requires javafx.controls;
    requires javafx.fxml;


    opens at.fhtw.energyjavafxgui to javafx.fxml;
    exports at.fhtw.energyjavafxgui;
}