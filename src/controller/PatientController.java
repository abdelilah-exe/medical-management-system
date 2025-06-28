package controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.io.File;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.DatePicker;

import model.Patient;
import utils.CSVExporter;
import utils.AlertMessage;
import utils.Database;
import utils.SceneManager;

/**
 * Contrôleur pour la gestion des patients dans l'application MediConnect.
 * Permet de créer, lire, mettre à jour et supprimer les données patients.
 *
 * @author pc
 */

public class PatientController {

    // ============================================================
    // ================ COMPOSANTS INTERFACE FXML ================
    // ============================================================

    // --- Navigation
    @FXML private Button btnAccueil;
    @FXML private Button btnTraitements;
    @FXML private Button btnRendezVous;
    @FXML private Button btnStatistiques;
    @FXML private Button btnParametres;
    @FXML private Button btnAide;

    // --- Actions principales
    @FXML private Button btnExporter;
    @FXML private Button btnSupprimerPatients;

    // --- Organisation des vues
    @FXML private VBox patientsListView;
    @FXML private VBox patientsAddView;
    @FXML private VBox patientsEditView;

    // --- Tableau de patients
    @FXML private TableView<Patient> tablePatients;
    @FXML private TableColumn<Patient, Boolean> checkboxColumn;
    @FXML private TableColumn<Patient, String> nomColumn;
    @FXML private TableColumn<Patient, String> prenomColumn;
    @FXML private TableColumn<Patient, String> dateNaissanceColumn;
    @FXML private TableColumn<Patient, String> sexeColumn;
    @FXML private TableColumn<Patient, Void> actionsColumn;

    // --- Bar de recherche
    @FXML private TextField searchField;

    // --- Select all checkbox
    @FXML private CheckBox selectAllCheckbox;

    // --- Formulaire d'ajout
    @FXML private TextField inputNom;
    @FXML private TextField inputPrenom;
    @FXML private TextField inputSecuriteSociale;
    @FXML private TextField inputTelephone;
    @FXML private DatePicker inputDateNaissance;
    @FXML private RadioButton radioHomme;
    @FXML private RadioButton radioFemme;

    // --- Formulaire d'édition
    @FXML private TextField editNom;
    @FXML private TextField editPrenom;
    @FXML private TextField editSecuriteSociale;
    @FXML private TextField editTelephone;
    @FXML private DatePicker editDateNaissance;
    @FXML private RadioButton editRadioHomme;
    @FXML private RadioButton editRadioFemme;

    // ============================================================
    // ============== ATTRIBUTS ET STRUCTURES DE DONNÉES ==========
    // ============================================================
    // --- Propriété booléenne indiquant si au moins un patient est actuellement sélectionné.
    private BooleanProperty anyPatientSelected = new SimpleBooleanProperty(false);

    // --- Référence au patient sélectionné pour une opération de modification.
    private Patient patientToEdit;

    // --- List contenant les patients
    private ObservableList<Patient> patients = FXCollections.observableArrayList();

    // --- List contenant les patients correspondant à la recherche.
    private FilteredList<Patient> filteredPatients;

    // ==============================================================================================================================
    // ================= MÉTHODE D’INITIALISATION (Méthode appelée automatiquement après le chargement du fichier FXML) =============
    // ==============================================================================================================================
    @FXML
    public void initialize() {
        // Configuration des vues
        patientsListView.setVisible(true);
        patientsAddView.setVisible(false);
        patientsEditView.setVisible(false);

        // Charge les patients depuis la base de données
        loadPatientsFromDatabase();

        // Configure la recherche avec filtrage dynamique
        setupSearch();

        // Configure les éléments du tableau
        tablePatients.setItems(filteredPatients);
        tablePatients.setEditable(true);

        configureCheckboxColumn();
        configureDataColumns();
        configureActionsColumn();

        // Configure les interactions entre éléments de l'interface
        btnSupprimerPatients.disableProperty().bind(anyPatientSelected.not());

        tablePatients.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            updateDeleteButtonState();
        });
    }

    // =============================================================================
    // ============== ACTIONS SUR LE MENU LATÉRAL (Navigation entre les vues) ======
    // =============================================================================
    @FXML
    private void handleAccueilClick(ActionEvent event) {
        SceneManager.switchScene(btnAccueil, "/view/AcceuilView.fxml", "MediConnect - Acceuil", 1500, 750);
        System.out.println("Navigation vers la vue d'accueil");
    }

    @FXML
    private void handlePatientsClick(ActionEvent event) {
        System.out.println("Page patients actuelle");
    }

    @FXML
    private void handleTraitementsClick(ActionEvent event) {
        SceneManager.switchScene(btnTraitements, "/view/TraitementView.fxml", "MediConnect - Traitments", 1500, 750);
        System.out.println("Navigation vers la vue Traitements");
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
            "ID", "Nom", "Prénom", "Date de naissance",
            "Sexe", "Téléphone", "Numéro Sécu", "Date création"
        );

        List<List<String>> rows = new ArrayList<>();

        for (Patient patient : patients) {
            rows.add(List.of(
                String.valueOf(patient.getId()),
                patient.getNom(),
                patient.getPrenom(),
                patient.getDateNaissance().toString(),
                patient.getSexe(),
                patient.getNumTelephone(),
                patient.getNumSecuriteSociale(),
                patient.getDateCreation()
            ));
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer les patients");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files (.csv)", ".csv"));
        File file = fileChooser.showSaveDialog(btnExporter.getScene().getWindow());

        if (file != null) {
            try {
                CSVExporter.exportToCSV(file.getAbsolutePath(), headers, rows);
                AlertMessage.showInfoAlert("Succès", "Exportation réussie", "Les patients ont été exportés avec succès.");
            } catch (Exception e) {
                AlertMessage.showErrorAlert("Erreur", "Échec de l'exportation", "Une erreur est survenue lors de l'exportation :\n" + e.getMessage());
            }
        }
    }

    // --- Gestion de select all
    @FXML
    public void handleSelectAllCheckbox() {
        boolean selectAll = selectAllCheckbox.isSelected();

        for (Patient patient : patients) {
            patient.setSelected(selectAll);
        }

        updateDeleteButtonState();
    }

    // --- Navigation vers le formulaire d'ajout
    @FXML
    public void handleAddPatient() {
        resetAddPatientForm();
        patientsListView.setVisible(false);
        patientsAddView.setVisible(true);
        patientsEditView.setVisible(false);
    }

    // --- GESTION DE LA SUPPRESSION (événements @FXML) ========
    @FXML
    public void handleDeletePatients() {
        ObservableList<Patient> patientsToRemove = FXCollections.observableArrayList();

        for (Patient patient : patients) {
            if (patient.isSelected()) {
                patientsToRemove.add(patient);
            }
        }

        if (!patientsToRemove.isEmpty()) {
            boolean confirmed = AlertMessage.showConfirmationAlert(
                    "Confirmation",
                    "Suppression de patient(s)",
                    "Êtes-vous sûr de vouloir supprimer " + patientsToRemove.size() + " patient(s) ?"
            );

            if (confirmed) {
                for (Patient p : patientsToRemove) {
                    supprimerPatientDeDB(p); // <== suppression de la base de données
                }
                patients.removeAll(patientsToRemove);
                selectAllCheckbox.setSelected(false);
                updateDeleteButtonState();

                AlertMessage.showInfoAlert(
                        "Succès",
                        "Suppression réussie",
                        "Patient(s) supprimé(s) avec succès !"
                );
            }
        } else {
            AlertMessage.showErrorAlert(
                    "Erreur",
                    "Aucune sélection",
                    "Aucun patient sélectionné."
            );
        }
    }

    // Methode pour supprimer les patients de la DB
    private void supprimerPatientDeDB(Patient patient) {
        String sql = "DELETE FROM patient WHERE id = ?";

        try (Connection connect = Database.connectDB();
             PreparedStatement pstmt = connect.prepareStatement(sql)) {

            pstmt.setInt(1, patient.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // =======================================================================
    // ========== ACTIONS SUR LA VUE DU FORMULAIRE D'AJOUT ===================
    // =======================================================================

    @FXML
    private void handleConfirmerAjoutClick() {
        if (!validatePatientInputs(inputNom, inputPrenom, inputDateNaissance, inputSecuriteSociale, inputTelephone)) {
            return;
        }

        String nom = inputNom.getText().trim();
        String prenom = inputPrenom.getText().trim();
        LocalDate dateNaissance = inputDateNaissance.getValue();
        String tele = inputTelephone.getText().trim();
        String secu = inputSecuriteSociale.getText().trim();
        String sexe = radioHomme.isSelected() ? "Homme" : (radioFemme.isSelected() ? "Femme" : "");

        Patient newPatient = new Patient(nom, prenom, dateNaissance, sexe, tele, secu);
        
        // Sauvegarder en base de données
        if(!ajouterPatientDansDB(newPatient)) {
            return; // Sortir si la sauvegarde échoue
        }
        
        patients.add(newPatient);

        patientsListView.setVisible(true);
        patientsAddView.setVisible(false);

        AlertMessage.showInfoAlert(
                "Succès",
                "Ajout réussi",
                "Patient ajouté avec succès !"
        );
    }

    @FXML
    private void handleAnnulerAjoutClick() {
        resetAddPatientForm();
        patientsListView.setVisible(true);
        patientsAddView.setVisible(false);
    }

    private boolean ajouterPatientDansDB(Patient patient) {

        String checkSecuSQL = "SELECT * FROM patient WHERE numero_securite_sociale = ?";
        String insertSQL = "INSERT INTO patient (nom, prenom, date_naissance, sexe, numero_telephone, numero_securite_sociale, date_creation) VALUES (?, ?, ?, ?, ?, ?, datetime('now'))";

        try (Connection connect = Database.connectDB();
             PreparedStatement checkStmt = connect.prepareStatement(checkSecuSQL);
             PreparedStatement insertStmt = connect.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {

            checkStmt.setString(1, patient.getNumSecuriteSociale());
            ResultSet result = checkStmt.executeQuery();
            if (result.next()) {
                AlertMessage.showErrorAlert(
                        "Erreur",
                        "Patient déjà existant",
                        "Un patient avec NSS: " + patient.getNumSecuriteSociale() + " existe déjà !");
                return false;
            }

            insertStmt.setString(1, patient.getNom());
            insertStmt.setString(2, patient.getPrenom());
            insertStmt.setString(3, patient.getDateNaissance().toString());
            insertStmt.setString(4, patient.getSexe());
            insertStmt.setString(5, patient.getNumTelephone());
            insertStmt.setString(6, patient.getNumSecuriteSociale());

            int res = insertStmt.executeUpdate();

            // Récupérer l'ID généré
            try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1); // Obtenir l'ID généré
                    patient.setId(generatedId); // L'assigner à l'objet Patient
                } else {
                    throw new SQLException("Création du patient échouée, aucun ID obtenu.");
                }
            }

            // Récupérer la date de création généré
            String selectSQL = "SELECT date_creation FROM patient WHERE id = ?";
            try (PreparedStatement selectStmt = connect.prepareStatement(selectSQL)) {
                selectStmt.setInt(1, patient.getId());
                ResultSet selectResult = selectStmt.executeQuery();
                if (selectResult.next()) {
                    String dateCreation = selectResult.getString("date_creation");
                    patient.setDateCreation(dateCreation);
                }
            }
            
            return res > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            AlertMessage.showErrorAlert(
                    "Erreur",
                    "Erreur BDD : ",
                    e.getMessage());
            return false;
        }
    }

    // =======================================================================
    // ========== ACTIONS SUR LA VUE DU FORMULAIRE DE MODIFICATION ===========
    // =======================================================================
    @FXML
    private void handleConfirmerModificationClick() {
        if (!validatePatientInputs(inputNom, inputPrenom, inputDateNaissance, inputSecuriteSociale, inputTelephone)) {
            return;
        }

        patientToEdit.setNom(editNom.getText().trim());
        patientToEdit.setPrenom(editPrenom.getText().trim());
        patientToEdit.setDateNaissance(editDateNaissance.getValue());
        patientToEdit.setNumTelephone(editTelephone.getText().trim());
        patientToEdit.setNumSecuriteSociale(editSecuriteSociale.getText().trim());
        patientToEdit.setSexe(editRadioHomme.isSelected() ? "Homme" : (editRadioFemme.isSelected() ? "Femme" : ""));

        modifierPatientDansDB(patientToEdit);

        tablePatients.refresh();

        patientsListView.setVisible(true);
        patientsEditView.setVisible(false);

        AlertMessage.showInfoAlert(
                "Succès",
                "Modification réussie",
                "Patient modifié avec succès !"
        );
    }

    @FXML
    private void handleAnnulerModificationClick() {
        patientsListView.setVisible(true);
        patientsEditView.setVisible(false);
    }

    private void modifierPatientDansDB(Patient patient) {
        String updateSQL = "UPDATE patient SET nom = ?, prenom = ?, date_naissance = ?, sexe = ?, numero_telephone = ?, numero_securite_sociale = ? WHERE id = ?";

        try (Connection connect = Database.connectDB();
             PreparedStatement updateStmt = connect.prepareStatement(updateSQL)) {

            updateStmt.setString(1, patient.getNom());
            updateStmt.setString(2, patient.getPrenom());
            updateStmt.setString(3, patient.getDateNaissance().toString());
            updateStmt.setString(4, patient.getSexe());
            updateStmt.setString(5, patient.getNumTelephone());
            updateStmt.setString(6, patient.getNumSecuriteSociale());
            updateStmt.setInt(7, patient.getId());

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
    // ---- Gestion de la base de données ----
    private void loadPatientsFromDatabase() {
        patients.clear();
        String url = "jdbc:sqlite:src/resources/database/MedicalTreatmentAppDB.db";

        String query = "SELECT id, nom, prenom, date_naissance, sexe, numero_telephone, numero_securite_sociale, date_creation FROM patient";

        try (Connection connect = DriverManager.getConnection(url);
             PreparedStatement prepare = connect.prepareStatement(query);
             ResultSet result = prepare.executeQuery();){

            while (result.next()) {
                int id = result.getInt("id");
                String nom = result.getString("nom");
                String prenom = result.getString("prenom");
                LocalDate dateNaissance = LocalDate.parse(result.getString("date_naissance"));
                String sexe = result.getString("sexe");
                String numTelephone = result.getString("numero_telephone");
                String numSecuriteSociale = result.getString("numero_securite_sociale");
                String dateCreation = result.getString("date_creation");

                patients.add(new Patient(id, nom, prenom, dateNaissance, sexe, numTelephone, numSecuriteSociale, dateCreation));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertMessage.showErrorAlert("Erreur", "Erreur de la base de données",
                    "Erreur de la base de données : " + e.getMessage());
        }
    }

    // ---- Gestion de la recherche et filtrage ----
    private void setupSearch() {
        filteredPatients = new FilteredList<>(patients, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredPatients.setPredicate(patient -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                return patient.getNom().toLowerCase().contains(lowerCaseFilter) ||
                        patient.getPrenom().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }

    // ---- Configuration du tableau ----
    private void configureCheckboxColumn() {
        checkboxColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        checkboxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkboxColumn));
        checkboxColumn.setEditable(true);

        for (Patient patient : patients) {
            patient.selectedProperty().addListener((obs, oldVal, newVal) -> {
                updateDeleteButtonState();

                boolean allSelected = true;
                for (Patient p : patients) {
                    if (!p.isSelected()) {
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

    private void configureDataColumns() {
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        dateNaissanceColumn.setCellValueFactory(new PropertyValueFactory<>("dateNaissance"));
        sexeColumn.setCellValueFactory(new PropertyValueFactory<>("sexe"));
    }

    private void configureActionsColumn() {
        Callback<TableColumn<Patient, Void>, TableCell<Patient, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Patient, Void> call(final TableColumn<Patient, Void> param) {
                return new TableCell<>() {
                    private final HBox actionsPane = new HBox(5);
                    private final Button btnView = createActionButton("view");
                    private final Button btnEdit = createActionButton("edit");
                    private final Button btnDelete = createActionButton("delete");

                    {
                        actionsPane.setAlignment(Pos.CENTER);
                        actionsPane.getChildren().addAll(btnView, btnEdit, btnDelete);

                        btnView.setOnAction((ActionEvent event) -> {
                            Patient patient = getTableView().getItems().get(getIndex());
                            System.out.println("Voir les détails de " + patient.getNom());
                            showPatientDetails(patient);
                        });

                        btnEdit.setOnAction((ActionEvent event) -> {
                            Patient patient = getTableView().getItems().get(getIndex());
                            showEditPatientForm(patient);
                        });

                        btnDelete.setOnAction((ActionEvent event) -> {
                            Patient patient = getTableView().getItems().get(getIndex());
                            System.out.println("When clicking on edit:" + patient.getId());
                            deletePatient(patient);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : actionsPane);
                    }
                };
            }
        };

        actionsColumn.setCellFactory(cellFactory);
    }

    private Button createActionButton(String type) {
        Button btn = new Button();
        btn.getStyleClass().add("action-button");
        btn.setStyle("-fx-background-color: transparent;");

        ImageView icon = new ImageView();
        icon.setFitWidth(16);
        icon.setFitHeight(16);
        icon.setPreserveRatio(true);

        switch (type) {
            case "view":
                icon.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream("/resources/icons/Eye.png")));
                break;
            case "edit":
                icon.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream("/resources/icons/Edit 3.png")));
                break;
            case "delete":
                icon.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream("/resources/icons/Trash 3.png")));
                break;
        }

        btn.setGraphic(icon);
        return btn;
    }

    private void showPatientDetails(Patient patient) {
        // Create a message to show in the alert, combining the patient details
        String patientDetails = String.format(
                "Nom: %s\nPrénom: %s\nDate de Naissance: %s\nSexe: %s\nNuméro de Téléphone: %s\n" +
                        "Numéro de Sécurité Sociale: %s\nDate de Création: %s",
                patient.getNom(),
                patient.getPrenom(),
                patient.getDateNaissance(),
                patient.getSexe(),
                patient.getNumTelephone(),
                patient.getNumSecuriteSociale(),
                patient.getDateCreation()
        );

        // Create an Alert to display the details
        AlertMessage.showInfoAlert(
                "Détails du Patient",
                "Informations complètes du patient",
                patientDetails
        );
    }

    // ---- Gestion du formulaire d'ajout ----
    private void resetAddPatientForm() {
        inputNom.clear();
        inputPrenom.clear();
        inputSecuriteSociale.clear();
        inputTelephone.clear();
        inputDateNaissance.setValue(LocalDate.now());

        radioHomme.setSelected(false);
        radioFemme.setSelected(false);
    }

    // ---- Gestion du formulaire de modification ----
    private void showEditPatientForm(Patient patient) {
        patientToEdit = patient;

        editNom.setText(patient.getNom());
        editPrenom.setText(patient.getPrenom());
        editSecuriteSociale.setText(patient.getNumSecuriteSociale());
        editTelephone.setText(patient.getNumTelephone());
        editDateNaissance.setValue(patient.getDateNaissance());

        if ("Homme".equals(patient.getSexe())) {
            editRadioHomme.setSelected(true);
        } else if ("Femme".equals(patient.getSexe())) {
            editRadioFemme.setSelected(true);
        }

        patientsListView.setVisible(false);
        patientsAddView.setVisible(false);
        patientsEditView.setVisible(true);
    }

    // ---- Gestion de la suppression ----
    private void deletePatient(Patient patient) {
        boolean confirmed = AlertMessage.showConfirmationAlert(
                "Confirmation",
                "Suppression de patient",
                "Êtes-vous sûr de vouloir supprimer " + patient.getNom() + " " + patient.getPrenom() + " ?"
        );

        if (confirmed) {
            patients.remove(patient);
            supprimerPatientDeDB(patient);
            AlertMessage.showInfoAlert(
                    "Succès",
                    "Suppression réussie",
                    "Patient supprimé avec succès !"
            );
        }
    }

    // ---- Validation des formulaires ----
    private boolean validatePatientInputs(TextField nom, TextField prenom, DatePicker dateNaissance, TextField numeroSecuriteSocialeField, TextField numeroTelephoneField) {
        StringBuilder errorMessage = new StringBuilder();

        // Vérification des champs obligatoires
        if (nom.getText().trim().isEmpty()) {
            errorMessage.append("- Le nom est obligatoire\n");
        }

        if (prenom.getText().trim().isEmpty()) {
            errorMessage.append("- Le prénom est obligatoire\n");
        }

        if (dateNaissance.getValue() == null) {
            errorMessage.append("- La date de naissance est obligatoire\n");
        }

        // Vérification du numéro de sécurité sociale (15 chiffres)
        String nss = numeroSecuriteSocialeField.getText().trim();
        if (nss.isEmpty()) {
            errorMessage.append("- Le numéro de sécurité sociale est obligatoire\n");
        } else if (!nss.matches("[0-9]{15}")) {
            errorMessage.append("- Numéro de sécurité sociale invalide (15 chiffres requis)\n");
        }

        // Vérification du numéro de téléphone (10 chiffres)
        String tel = numeroTelephoneField.getText().trim();
        if (tel.isEmpty()) {
            errorMessage.append("- Le numéro de téléphone est obligatoire\n");
        } else if (!tel.matches("[0-9]{10}")) {
            errorMessage.append("- Numéro de téléphone invalide (10 chiffres requis)\n");
        }

        // Affichage d'une alerte si des erreurs sont détectées
        if (errorMessage.length() > 0) {
            AlertMessage.showErrorAlert(
                "Erreur",
                "Validation échouée",
                "Veuillez corriger les erreurs suivantes :\n" + errorMessage.toString()
            );
            return false;
        }

        return true;
    }

    // ---- Gestion de l'état du bouton de suppression d'un patient ----
    private void updateDeleteButtonState() {
        boolean atLeastOneSelected = false;

        for (Patient patient : patients) {
            if (patient.isSelected()) {
                atLeastOneSelected = true;
                break;
            }
        }

        anyPatientSelected.set(atLeastOneSelected);
        
        if (atLeastOneSelected) {
            btnSupprimerPatients.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-background-radius: 8px; -fx-border-radius: 8px;");
        } else {
            btnSupprimerPatients.setStyle("-fx-background-color: #F8F8F8; -fx-border-color: #CCCCCC; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-text-fill: #333333;");
        }
    }

    // ============================================================
    // ============ PROPRIÉTÉS OBSERVABLES ET BINDINGS ============
    // ============================================================
    public BooleanProperty anyPatientSelectedProperty() {
        return anyPatientSelected;
    }

    public boolean isAnyPatientSelected() {
        return anyPatientSelected.get();
    }
}