module interpretor.interpretorgui {
    requires javafx.controls;
    requires javafx.fxml;


    opens interpretor.interpretorgui to javafx.fxml;
    exports interpretor.interpretorgui;
}