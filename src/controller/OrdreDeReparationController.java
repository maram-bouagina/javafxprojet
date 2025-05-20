
/*package controller;
import dao.OrdredeReparationDAO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import model.*;

public class OrdreDeReparationController {

    @FXML private Button btnAjouterPiece;
    @FXML private Button btnAnnuler;
    @FXML private Button btnEnregistrer;
    @FXML private ComboBox<Categorie> cbCategorieAppareil;
    @FXML private ComboBox<Piece> cbPieces;
    @FXML private Spinner<Integer> spinnerNbHeures;
    @FXML private TextField tfAdresseClient;
    @FXML private TextField tfDescriptionAppareil;
    @FXML private TextField tfMarqueAppareil;
    @FXML private TextField tfNomClient;
    @FXML private TextField tfQuantitePiece;
    @FXML private TextField tfTelClient;
    
    private OrdredeReparationDAO ordreDAO = new OrdredeReparationDAO();
    private ObservableList<Piece> piecesList = FXCollections.observableArrayList();
    private ObservableList<Categorie> categories = FXCollections.observableArrayList();
    private ObservableList<Piece> allPieces = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupSpinner();
        loadCategories();
        setupCategoryComboBox();
        loadPieces();
        setupPiecesComboBox();
    }

    private void setupSpinner() {
        spinnerNbHeures.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
    }

    private void loadCategories() {
        // Replace with actual DAO call in production
        Categorie cat1 = new Categorie();
        cat1.setId(1);
        cat1.setLibelle("Électronique");
        cat1.setTarif("Standard");
        
        Categorie cat2 = new Categorie();
        cat2.setId(2);
        cat2.setLibelle("Électroménager");
        cat2.setTarif("Premium");
        
        categories.addAll(cat1, cat2);
    }

    private void setupCategoryComboBox() {
        cbCategorieAppareil.setItems(categories);
        
        cbCategorieAppareil.setCellFactory(param -> new ListCell<Categorie>() {
            @Override
            protected void updateItem(Categorie item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getLibelle());
            }
        });
        
        cbCategorieAppareil.setConverter(new StringConverter<Categorie>() {
            @Override
            public String toString(Categorie categorie) {
                return categorie == null ? "" : categorie.getLibelle();
            }
            
            @Override
            public Categorie fromString(String string) {
                if (string == null || string.isEmpty()) {
                    return null;
                }
                return categories.stream()
                        .filter(c -> c.getLibelle().equalsIgnoreCase(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        cbCategorieAppareil.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null && oldVal != null) {
                Platform.runLater(() -> cbCategorieAppareil.setValue(oldVal));
            }
        });
    }

    private void loadPieces() {
        // Replace with actual DAO call in production
        Piece p1 = new Piece();
        p1.setId(1);
        p1.setDesignation("Carte mère");
        p1.setPrix(120.0);
        p1.setQuantite(1);
        
        Piece p2 = new Piece();
        p2.setId(2);
        p2.setDesignation("Disque dur");
        p2.setPrix(80.0);
        p2.setQuantite(1);
        
        allPieces.addAll(p1, p2);
    }

    private void setupPiecesComboBox() {
        cbPieces.setItems(allPieces);
        
        cbPieces.setCellFactory(param -> new ListCell<Piece>() {
            @Override
            protected void updateItem(Piece item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getDesignation());
            }
        });
        
        cbPieces.setConverter(new StringConverter<Piece>() {
            @Override
            public String toString(Piece piece) {
                return piece == null ? "" : piece.getDesignation();
            }
            
            @Override
            public Piece fromString(String string) {
                if (string == null || string.isEmpty()) {
                    return null;
                }
                return allPieces.stream()
                        .filter(p -> p.getDesignation().equalsIgnoreCase(string))
                        .findFirst()
                        .orElse(null);
            }
        });
    }

    @FXML
    void handleAjouterPiece(ActionEvent event) {
        Piece selectedPiece = cbPieces.getValue();
        if (selectedPiece == null) {
            showErrorAlert("Erreur", "Veuillez sélectionner une pièce");
            return;
        }
        
        try {
            String quantiteText = tfQuantitePiece.getText().trim();
            if (quantiteText.isEmpty()) {
                showErrorAlert("Erreur", "Veuillez entrer une quantité");
                return;
            }
            
            int quantite = Integer.parseInt(quantiteText);
            if (quantite <= 0) {
                showErrorAlert("Erreur", "La quantité doit être supérieure à 0");
                return;
            }
            
            Piece pieceToAdd = new Piece();
            pieceToAdd.setId(selectedPiece.getId());
            pieceToAdd.setDesignation(selectedPiece.getDesignation());
            pieceToAdd.setPrix(selectedPiece.getPrix());
            pieceToAdd.setQuantite(quantite);
            
            piecesList.add(pieceToAdd);
            
            // Reset UI
            cbPieces.getSelectionModel().clearSelection();
            tfQuantitePiece.clear();
            
        } catch (NumberFormatException e) {
            showErrorAlert("Erreur", "Veuillez entrer une quantité valide (nombre entier)");
        }
    }

    @FXML
    void handleAnnuler(ActionEvent event) {
        // Clear all fields
        tfNomClient.clear();
        tfTelClient.clear();
        tfAdresseClient.clear();
        tfMarqueAppareil.clear();
        tfDescriptionAppareil.clear();
        cbCategorieAppareil.getSelectionModel().clearSelection();
        spinnerNbHeures.getValueFactory().setValue(1);
        cbPieces.getSelectionModel().clearSelection();
        tfQuantitePiece.clear();
        piecesList.clear();
    }

    @FXML
    void handleEnregistrer(ActionEvent event) {
        try {
            // Validate all required fields
            if (!validateFields()) {
                return;
            }
            
            // Create client
            Client client = createClientFromInput();
            
            // Create appareil
            Appareil appareil = createAppareilFromInput();
            
            // Create repair order
            OrdreDeReparation ordre = new OrdreDeReparation();
            ordre.setClient(client);
            ordre.setNbHeureMo(spinnerNbHeures.getValue());
            ordre.getAppareils().add(appareil);
            ordre.getPieces().addAll(piecesList);
            
            // Call DAO
            boolean success = ordreDAO.ajouterOrdre(ordre);
            
            if (success) {
                showInfoAlert("Succès", "Ordre enregistré avec succès");
                handleAnnuler(null);
            } else {
                showErrorAlert("Erreur", "Échec de l'enregistrement");
            }
            
        } catch (NullPointerException e) {
            showErrorAlert("Erreur Critique", "Une valeur requise est manquante: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showErrorAlert("Erreur", "Une erreur est survenue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateFields() {
        if (tfNomClient.getText().trim().isEmpty()) {
            showErrorAlert("Erreur", "Le nom du client est requis");
            tfNomClient.requestFocus();
            return false;
        }
        
        if (tfTelClient.getText().trim().isEmpty()) {
            showErrorAlert("Erreur", "Le téléphone du client est requis");
            tfTelClient.requestFocus();
            return false;
        }
        
        if (tfMarqueAppareil.getText().trim().isEmpty()) {
            showErrorAlert("Erreur", "La marque de l'appareil est requise");
            tfMarqueAppareil.requestFocus();
            return false;
        }
        
        if (tfDescriptionAppareil.getText().trim().isEmpty()) {
            showErrorAlert("Erreur", "La description de l'appareil est requise");
            tfDescriptionAppareil.requestFocus();
            return false;
        }
        
        if (cbCategorieAppareil.getValue() == null) {
            showErrorAlert("Erreur", "Veuillez sélectionner une catégorie");
            cbCategorieAppareil.requestFocus();
            return false;
        }
        
        return true;
    }

    private Client createClientFromInput() {
        Client client = new Client();
        client.setNom(tfNomClient.getText().trim());
        client.setTel(tfTelClient.getText().trim());
        client.setAdresse(tfAdresseClient.getText().trim());
        return client;
    }

    private Appareil createAppareilFromInput() {
        Appareil appareil = new Appareil();
        appareil.setMarque(tfMarqueAppareil.getText().trim());
        appareil.setDescription(tfDescriptionAppareil.getText().trim());
        appareil.setCategorie(cbCategorieAppareil.getValue());
        return appareil;
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}*/
package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import connexion.Connexion;
import dao.OrdredeReparationDAO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import model.*;

public class OrdreDeReparationController {

    @FXML private Button btnAjouterPiece;
    @FXML private Button btnAnnuler;
    @FXML private Button btnEnregistrer;
    @FXML private ComboBox<Categorie> cbCategorieAppareil;
    @FXML private ComboBox<Piece> cbPieces;
    @FXML private Spinner<Integer> spinnerNbHeures;
    @FXML private TextField tfAdresseClient;
    @FXML private TextField tfDescriptionAppareil;
    @FXML private TextField tfMarqueAppareil;
    @FXML private TextField tfNomClient;
    @FXML private TextField tfQuantitePiece;
    @FXML private TextField tfTelClient;
    
    private OrdredeReparationDAO ordreDAO = new OrdredeReparationDAO();
    private ObservableList<Piece> piecesList = FXCollections.observableArrayList();
    private ObservableList<Categorie> categories = FXCollections.observableArrayList();
    private ObservableList<Piece> allPieces = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupSpinner();
        loadCategories();
        setupCategoryComboBox();
        loadPieces();
        setupPiecesComboBox();
    }

    private void setupSpinner() {
        spinnerNbHeures.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
    }

    private void loadCategories() {
        categories.clear();
        String query = "SELECT * FROM categorie";
        try (Connection cn = Connexion.getCn();
             PreparedStatement ps = cn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Categorie cat = new Categorie();
                cat.setId(rs.getLong("id"));
                cat.setLibelle(rs.getString("libelle"));
                cat.setTarif(rs.getString("tarif"));
                categories.add(cat);
            }
            if (categories.isEmpty()) {
                showErrorAlert("Erreur", "Aucune catégorie trouvée dans la base de données. Veuillez ajouter des catégories.");
            }
        } catch (SQLException e) {
            System.err.println("Failed to load categories: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Erreur", "Échec du chargement des catégories : " + e.getMessage());
        }
    }

    private void setupCategoryComboBox() {
        cbCategorieAppareil.setItems(categories);
        
        cbCategorieAppareil.setCellFactory(param -> new ListCell<Categorie>() {
            @Override
            protected void updateItem(Categorie item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getLibelle());
            }
        });
        
        cbCategorieAppareil.setConverter(new StringConverter<Categorie>() {
            @Override
            public String toString(Categorie categorie) {
                return categorie == null ? "" : categorie.getLibelle();
            }
            
            @Override
            public Categorie fromString(String string) {
                if (string == null || string.isEmpty()) {
                    return null;
                }
                return categories.stream()
                        .filter(c -> c.getLibelle().equalsIgnoreCase(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        cbCategorieAppareil.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null && oldVal != null) {
                Platform.runLater(() -> cbCategorieAppareil.setValue(oldVal));
            }
        });
    }

    private void loadPieces() {
        // Replace with actual DAO call in production
        Piece p1 = new Piece();
        p1.setId(1);
        p1.setDesignation("Carte mère");
        p1.setPrix(120.0);
        p1.setQuantite(1);
        
        Piece p2 = new Piece();
        p2.setId(2);
        p2.setDesignation("Disque dur");
        p2.setPrix(80.0);
        p2.setQuantite(1);
        
        allPieces.addAll(p1, p2);
    }

    private void setupPiecesComboBox() {
        cbPieces.setItems(allPieces);
        
        cbPieces.setCellFactory(param -> new ListCell<Piece>() {
            @Override
            protected void updateItem(Piece item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getDesignation());
            }
        });
        
        cbPieces.setConverter(new StringConverter<Piece>() {
            @Override
            public String toString(Piece piece) {
                return piece == null ? "" : piece.getDesignation();
            }
            
            @Override
            public Piece fromString(String string) {
                if (string == null || string.isEmpty()) {
                    return null;
                }
                return allPieces.stream()
                        .filter(p -> p.getDesignation().equalsIgnoreCase(string))
                        .findFirst()
                        .orElse(null);
            }
        });
    }

    @FXML
    void handleAjouterPiece(ActionEvent event) {
        Piece selectedPiece = cbPieces.getValue();
        if (selectedPiece == null) {
            showErrorAlert("Erreur", "Veuillez sélectionner une pièce");
            return;
        }
        
        try {
            String quantiteText = tfQuantitePiece.getText().trim();
            if (quantiteText.isEmpty()) {
                showErrorAlert("Erreur", "Veuillez entrer une quantité");
                return;
            }
            
            int quantite = Integer.parseInt(quantiteText);
            if (quantite <= 0) {
                showErrorAlert("Erreur", "La quantité doit être supérieure à 0");
                return;
            }
            
            Piece pieceToAdd = new Piece();
            pieceToAdd.setId(selectedPiece.getId());
            pieceToAdd.setDesignation(selectedPiece.getDesignation());
            pieceToAdd.setPrix(selectedPiece.getPrix());
            pieceToAdd.setQuantite(quantite);
            
            piecesList.add(pieceToAdd);
            
            // Reset UI
            cbPieces.getSelectionModel().clearSelection();
            tfQuantitePiece.clear();
            
        } catch (NumberFormatException e) {
            showErrorAlert("Erreur", "Veuillez entrer une quantité valide (nombre entier)");
        }
    }

    @FXML
    void handleAnnuler(ActionEvent event) {
        // Clear all fields
        tfNomClient.clear();
        tfTelClient.clear();
        tfAdresseClient.clear();
        tfMarqueAppareil.clear();
        tfDescriptionAppareil.clear();
        cbCategorieAppareil.getSelectionModel().clearSelection();
        spinnerNbHeures.getValueFactory().setValue(1);
        cbPieces.getSelectionModel().clearSelection();
        tfQuantitePiece.clear();
        piecesList.clear();
    }

    @FXML
    void handleEnregistrer(ActionEvent event) {
        try {
            // Validate all required fields
            if (!validateFields()) {
                return;
            }
            
            // Create client
            Client client = createClientFromInput();
            
            // Create appareil
            Appareil appareil = createAppareilFromInput();
            
            // Create repair order
            OrdreDeReparation ordre = new OrdreDeReparation();
            ordre.setClient(client);
            ordre.setNbHeureMo(spinnerNbHeures.getValue());
            ordre.getAppareils().add(appareil);
            ordre.getPieces().addAll(piecesList);
            
            // Call DAO
            boolean success = ordreDAO.ajouterOrdre(ordre);
            
            if (success) {
                showInfoAlert("Succès", "Ordre enregistré avec succès");
                handleAnnuler(null);
            } else {
                showErrorAlert("Erreur", "Échec de l'enregistrement");
            }
            
        } catch (NullPointerException e) {
            showErrorAlert("Erreur Critique", "Une valeur requise est manquante: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showErrorAlert("Erreur", "Une erreur est survenue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateFields() {
        if (tfNomClient.getText().trim().isEmpty()) {
            showErrorAlert("Erreur", "Le nom du client est requis");
            tfNomClient.requestFocus();
            return false;
        }
        
        if (tfTelClient.getText().trim().isEmpty()) {
            showErrorAlert("Erreur", "Le téléphone du client est requis");
            tfTelClient.requestFocus();
            return false;
        }
        
        if (tfMarqueAppareil.getText().trim().isEmpty()) {
            showErrorAlert("Erreur", "La marque de l'appareil est requise");
            tfMarqueAppareil.requestFocus();
            return false;
        }
        
        if (tfDescriptionAppareil.getText().trim().isEmpty()) {
            showErrorAlert("Erreur", "La description de l'appareil est requise");
            tfDescriptionAppareil.requestFocus();
            return false;
        }
        
        if (cbCategorieAppareil.getValue() == null) {
            showErrorAlert("Erreur", "Veuillez sélectionner une catégorie");
            cbCategorieAppareil.requestFocus();
            return false;
        }
        
        return true;
    }

    private Client createClientFromInput() {
        Client client = new Client();
        client.setNom(tfNomClient.getText().trim());
        client.setTel(tfTelClient.getText().trim());
        client.setAdresse(tfAdresseClient.getText().trim());
        return client;
    }

    private Appareil createAppareilFromInput() {
        Appareil appareil = new Appareil();
        appareil.setMarque(tfMarqueAppareil.getText().trim());
        appareil.setDescription(tfDescriptionAppareil.getText().trim());
        appareil.setCategorie(cbCategorieAppareil.getValue());
        System.out.println("Selected categorie ID: " + (appareil.getCategorie() != null ? appareil.getCategorie().getId() : "null"));
        return appareil;
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}