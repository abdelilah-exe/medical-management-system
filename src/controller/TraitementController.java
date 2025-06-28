package controller;

import java.io.File;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.stage.FileChooser;

import model.Traitement;
import utils.AlertMessage;
import utils.CSVExporter;
import utils.Database;
import utils.SceneManager;

/**
 * Contrôleur pour la gestion des traitements médicaux dans l'application MediConnect.
 * Permet d'afficher, ajouter, modifier et supprimer des traitements.
 * 
 * @author pc
 */

public class TraitementController implements Initializable {

    // ============================================================
    // ================ COMPOSANTS INTERFACE FXML ================
    // ============================================================
    
    // --- Navigation
    @FXML private Button btnAccueil;
    @FXML private Button btnPatients;
    @FXML private Button btnRendezVous;
    @FXML private Button btnStatistiques;
    @FXML private Button btnParametres;
    @FXML private Button btnAide;
    
    // --- Actions principales
    @FXML private Button btnSupprimerTraitements;
    @FXML private Button btnExporter;
    
    // --- Organisation des vues
    @FXML private VBox traitementsListView;
    @FXML private VBox traitementsAddView;
    @FXML private VBox modifierTraitementView;
    
    // --- Tableau des traitements
    @FXML private TableView<Traitement> tableTraitements;
    @FXML private TableColumn<Traitement, Boolean> checkboxColumn;
    @FXML private TableColumn<Traitement, String> nomColumn;
    @FXML private TableColumn<Traitement, String> typeColumn;
    @FXML private TableColumn<Traitement, String> dateDebutColumn;
    @FXML private TableColumn<Traitement, String> dateFinColumn;
    @FXML private TableColumn<Traitement, String> statutColumn;
    @FXML private TableColumn<Traitement, Void> actionsColumn;
    
    // --- Bar de recherche
    @FXML private TextField searchField;
    
    // --- Select all checkbox
    @FXML private CheckBox selectAllCheckbox;
    
    // --- Formulaire d'ajout
    @FXML private ComboBox<String> comboPatient;
    @FXML private TextField inputType;
    @FXML private DatePicker inputDateDebut;
    @FXML private DatePicker inputDateFin;
    @FXML private TextArea inputDescription;
    
    // --- Formulaire d'édition
    @FXML private Label labelTraitementId;
    @FXML private ComboBox<String> comboPatientModif;
    @FXML private TextField inputPosologieModif;
    @FXML private TextField inputTypeModif;
    @FXML private DatePicker datePickerDebutModif;
    @FXML private DatePicker datePickerFinModif;
    @FXML private CheckBox checkBoxActifModif;
    @FXML private TextArea textAreaObservationsModif;

    // ============================================================
    // ============== ATTRIBUTS ET STRUCTURES DE DONNÉES ==========
    // ============================================================
    // --- Propriété booléenne indiquant si au moins un traitement est actuellement sélectionné.
    private BooleanProperty anyTraitementSelected = new SimpleBooleanProperty(false);
    
    // --- Référence au traitement sélectionné pour une opération de modification.
    private Traitement traitementEnEdition;
    
    // --- List contenant les traitements
    private ObservableList<Traitement> traitementsList = FXCollections.observableArrayList();
    
    // --- List contenant les traitements correspondant à la recherche.
    private FilteredList<Traitement> filteredTraitements;
    
    // --- List contenant les patients disponibles
    private ObservableList<String> patientsList = FXCollections.observableArrayList();

    // ==============================================================================================================================
    // ================= MÉTHODE D'INITIALISATION (Méthode appelée automatiquement après le chargement du fichier FXML) =============
    // ==============================================================================================================================
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configuration des vues
        setupViews();
        
        // Charge les traitements avec des données d'exemple
        //initializeData();
        
        // Charge les noms des patients depuis la base de données
        loadPatientsFromDatabase();
        
        //// Charge les traitements depuis la base de données
        loadTraitementsFromDatabase();

        // Configure la recherche avec filtrage dynamique
        setupSearchFilter();
        
        // Configure les éléments du tableau
        tableTraitements.setItems(filteredTraitements);
        tableTraitements.setEditable(true);
        
        comboPatient.setItems(patientsList);
        
        setupCheckboxColumn();
        setupDataColumns();
        setupActionsColumn();
        
        // Configure les interactions entre éléments de l'interface
        btnSupprimerTraitements.disableProperty().bind(anyTraitementSelected.not());
        
        tableTraitements.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            updateDeleteButtonState();
        });
    }

    // =============================================================================
    // ============== ACTIONS SUR LE MENU LATÉRAL (Navigation entre les vues) ======
    // =============================================================================
    @FXML
    private void handleAccueilClick(ActionEvent event) {
        SceneManager.switchScene(btnAccueil, "/view/AcceuilView.fxml", "MediConnect - Acceuil", 1500, 750);
        System.out.println("Navigation vers la vue d'acceuil");
    }

    @FXML
    private void handlePatientsClick(ActionEvent event) {
        SceneManager.switchScene(btnPatients, "/view/PatientView.fxml", "MediConnect - Patients", 1500, 750);
        System.out.println("Navigation vers la vue Patients");
    }
    
    @FXML
    public void handleTraitementsClick(ActionEvent event) {
        traitementsListView.setVisible(true);
        traitementsAddView.setVisible(false);
        modifierTraitementView.setVisible(false);
        System.out.println("Déjà sur la page des traitements");
    }
    
    @FXML
    private void handleRendezVousClick(ActionEvent event) {
        SceneManager.switchScene(btnRendezVous, "/view/RendezVousView.fxml", "MediConnect - RendezVous", 1500, 750);
        System.out.println("Navigation vers la vue Rendez-vous");
    }

    @FXML
    private void handleStatistiquesClick(ActionEvent event) {
        SceneManager.switchScene(btnStatistiques, "/view/StatistiquesView.fxml", "MediConnect - Statistiques", 1500, 750);
        System.out.println("Navigation vers la vue Statistiques");
    }

    @FXML
    private void handleParametresClick(ActionEvent event) {
        SceneManager.switchScene(btnParametres, "/view/SettingsView.fxml", "MediConnect - Settings", 1500, 750);
        System.out.println("Navigation vers la vue Paramètres");
    }

    @FXML
    private void handleAideClick(ActionEvent event) {
        SceneManager.switchScene(btnAide, "/view/AideView.fxml", "MediConnect - Aide", 1500, 750);
        System.out.println("Navigation vers la vue Aide");
    }

    // ============================================================
    // ========== ACTIONS SUR LA VUE PRINCIPALE ===================
    // ============================================================
    
    @FXML
    private void handleExportPatientsCSV() {
        List<String> headers = List.of(
            "ID", "Nom du Patient", "Type", "Posologie", "Date de début",
            "Date de fin", "Statut", "Description", "ID du Patient"
        );

        List<List<String>> rows = new ArrayList<>();

        for (Traitement traitement : traitementsList) {
            rows.add(List.of(
                String.valueOf(traitement.getId()),
                traitement.getNomPatient(),
                traitement.getType(),
                traitement.getPosologie(),
                traitement.getDateDebutFormatted(),
                traitement.getDateFinFormatted(),
                traitement.getStatut(),
                traitement.getDescription(),
                String.valueOf(traitement.getPatientId())
            ));
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer les traitements");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files (.csv)", ".csv"));
        File file = fileChooser.showSaveDialog(btnExporter.getScene().getWindow());

        if (file != null) {
            try {
                CSVExporter.exportToCSV(file.getAbsolutePath(), headers, rows);
                AlertMessage.showInfoAlert("Succès", "Exportation réussie", "Les traitements ont été exportés avec succès.");
            } catch (Exception e) {
                AlertMessage.showErrorAlert("Erreur", "Échec de l'exportation", "Une erreur est survenue lors de l'exportation :\n" + e.getMessage());
            }
        }
    }
    
    // --- Gestion de select all
    @FXML
    public void handleSelectAllCheckbox(ActionEvent event) {
        boolean selectAll = selectAllCheckbox.isSelected();
        
        for (Traitement traitement : traitementsList) {
            traitement.setSelected(selectAll);
        }
        
        updateDeleteButtonState();
    }

    // --- Navigation vers le formulaire d'ajout
    @FXML
    public void handleAddTraitement(ActionEvent event) {
        comboPatient.setValue(null);
        inputType.clear();
        inputDateDebut.setValue(LocalDate.now());
        inputDateFin.setValue(LocalDate.now());
        inputDescription.setText("");
        
        traitementsListView.setVisible(false);
        traitementsAddView.setVisible(true);
        modifierTraitementView.setVisible(false);
    }
    
    // --- GESTION DE LA SUPPRESSION (événements @FXML) ========
    @FXML
    public void handleDeleteTraitement(ActionEvent event) {
        List<Traitement> selectedTraitements = new ArrayList<>();
        for (Traitement traitement : traitementsList) {
            if (traitement.isSelected()) {
                selectedTraitements.add(traitement);
            }
        }

        if (selectedTraitements.isEmpty()) {
            return;
        }

        String header;
        if (selectedTraitements.size() == 1) {
            header = "Voulez-vous vraiment supprimer ce traitement ?";
        } else {
            header = "Voulez-vous vraiment supprimer ces " + selectedTraitements.size() + " traitements ?";
        }

        String titre = "Confirmation de suppression";
        String content = "Cette action ne peut pas être annulée.";

        boolean confirmed = AlertMessage.showConfirmationAlert(titre, header, content);

        if (confirmed) {
            traitementsList.removeAll(selectedTraitements);
            for (Traitement t : selectedTraitements) {
                    supprimerTraitementDeDB(t); // <== suppression de la base de données
                }
            selectAllCheckbox.setSelected(false);
            updateDeleteButtonState();
        }
    }
    
    private void supprimerTraitementDeDB(Traitement traitement) {
        String sqlDelete = "DELETE FROM traitement WHERE id = ?";

        try (Connection connect = Database.connectDB();
             PreparedStatement pstmt = connect.prepareStatement(sqlDelete)) {

            pstmt.setInt(1, traitement.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =======================================================================
    // ========== ACTIONS SUR LA VUE DU FORMULAIRE D'AJOUT ===================
    // =======================================================================
    @FXML
    public void handleConfirmerAjoutClick(ActionEvent event) {
        if (comboPatient.getValue() == null || inputType.getText().isEmpty() || inputDateDebut.getValue() == null) {
            AlertMessage.showErrorAlert("Erreur", "Champs vides","Veuillez remplir tous les champs obligatoires.");
            return;
        }
        
        try {
            String nomPatient = comboPatient.getValue();
            String type = inputType.getText();
            LocalDate dateDebut = inputDateDebut.getValue();
            LocalDate dateFin = inputDateFin.getValue();
            String description = inputDescription.getText();
            
            String statut = (dateFin == null || dateFin.isAfter(LocalDate.now())) ? "En cours" : "Terminé";
            
            Traitement nouveauTraitement = new Traitement(
                nomPatient,
                type,
                dateDebut,
                dateFin,
                statut,
                description
            );
            
            traitementsList.add(nouveauTraitement);
            ajouterTraitementDansDB(nouveauTraitement);
            tableTraitements.refresh();
            
            traitementsListView.setVisible(true);
            traitementsAddView.setVisible(false);
            modifierTraitementView.setVisible(false);
            
        } catch (Exception e) {
            AlertMessage.showErrorAlert("Erreur", "Format de date invalide", "Format de date invalide. Utilisez le format JJ/MM/AAAA.");
        }
    }
    
    @FXML
    public void handleAnnulerAjoutClick(ActionEvent event) {
        traitementsListView.setVisible(true);
        traitementsAddView.setVisible(false);
        modifierTraitementView.setVisible(false);
    }
    
    private void ajouterTraitementDansDB(Traitement traitement) {
        String queryId = "SELECT id FROM patient WHERE nom = ? AND prenom = ?";
        String insertSQL = "INSERT INTO traitement (nom, posologie, date_debut, date_fin, type, actif, description, patient_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connect = Database.connectDB();
             PreparedStatement stmt = connect.prepareStatement(queryId);
             PreparedStatement insertStmt = connect.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
            
            String[] nomPatientParts = traitement.getNomPatient().split(" ");
            
            stmt.setString(1, nomPatientParts[0]);
            stmt.setString(2, nomPatientParts[1]);
            
            // Récupérer l'ID du patient
            int patientId = 0;
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                patientId = resultSet.getInt("id");
            } else {
                System.out.println("Patient not found.");
            }

            insertStmt.setString(1, traitement.getNomPatient());
            insertStmt.setString(2, "");
            insertStmt.setString(3, traitement.getDateDebutFormatted());
            insertStmt.setString(4, traitement.getDateFinFormatted());
            insertStmt.setString(5, traitement.getType());
            insertStmt.setBoolean(6, traitement.getStatut().equals("En cours"));
            insertStmt.setString(7, traitement.getDescription());
            insertStmt.setInt(8, patientId);

            insertStmt.executeUpdate();
            
            // Récupérer l'ID généré
            try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1); // Obtenir l'ID généré
                    traitement.setId(generatedId); // L'assigner à l'objet Traitement
                } else {
                    throw new SQLException("Création du traitement échouée, aucun ID obtenu.");
                }
            }
            
            } catch (SQLException e) {
            e.printStackTrace();
            AlertMessage.showErrorAlert(
            "Erreur", 
            "Erreur BDD : ", 
            e.getMessage());
        }
    }

    // =======================================================================
    // ========== ACTIONS SUR LA VUE DU FORMULAIRE DE MODIFICATION ===========
    // =======================================================================
    @FXML
    public void handleConfirmerModificationClick(ActionEvent event) {
        if (comboPatientModif.getValue() == null || inputTypeModif.getText().isEmpty() || datePickerDebutModif.getValue() == null) {
            AlertMessage.showErrorAlert("Erreur", "Champs vides","Veuillez remplir tous les champs obligatoires.");
            return;
        }
        
        traitementEnEdition.setNomPatient(comboPatientModif.getValue());
        traitementEnEdition.setType(inputTypeModif.getText());
        traitementEnEdition.setPosologie(inputPosologieModif.getText());
        traitementEnEdition.setDateDebut(datePickerDebutModif.getValue());
        traitementEnEdition.setDateFin(datePickerFinModif.getValue());
        traitementEnEdition.setStatut(checkBoxActifModif.isSelected() ? "En cours" : "Terminé");
        traitementEnEdition.setDescription(textAreaObservationsModif.getText());
        
        modifierTraitementDansDB(traitementEnEdition);
        
        tableTraitements.refresh();
        
        traitementsListView.setVisible(true);
        traitementsAddView.setVisible(false);
        modifierTraitementView.setVisible(false);
        
        AlertMessage.showInfoAlert(
            "Succès", 
            "Modification réussie", 
            "Traitement modifié avec succès !"
        );
    }
    
    @FXML
    public void handleAnnulerModificationClick(ActionEvent event) {
        traitementsListView.setVisible(true);
        traitementsAddView.setVisible(false);
        modifierTraitementView.setVisible(false);
    }
    
    private void modifierTraitementDansDB(Traitement traitement) { 
        String updateSQL = "UPDATE traitement SET nom = ?, posologie = ?, date_debut = ?, date_fin = ?, type = ?, actif = ? , description = ? WHERE id = ?";

        try (Connection connect = Database.connectDB();
            PreparedStatement updateStmt = connect.prepareStatement(updateSQL)) {

            updateStmt.setString(1, traitement.getNomPatient());
            updateStmt.setString(2, traitement.getPosologie());
            updateStmt.setString(3, traitement.getDateDebutFormatted());
            updateStmt.setString(4, traitement.getDateFinFormatted());
            updateStmt.setString(5, traitement.getType());
            updateStmt.setBoolean(6, traitement.getStatut().equals("En cours"));
            updateStmt.setString(7, traitement.getDescription());
            updateStmt.setInt(8, traitement.getId());

            updateStmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            AlertMessage.showErrorAlert(
            "Erreur", 
            "Erreur BDD : ", 
            e.getMessage());
        }
    }

    // ============================================================
    // =================== MÉTHODES AUXILIAIRES ===================
    // ============================================================
    
    // ---- Charger les traitements de la DB ----
    private void loadTraitementsFromDatabase() {
        traitementsList.clear();
        String url = "jdbc:sqlite:src/resources/database/MedicalTreatmentAppDB.db";

        String query = "SELECT id, nom, posologie, date_debut, date_fin, type, actif, description, patient_id FROM traitement";

        try (Connection connect = DriverManager.getConnection(url);
             PreparedStatement prepare = connect.prepareStatement(query);
             ResultSet result = prepare.executeQuery();){
            
            while (result.next()) {
                int id = result.getInt("id");
                String nomPatient = result.getString("nom");
                String posologie = result.getString("posologie");
                LocalDate dateDebut = LocalDate.parse(result.getString("date_debut"));
                LocalDate dateFin = LocalDate.parse(result.getString("date_fin"));
                String type = result.getString("type");
                String statut = result.getBoolean("actif") ? "En cours" : "Terminé";
                String description = result.getString("description");
                int patientId = result.getInt("patient_id");

                traitementsList.add(new Traitement(id, nomPatient, posologie, type, dateDebut, dateFin, statut, description, patientId));
                
                filteredTraitements = new FilteredList<>(traitementsList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertMessage.showErrorAlert("Erreur", "Erreur de la base de données", 
                "Erreur de la base de données : " + e.getMessage());
        }
    }
    
    // ---- Charger les donnees des patients de la DB ----
    private void loadPatientsFromDatabase() {
        patientsList.clear();
        String url = "jdbc:sqlite:src/resources/database/MedicalTreatmentAppDB.db";

        String query = "SELECT nom, prenom FROM patient";

        try (Connection connect = DriverManager.getConnection(url);
             PreparedStatement prepare = connect.prepareStatement(query);
             ResultSet result = prepare.executeQuery();){
            
            while (result.next()) {
                String nom = result.getString("nom");
                String prenom = result.getString("prenom");

                patientsList.add(nom + " " + prenom);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertMessage.showErrorAlert("Erreur", "Erreur de la base de données", 
                "Erreur de la base de données : " + e.getMessage());
        }
    }

    // ---- Gestion de la recherche et filtrage ----
    private void setupSearchFilter() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredTraitements.setPredicate(traitement -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                String lowerCaseFilter = newValue.toLowerCase();
                
                if (traitement.getNomPatient().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } 
                else if (traitement.getType().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (traitement.getStatut().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                
                return false;
            });
        });
    }

    // ---- Configuration du tableau ----
    private void setupCheckboxColumn() {
        checkboxColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        checkboxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkboxColumn));
        checkboxColumn.setEditable(true);
        
        tableTraitements.setEditable(true);
        
        for (Traitement patient : traitementsList) {
            patient.selectedProperty().addListener((obs, oldVal, newVal) -> {
                updateDeleteButtonState();

                boolean allSelected = true;
                for (Traitement t : traitementsList) {
                    if (!t.isSelected()) {
                        allSelected = false;
                        break;
                    }
                }

                if (selectAllCheckbox.isSelected() != allSelected) {
                    selectAllCheckbox.setSelected(allSelected);
                }
            });
        }
    }
    
    private void setupDataColumns() {
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nomPatient"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        dateDebutColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateDebutFormatted()));
        dateFinColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateFinFormatted()));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
    }
    
    private void setupActionsColumn() {
        actionsColumn.setCellFactory(new Callback<TableColumn<Traitement, Void>, TableCell<Traitement, Void>>() {
            @Override
            public TableCell<Traitement, Void> call(final TableColumn<Traitement, Void> param) {
                return new TableCell<Traitement, Void>() {
                    private final Button btnView = new Button();
                    private final Button btnEdit = new Button();
                    private final Button btnDelete = new Button();
                    private final HBox actionPane = new HBox(5);
                    
                    {
                        // Configuration des icônes pour les boutons
                        ImageView viewIcon = new ImageView(new Image(getClass().getResourceAsStream("/resources/icons/Eye.png")));
                        viewIcon.setFitHeight(16);
                        viewIcon.setFitWidth(16);
                        btnView.setGraphic(viewIcon);
                        btnView.setStyle("-fx-background-color: transparent;");
                        
                        ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/resources/icons/Edit 3.png")));
                        editIcon.setFitHeight(16);
                        editIcon.setFitWidth(16);
                        btnEdit.setGraphic(editIcon);
                        btnEdit.setStyle("-fx-background-color: transparent;");
                        
                        ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/resources/icons/Trash 3.png")));
                        deleteIcon.setFitHeight(16);
                        deleteIcon.setFitWidth(16);
                        btnDelete.setGraphic(deleteIcon);
                        btnDelete.setStyle("-fx-background-color: transparent;");
                        
                        btnView.setOnAction(event -> {
                            Traitement traitement = getTableView().getItems().get(getIndex());
                            viewTraitement(traitement);
                        });
                        
                        btnEdit.setOnAction(event -> {
                            Traitement traitement = getTableView().getItems().get(getIndex());
                            editTraitement(traitement);
                        });
                        
                        btnDelete.setOnAction(event -> {
                            Traitement traitement = getTableView().getItems().get(getIndex());
                            deleteTraitement(traitement);
                        });
                        
                        actionPane.getChildren().addAll(btnView, btnEdit, btnDelete);
                        actionPane.setAlignment(Pos.CENTER);
                    }
                    
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(actionPane);
                        }
                    }
                };
            }
        });
    }

    // ---- Configuration des vues ----
    private void setupViews() {
        traitementsListView.setVisible(true);
        traitementsAddView.setVisible(false);
        modifierTraitementView.setVisible(false);
        
        comboPatientModif.setItems(patientsList);
    }

    // ---- Gestion des traitements ----
    private void viewTraitement(Traitement traitement) {
        String titre = "Détails du traitement";
        String header = "Traitement de " + traitement.getNomPatient();

        String content = "Type: " + traitement.getType() + "\n" +
                         "Date de début: " + traitement.getDateDebutFormatted() + "\n" +
                         "Date de fin: " + traitement.getDateFinFormatted() + "\n" +
                         "Statut: " + traitement.getStatut();

        if (traitement.getPosologie() != null && !traitement.getPosologie().isEmpty()) {
            content += "\nPosologie: " + traitement.getPosologie();
        }
        if (traitement.getDescription() != null && !traitement.getDescription().isEmpty()) {
            content += "\n\nDescription: " + traitement.getDescription();
        }

        AlertMessage.showInfoAlert(titre, header, content);
    }
    
    private void editTraitement(Traitement traitement) {
        traitementEnEdition = traitement;
        
        labelTraitementId.setText(String.valueOf(traitement.getId()));
        comboPatientModif.setValue(traitement.getNomPatient());
        inputTypeModif.setText(traitement.getType());
        inputPosologieModif.setText(traitement.getPosologie());
        datePickerDebutModif.setValue(traitement.getDateDebut());
        datePickerFinModif.setValue(traitement.getDateFin());
        checkBoxActifModif.setSelected(traitement.getStatut().equals("En cours"));
        textAreaObservationsModif.setText(traitement.getDescription());
        
        traitementsListView.setVisible(false);
        traitementsAddView.setVisible(false);
        modifierTraitementView.setVisible(true);
    }
    
    private void deleteTraitement(Traitement traitement) {
        boolean confirmed = AlertMessage.showConfirmationAlert(
                "Confirmation de suppression",
                "Voulez-vous vraiment supprimer ce traitement ?",
                "Cette action ne peut pas être annulée."
        );

        if (confirmed) {
            supprimerTraitementDeDB(traitement);
            traitementsList.remove(traitement);
            AlertMessage.showInfoAlert(
                "Succès", 
                "Suppression réussie", 
                "Traitement supprimé avec succès !"
            );
            updateDeleteButtonState();
        }
    }

    // ---- Gestion de l'état du bouton de suppression d'un traitement ----
    private void updateDeleteButtonState() {
        boolean atLeastOneSelected = false;
        
        for (Traitement traitement : traitementsList) {
            if (traitement.isSelected()) {
                atLeastOneSelected = true;
                break;
            }
        }
        
        anyTraitementSelected.set(atLeastOneSelected);
        
        if (atLeastOneSelected) {
            btnSupprimerTraitements.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-background-radius: 8px; -fx-border-radius: 8px;");
        } else {
            btnSupprimerTraitements.setStyle("-fx-background-color: #F8F8F8; -fx-border-color: #CCCCCC; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-text-fill: #333333;");
        }
    }

    // ============================================================
    // ============ PROPRIÉTÉS OBSERVABLES ET BINDINGS ============
    // ============================================================
    public BooleanProperty anyTraitementSelectedProperty() {
        return anyTraitementSelected;
    }
    
    public boolean isAnyTraitementSelected() {
        return anyTraitementSelected.get();
    }
}