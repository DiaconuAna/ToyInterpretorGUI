package interpretor.interpretorgui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class main extends Application {
    @Override
    public void start(Stage stage) throws Exception {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("ProgramList.fxml"));
            Scene scene = new Scene(root, Color.LIGHTSKYBLUE);
            String css = this.getClass().getResource("ProgramList.css").toExternalForm();
            scene.getStylesheets().add(css);

            Image icon = new Image(getClass().getResource("code.png").toExternalForm());
            stage.getIcons().add(icon);
            stage.setTitle("Program List");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.show();

        }
        catch(Exception e)
        {e.printStackTrace();}

    }

    public static void main(String[] args) {
        launch(args);
    }
}
