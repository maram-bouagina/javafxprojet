package controller;

import dao.OrdredeReparationDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import model.Appareil;
import model.Categorie;
import model.Client;
import model.OrdreDeReparation;

public class OrdreController {

    @FXML private TextField txtNomClient;
    @FXML private TextField txtAdresseClient;
    @FXML private TextField txtTelClient;
    @FXML private TextField txtDescriptionAppareil;
    @FXML private TextField txtMarqueAppareil;
    @FXML private TextField txtNbHeureMO;
    @FXML private Button btnAjouter;

    private OrdredeReparationDAO ordreDAO = new OrdredeReparationDAO();

    @FXML
    private void initialize() {
        btnAjouter.setOnAction(e -> ajouterOrdre());
    }

    private void ajouterOrdre() {
        try {
            Client client = new Client();
            client.setNom(txtNomClient.getText());
            client.setAdresse(txtAdresseClient.getText());
            client.setTel(txtTelClient.getText());

            Appareil appareil = new Appareil();
            appareil.setDescription(txtDescriptionAppareil.getText());
            appareil.setMarque(txtMarqueAppareil.getText());
            Categorie categorie = new Categorie();
            categorie.setId(1); 
            appareil.setCategorie(categorie);

            OrdreDeReparation ordre = new OrdreDeReparation();
            ordre.setClient(client);
            ordre.setNbHeureMo(Integer.parseInt(txtNbHeureMO.getText()));
            ordre.getAppareils().add(appareil);

            boolean ok = ordreDAO.ajouterOrdre(ordre);

            if (ok) {
                showAlert("Success", "Ordre added successfully!");
            } else {
                showAlert("Error", "Failed to add ordre.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Error", "Unexpected error: " + ex.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
