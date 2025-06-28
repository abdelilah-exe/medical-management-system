package controller;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import utils.Database;

import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import model.RendezVous;
import utils.AlertMessage;
import utils.CSVExporter;
import utils.SceneManager;

/**
 * Contrôleur pour la gestion des rendez-vous dans l'application MediConnect.
 * Permet la création, modification et suppression des rendez-vous patient.
 * 
 * @author pc
 */

public class RendezVousController implements Initializable {
    
    // ============================================================
    // ================ COMPOSANTS INTERFACE FXML ================
    // ============================================================
    
    // --- Navigation
    @FXML private Button btnAccueil;
    @FXML private Button btnPatients;
    @FXML private Button btnTraitements;
    @FXML private Button btnStatistiques;
    @FXML private Button btnParametres;
    @FXML private Button btnAide;
    
    // --- Actions principales
    @FXML private Button btnExporter;
    @FXML private Button btnSupprimer;
    
    // --- Organisation des vues
    @FXML private VBox rendezVousListView;
    @FXML private VBox planifierRendezVousView;
    @FXML private VBox modifierRendezVousView;

    // --- Tableau de rendez-vous
    @FXML private TableView<RendezVous> tableRendezVous;
    @FXML private TableColumn<RendezVous, Boolean> colCheckbox;
    @FXML private TableColumn<RendezVous, Integer> colId;
    @FXML private TableColumn<RendezVous, String> colNom;
    @FXML private TableColumn<RendezVous, LocalDate> colDate;
    @FXML private TableColumn<RendezVous, LocalTime> colHeure;
    @FXML private TableColumn<RendezVous, Void> colActions;

    // --- Bar de recherche
    @FXML private TextField searchField;
    
    // --- Select all checkbox
    @FXML private CheckBox selectAllCheckbox;
    
    // --- Formulaire de planification
    @FXML private ComboBox<String> comboPatient;
    @FXML private DatePicker datePickerRendezVous;
    @FXML private TextField inputHeure;
    @FXML private TextField inputMotif;
    @FXML private TextArea textAreaCommentaire;
    
    // --- Formulaire de modification
    @FXML private Label labelRendezVousId;
    @FXML private ComboBox<String> comboPatientModif;
    @FXML private DatePicker datePickerRendezVousModif;
    @FXML private TextField inputHeureModif;
    @FXML private TextField inputMotifModif;
    @FXML private TextArea textAreaCommentaireModif;
    
    // ============================================================
    // ============== ATTRIBUTS ET STRUCTURES DE DONNÉES ==========
    // ============================================================
    
    // --- Collections de données
    private ObservableList<RendezVous> rendezVousList = FXCollections.observableArrayList();
    private FilteredList<RendezVous> filteredRendezVous;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private RendezVous rendezVousEnModification;
    
    // --- Données de référence
    private final ObservableList<String> patientsList = FXCollections.observableArrayList();

    // ==============================================================================================================================
    // ================= MÉTHODE D'INITIALISATION (Méthode appelée automatiquement après le chargement du fichier FXML) =============
    // ==============================================================================================================================
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        chargerDonneesDepuisBD();
        setupSearch();
        
        chargerPatientsDepuisBD();
        comboPatient.setItems(patientsList);
        comboPatientModif.setItems(patientsList);
        
        btnSupprimer.setDisable(true);
        updateDeleteButtonState();
        
        tableRendezVous.setOnMouseClicked(event -> updateDeleteButtonState());
        
        datePickerRendezVous.setValue(LocalDate.now());
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
    private void handleTraitementsClick(ActionEvent event) {
        SceneManager.switchScene(btnTraitements, "/view/TraitementView.fxml", "MediConnect - Traitments", 1500, 750);
        System.out.println("Navigation vers la vue Traitements");
    }
    
    @FXML
    private void handleRendezVousClick(ActionEvent event) {
        System.out.println("Page actuelle");
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
    private void handleAideClick() {
        SceneManager.switchScene(btnAide, "/view/AideView.fxml", "MediConnect - Aide", 1500, 750);
        System.out.println("Navigation vers la vue Aide");
    }

    // ============================================================
    // ========== ACTIONS SUR LA VUE PRINCIPALE ===================
    // ============================================================
    
    @FXML
    private void handleExportPatientsCSV() {
        List<String> headers = List.of(
            "ID", "Nom du Patient", "Date", "Heure",
            "Motif", "Commentaire", "ID du Patient"
        );

        List<List<String>> rows = new ArrayList<>();

        for (RendezVous rdv : rendezVousList) {
            rows.add(List.of(
                String.valueOf(rdv.getId()),
                rdv.getNomPatient(),
                String.valueOf(rdv.getDate()),
                String.valueOf(rdv.getHeure()),
                rdv.getMotif(),
                rdv.getCommentaire(),
                String.valueOf(rdv.getPatientId())
            ));
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer les rendez-vous");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files (.csv)", ".csv"));
        File file = fileChooser.showSaveDialog(btnExporter.getScene().getWindow());

        if (file != null) {
            try {
                CSVExporter.exportToCSV(file.getAbsolutePath(), headers, rows);
                AlertMessage.showInfoAlert("Succès", "Exportation réussie", "Les rendez-vous ont été exportés avec succès.");
            } catch (Exception e) {
                AlertMessage.showErrorAlert("Erreur", "Échec de l'exportation", "Une erreur est survenue lors de l'exportation :\n" + e.getMessage());
            }
        }
    }
    
    // --- Gestion de select all
    @FXML
    void handleSelectAllCheckbox(ActionEvent event) {
        boolean selectAll = selectAllCheckbox.isSelected();
        
        for (RendezVous rdv : rendezVousList) {
            rdv.setSelected(selectAll);
        }
        
        tableRendezVous.refresh();
        
        updateDeleteButtonState();
    }

    // --- Navigation vers le formulaire de planification
    @FXML
    void handlePlanifierClick(ActionEvent event) {
        rendezVousListView.setVisible(false);
        modifierRendezVousView.setVisible(false);
        planifierRendezVousView.setVisible(true);
        
        comboPatient.setValue(null);
        datePickerRendezVous.setValue(LocalDate.now());
        inputHeure.setText("");
        inputMotif.setText("");
        textAreaCommentaire.setText("");
    }
    
    // --- GESTION DE LA SUPPRESSION (événements @FXML) ========
    @FXML
    void handleSupprimerClick(ActionEvent event) {
        long selectedCount = rendezVousList.stream().filter(RendezVous::isSelected).count();
        
        if (selectedCount == 0) {
            AlertMessage.showInfoAlert("Information", "Aucun rendez-vous sélectionné", 
                                   "Veuillez sélectionner au moins un rendez-vous à supprimer.");
            return;
        }
        
        String message = selectedCount == 1 
                       ? "Êtes-vous sûr de vouloir supprimer le rendez-vous sélectionné ?" 
                       : "Êtes-vous sûr de vouloir supprimer ces " + selectedCount + " rendez-vous ?";
        
        boolean result = AlertMessage.showConfirmationAlert("Confirmation", 
                                                                    "Suppression de rendez-vous", 
                                                                    message);
        
        if (result) {
            List<RendezVous> toRemove = new ArrayList<>();
            
            for (RendezVous rdv : rendezVousList) {
                if (rdv.isSelected()) {
                    if (supprimerRendezVousEnBD(rdv)) {
                        toRemove.add(rdv);
                    }
                }
            }
            
            rendezVousList.removeAll(toRemove);
            
            selectAllCheckbox.setSelected(false);
            
            updateDeleteButtonState();
            
            AlertMessage.showInfoAlert("Succès", "Suppression réussie", 
                                   selectedCount + " rendez-vous ont été supprimés avec succès.");
        }
    }

    // =======================================================================
    // ========== ACTIONS SUR LA VUE DU FORMULAIRE DE PLANIFICATION ==========
    // =======================================================================
    
    @FXML
    void handleConfirmerPlanificationClick(ActionEvent event) {
        if (!validerFormulairePlanification()) {
            return;
        }
        
        try {
            String nomPatient = comboPatient.getValue();
            LocalDate date = datePickerRendezVous.getValue();
            LocalTime heure = LocalTime.parse(inputHeure.getText(), timeFormatter);
            
            // S'asssurer de ne pas inserer des valeurs nulles dans la BD
            String motif = inputMotif.getText();
            if (motif == null || motif.trim().isEmpty()) {
                motif = "";
            }
            
            String commentaire = textAreaCommentaire.getText();
            if (commentaire == null || commentaire.trim().isEmpty()) {
                commentaire = "";
            }
            
            int nouveauId = rendezVousList.isEmpty() ? 1 : rendezVousList.get(rendezVousList.size() - 1).getId() + 1;
            
            RendezVous nouveauRdv = new RendezVous(nouveauId, nomPatient, date, heure, motif, commentaire);
            
            ajouterRendezVous(nouveauRdv);
            
            planifierRendezVousView.setVisible(false);
            rendezVousListView.setVisible(true);
            
        } catch (DateTimeParseException e) {
            AlertMessage.showErrorAlert("Erreur", "Format d'heure invalide", "Veuillez entrer l'heure au format HH:MM.");
        }
    }
    
    @FXML
    void handleAnnulerPlanificationClick(ActionEvent event) {
        planifierRendezVousView.setVisible(false);
        rendezVousListView.setVisible(true);
    }

    // =======================================================================
    // ========== ACTIONS SUR LA VUE DU FORMULAIRE DE MODIFICATION ===========
    // =======================================================================
    
    @FXML
    void handleConfirmerModificationClick(ActionEvent event) {
        if (!validerFormulaireModification()) {
            return;
        }
        
        try {
            String nomPatient = comboPatientModif.getValue();
            LocalDate date = datePickerRendezVousModif.getValue();
            LocalTime heure = LocalTime.parse(inputHeureModif.getText(), timeFormatter);
            
            mettreAJourRendezVous(rendezVousEnModification, nomPatient, date, heure);
            
            modifierRendezVousView.setVisible(false);
            rendezVousListView.setVisible(true);
            
        } catch (DateTimeParseException e) {
            AlertMessage.showErrorAlert("Erreur", "Format d'heure invalide", 
                              "Veuillez entrer l'heure au format HH:MM.");
        }
    }
    
    @FXML
    void handleAnnulerModificationClick(ActionEvent event) {
        modifierRendezVousView.setVisible(false);
        rendezVousListView.setVisible(true);
    }

    // ============================================================
    // =================== MÉTHODES AUXILIAIRES ===================
    // ============================================================
    
    // ---- Gestion de la recherche et filtrage ----
    private void setupSearch() {
        filteredRendezVous = new FilteredList<>(rendezVousList, p -> true);
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredRendezVous.setPredicate(createSearchPredicate(newValue));
        });
        
        tableRendezVous.setItems(filteredRendezVous);
    }
    
    private Predicate<RendezVous> createSearchPredicate(String searchText) {
        return rendezVous -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            
            String lowerCaseFilter = searchText.toLowerCase();
            
            if (rendezVous.getNomPatient().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }
            
            if (String.valueOf(rendezVous.getId()).contains(lowerCaseFilter)) {
                return true;
            }
            
            if (rendezVous.getDate().toString().contains(lowerCaseFilter) ||
                rendezVous.getHeure().toString().contains(lowerCaseFilter)) {
                return true;
            }
            
            return false;
        };
    }
    
    // ---- Configuration du tableau ----
    private void setupTableColumns() {
        colCheckbox.setCellValueFactory(cellData -> {
            RendezVous rdv = cellData.getValue();
            BooleanProperty selected = rdv.selectedProperty();
            
            selected.addListener((obs, oldVal, newVal) -> updateDeleteButtonState());
            
            return selected;
        });
        colCheckbox.setCellFactory(col -> new CheckBoxTableCell<>());
        colCheckbox.setEditable(true);
        
        tableRendezVous.setEditable(true);
        
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nomPatient"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colHeure.setCellValueFactory(new PropertyValueFactory<>("heure"));
        
        setupActionsColumn();
    }
    
    private void setupActionsColumn() {
        colActions.setCellFactory(new Callback<TableColumn<RendezVous, Void>, TableCell<RendezVous, Void>>() {
            @Override
            public TableCell<RendezVous, Void> call(final TableColumn<RendezVous, Void> param) {
                return new TableCell<RendezVous, Void>() {
                    private final Button btnView = new Button();
                    private final Button btnEdit = new Button();
                    private final Button btnDelete = new Button();
                    private final HBox actionPane = new HBox(5);

                    {
                        
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
                            RendezVous rdv = getTableView().getItems().get(getIndex());
                            afficherRendezVous(rdv);
                        });

                        btnEdit.setOnAction(event -> {
                            RendezVous rdv = getTableView().getItems().get(getIndex());
                            modifierRendezVous(rdv);
                        });

                        btnDelete.setOnAction(event -> {
                            RendezVous rdv = getTableView().getItems().get(getIndex());
                            supprimerRendezVous(rdv);
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
    
    private void afficherRendezVous(RendezVous rendezVous) {
        // Récupérer les détails complets du rendez-vous depuis la base de données
        String query = "SELECT date, heure, motif, commentaire, patient_id FROM rendez_vous WHERE id = ?";
        try (Connection connection = Database.connectDB();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, rendezVous.getId());
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String motif = rs.getString("motif");
                String commentaire = rs.getString("commentaire");

                // Créer un message pour afficher dans l'alerte
                String rendezVousDetails = String.format(
                        "Patient: %s\nDate: %s\nHeure: %s\nMotif: %s\nCommentaire: %s",
                        rendezVous.getNomPatient(),
                        rendezVous.getDate(),
                        rendezVous.getHeure(),
                        motif != null && !motif.isEmpty() ? motif : "Non spécifié",
                        commentaire != null && !commentaire.isEmpty() ? commentaire : "Aucun commentaire"
                );

                // Créer une Alert pour afficher les détails
                AlertMessage.showInfoAlert(
                        "Détails du Rendez-vous",
                        "Informations complètes du rendez-vous",
                        rendezVousDetails
                );
            }

            rs.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            AlertMessage.showErrorAlert(
                    "Erreur",
                    "Erreur lors de la récupération des détails",
                    "Impossible de récupérer les détails du rendez-vous"
            );
        }
    }
    
    // ---- Gestion des données test ----
    private void chargerDonneesDepuisBD() {
        rendezVousList.clear();
        String query = """
            SELECT rv.id, rv.date, rv.heure, rv.motif, rv.commentaire, rv.patient_id,
                   CONCAT(p.prenom, ' ', p.nom) as nom_patient
            FROM rendez_vous rv
            JOIN patient p ON rv.patient_id = p.id
            ORDER BY rv.date, rv.heure
            """;
        try (Connection connection = Database.connectDB();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nomPatient = resultSet.getString("nom_patient");

                // Correction : utiliser getString() et parser manuellement
                String dateStr = resultSet.getString("date");
                String heureStr = resultSet.getString("heure");

                LocalDate date = LocalDate.parse(dateStr);
                LocalTime heure = LocalTime.parse(heureStr);
                
                String motif = resultSet.getString("motif");
                String commentaire = resultSet.getString("commentaire");
                int patientId = resultSet.getInt("patient_id");

                RendezVous rdv = new RendezVous(id, nomPatient, date, heure, motif, commentaire, patientId);
                rendezVousList.add(rdv);
            }
            tableRendezVous.setItems(rendezVousList);
            for (RendezVous r : rendezVousList) {
                attachRendezVousListener(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertMessage.showErrorAlert("Erreur", "Erreur de base de données", 
                              "Impossible de charger les rendez-vous depuis la base de données.");
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            AlertMessage.showErrorAlert("Erreur", "Erreur de format de date", 
                              "Format de date/heure invalide dans la base de données.");
        }
    }
    
    private void chargerPatientsDepuisBD() {
        patientsList.clear();

        String query = "SELECT CONCAT(prenom, ' ', nom) as nom_complet FROM patient ORDER BY nom, prenom";

        try (Connection connection = Database.connectDB();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                patientsList.add(resultSet.getString("nom_complet"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private boolean sauvegarderRendezVousEnBD(RendezVous rdv) {
        String query = """
            INSERT INTO rendez_vous (date, heure, motif, commentaire, patient_id) 
            VALUES (?, ?, ?, ?, (SELECT id FROM patient WHERE CONCAT(prenom, ' ', nom) = ?))
            """;

        try (Connection connection = Database.connectDB();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, rdv.getDate().toString());
            statement.setString(2, rdv.getHeure().toString());
            statement.setString(3, inputMotif.getText());
            statement.setString(4, textAreaCommentaire.getText());
            statement.setString(5, rdv.getNomPatient());

            int result = statement.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            AlertMessage.showErrorAlert("Erreur", "Erreur de sauvegarde", 
                              "Impossible de sauvegarder le rendez-vous.");
            return false;
        }
    }
    
    private boolean mettreAJourRendezVousEnBD(RendezVous rdv, String nouveauNom, LocalDate nouvelleDate, LocalTime nouvelleHeure) {
        String query = """
            UPDATE rendez_vous 
            SET date = ?, heure = ?, motif = ?, commentaire = ?, 
                patient_id = (SELECT id FROM patient WHERE CONCAT(prenom, ' ', nom) = ?)
            WHERE id = ?
            """;

        try (Connection connection = Database.connectDB();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, nouvelleDate.toString());
            statement.setString(2, nouvelleHeure.toString());
            statement.setString(3, inputMotifModif.getText());
            statement.setString(4, textAreaCommentaireModif.getText());
            statement.setString(5, nouveauNom);
            statement.setInt(6, rdv.getId());

            int result = statement.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            AlertMessage.showErrorAlert("Erreur", "Erreur de mise à jour", 
                              "Impossible de mettre à jour le rendez-vous.");
            return false;
        }
    }
    
    private boolean supprimerRendezVousEnBD(RendezVous rdv) {
        String query = "DELETE FROM rendez_vous WHERE id = ?";

        try (Connection connection = Database.connectDB();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, rdv.getId());
            int result = statement.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            AlertMessage.showErrorAlert("Erreur", "Erreur de suppression", 
                              "Impossible de supprimer le rendez-vous.");
            return false;
        }
    }
    
    private void attachRendezVousListener(RendezVous rdv) {
        rdv.selectedProperty().addListener((obs, oldVal, newVal) -> {
            updateDeleteButtonState();
            
            boolean allSelected = true;
            for (RendezVous r : rendezVousList) {
                if (!r.isSelected()) {
                    allSelected = false;
                    break;
                }
            }
            
            if (selectAllCheckbox.isSelected() != allSelected) {
                selectAllCheckbox.setSelected(allSelected);
            }
        });
    }
    
    // ---- Gestion du formulaire de planification ----
    private boolean validerFormulairePlanification() {
        StringBuilder erreurs = new StringBuilder();
        
        if (comboPatient.getValue() == null || comboPatient.getValue().trim().isEmpty()) {
            erreurs.append("- Veuillez sélectionner un patient.\n");
        }
        
        if (datePickerRendezVous.getValue() == null) {
            erreurs.append("- Veuillez sélectionner une date.\n");
        }
        
        if (inputHeure.getText() == null || inputHeure.getText().trim().isEmpty()) {
            erreurs.append("- Veuillez saisir une heure.\n");
        } else {
            try {
                LocalTime.parse(inputHeure.getText(), timeFormatter);
            } catch (DateTimeParseException e) {
                erreurs.append("- Format d'heure invalide. Utilisez le format HH:MM.\n");
            }
        }
        
        if (erreurs.length() > 0) {
            AlertMessage.showErrorAlert("Erreur de validation", "Veuillez corriger les erreurs suivantes:", 
                              erreurs.toString());
            return false;
        }
        
        return true;
    }

    // ---- Gestion du formulaire de modification ----
    private boolean validerFormulaireModification() {
        StringBuilder erreurs = new StringBuilder();
        
        if (comboPatientModif.getValue() == null || comboPatientModif.getValue().trim().isEmpty()) {
            erreurs.append("- Veuillez sélectionner un patient.\n");
        }
        
        if (datePickerRendezVousModif.getValue() == null) {
            erreurs.append("- Veuillez sélectionner une date.\n");
        }
        
        if (inputHeureModif.getText() == null || inputHeureModif.getText().trim().isEmpty()) {
            erreurs.append("- Veuillez saisir une heure.\n");
        } else {
            try {
                LocalTime.parse(inputHeureModif.getText(), timeFormatter);
            } catch (DateTimeParseException e) {
                erreurs.append("- Format d'heure invalide. Utilisez le format HH:MM.\n");
            }
        }
        
        if (erreurs.length() > 0) {
            AlertMessage.showErrorAlert("Erreur de validation", "Veuillez corriger les erreurs suivantes:", 
                              erreurs.toString());
            return false;
        }
        
        return true;
    }
    
    // ---- Gestion de la suppression ----
    private void supprimerRendezVous(RendezVous rdv) {
        boolean result = AlertMessage.showConfirmationAlert("Confirmation", 
                                                                   "Suppression de rendez-vous", 
                                                                   "Êtes-vous sûr de vouloir supprimer ce rendez-vous ?");
        
        if (result) {
            // Supprimer de la base de données
            if (!supprimerRendezVousEnBD(rdv)) {
                return; // Sortir si la suppression échoue
            }
            rendezVousList.remove(rdv);
            
            AlertMessage.showInfoAlert("Succès", "Suppression réussie", 
                                   "Le rendez-vous a été supprimé avec succès.");
        }
    }

    // ---- Gestion de l'état du bouton de suppression ----
    private void updateDeleteButtonState() {
        boolean hasSelectedItems = rendezVousList.stream().anyMatch(RendezVous::isSelected);
        btnSupprimer.setDisable(!hasSelectedItems);
        
        if (hasSelectedItems) {
            btnSupprimer.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-background-radius: 8px; -fx-border-radius: 8px;");
        } else {
            btnSupprimer.setStyle("-fx-background-color: #F8F8F8; -fx-border-color: #CCCCCC; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-text-fill: #333333;");
        }
    }
    
    // ---- Manipulation des données ----
    public void ajouterRendezVous(RendezVous nouveauRdv) {
        
        // Sauvegarder en base de données
        if (!sauvegarderRendezVousEnBD(nouveauRdv)) {
            return; // Sortir si la sauvegarde échoue
        }
        
        rendezVousList.add(nouveauRdv);
        
        tableRendezVous.refresh();
        
        AlertMessage.showInfoAlert("Succès", "Ajout réussi", 
                               "Le rendez-vous a été planifié avec succès.");
    }
    
    private void modifierRendezVous(RendezVous rdv) {
        rendezVousEnModification = rdv;
        
        labelRendezVousId.setText(String.valueOf(rdv.getId()));
        comboPatientModif.setValue(rdv.getNomPatient());
        datePickerRendezVousModif.setValue(rdv.getDate());
        inputHeureModif.setText(rdv.getHeure().format(timeFormatter));
        inputMotifModif.setText(rdv.getMotif());
        textAreaCommentaireModif.setText(rdv.getCommentaire());
        
        
        rendezVousListView.setVisible(false);
        planifierRendezVousView.setVisible(false);
        modifierRendezVousView.setVisible(true);
    }
    
    public void mettreAJourRendezVous(RendezVous rdv, String nouveauNom, LocalDate nouvelleDate, LocalTime nouvelleHeure) {
        // Mettre à jour en base de données
        if (!mettreAJourRendezVousEnBD(rdv, nouveauNom, nouvelleDate, nouvelleHeure)) {
            return; // Sortir si la mise à jour échoue
        }
        
        rdv.setNomPatient(nouveauNom);
        rdv.setDate(nouvelleDate);
        rdv.setHeure(nouvelleHeure);
        
        tableRendezVous.refresh();
        
        AlertMessage.showInfoAlert("Succès", "Modification réussie", 
                               "Le rendez-vous a été modifié avec succès.");
    }
}