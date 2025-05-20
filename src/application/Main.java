package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // ✅ Cette ligne est la clé pour que ton FXML soit trouvé
            URL fxmlUrl = getClass().getResource("/view/file2.fxml");
            if (fxmlUrl == null) {
                System.err.println("Fichier FXML introuvable à : /view/file2.fxml");
                return;
            }

            Parent root = FXMLLoader.load(fxmlUrl);

            // Charger le CSS (facultatif)
            URL cssUrl = getClass().getResource("/view/application.css");
            Scene scene = new Scene(root);
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            primaryStage.setTitle("Ordre de Réparation");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.show();

        } catch (Exception e) {
            System.err.println(" Erreur lors du chargement de l'interface :");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
