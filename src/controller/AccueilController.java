package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

import model.RendezVous;
import model.Utilisateur;
import utils.AlertMessage;
import utils.Database;
import utils.SceneManager;
import utils.SessionManager;


/**
 * Contrôleur de la vue d'accueil (tableau de bord)
 * Gère l'affichage et les interactions sur la page principale de l'application
 * 
 * @author pc
 */
public class AccueilController implements Initializable {

    // ============================================================
    // ================ COMPOSANTS INTERFACE FXML =================
    // ============================================================

    // --- Navigation
    @FXML private Button btnPatients;
    @FXML private Button btnTraitements;
    @FXML private Button btnRendezVous;
    @FXML private Button btnStatistiques;
    @FXML private Button btnParametres;
    @FXML private Button btnAide;

    // --- Affichage des données
    @FXML private Label lblTotalPatients;
    @FXML private Label lblTraitementsActifs;
    @FXML private Label lblProchainsRdv;
    @FXML private Label labelBienvenue;

    // --- Tableau de rendez-vous
    @FXML private TableView<RendezVous> tableRendezVous;
    @FXML private TableColumn<RendezVous, String> colNomPatient;
    @FXML private TableColumn<RendezVous, String> colDate;
    @FXML private TableColumn<RendezVous, String> colType;

    // ============================================================
    // ============== ATTRIBUTS ET STRUCTURES DE DONNÉES ==========
    // ============================================================
    // --- List contenant les rendez-vous
    private ObservableList<RendezVous> rendezVousList = FXCollections.observableArrayList();

    // ==============================================================================================================================
    // ================= MÉTHODE D’INITIALISATION (Méthode appelée automatiquement après le chargement du fichier FXML) =============
    // ==============================================================================================================================
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // Message de Bienvenue
        Utilisateur utilisateur = SessionManager.getUtilisateurActuel();
        
        if (utilisateur != null && utilisateur.getRole().equals("Médecin")) {
            labelBienvenue.setText("Bienvenue Dr. " + utilisateur.getNomUtilisateur() + "!");
        } else if (utilisateur != null && utilisateur.getRole().equals("Secrétaire")){
            labelBienvenue.setText("Bienvenue " + utilisateur.getNomUtilisateur() + "!");
        }
        
        // Configuration des colonnes de la table des rendez-vous
        colNomPatient.setCellValueFactory(new PropertyValueFactory<>("nomPatient"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colType.setCellValueFactory(new PropertyValueFactory<>("heure"));

        // Chargement des données de test
        chargerDonneesDepuisBD();

        // Mise à jour des statistiques affichées sur le tableau de bord
        mettreAJourStatistiques();

        // Attacher les données à la table
        tableRendezVous.setItems(rendezVousList);
    }
    
    // =============================================================================
    // ============== ACTIONS SUR LE MENU LATÉRAL (Navigation entre les vues) ======
    // =============================================================================
    @FXML
    private void handleAccueilClick(ActionEvent event) {
        System.out.println("Page actuelle");
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
    // --- Exporter
    @FXML
    private void handleExporterClick(ActionEvent event) {
        System.out.println("Exportation des données");
    }

    // --- Accées rapide
    @FXML
    private void handleAjouterPatientClick(ActionEvent event) {
        SceneManager.switchScene(btnPatients, "/view/PatientView.fxml", "MediConnect - Patients", 1500, 750);
        System.out.println("Ajout d'un nouveau patient");
    }

    @FXML
    private void handleAjouterTraitementClick(ActionEvent event) {
        SceneManager.switchScene(btnTraitements, "/view/TraitementView.fxml", "MediConnect - Traitments", 1500, 750);
        System.out.println("Ajout d'un nouveau traitement");
    }

    @FXML
    private void handlePlanifierRendezVousClick(ActionEvent event) {
        SceneManager.switchScene(btnRendezVous, "/view/RendezVousView.fxml", "MediConnect - RendezVous", 1500, 750);
        System.out.println("Planification d'un nouveau rendez-vous");
    }
    // ============================================================
    // =================== MÉTHODES AUXILIAIRES ===================
    // ============================================================

    // --- Chargement des données de démonstration
    private void chargerDonneesTest() {
        rendezVousList.add(new RendezVous("Dupont Jean", LocalDate.of(2025, 06, 05), LocalTime.of(14, 30)));
        rendezVousList.add(new RendezVous("Lambert Sophie", LocalDate.of(2025, 07, 05), LocalTime.of(14, 30)));
        rendezVousList.add(new RendezVous("Dubois Marie", LocalDate.of(2025, 10, 05), LocalTime.of(14, 30)));
    }
    
    // ---- Gestion des données test ----
    private void chargerDonneesDepuisBD() {
        rendezVousList.clear();
        String query = """
            SELECT rv.date, rv.heure, CONCAT(p.prenom, ' ', p.nom) as nom_patient
            FROM rendez_vous rv
            JOIN patient p ON rv.patient_id = p.id
                       WHERE rv.date >= date('now')
            ORDER BY rv.date, rv.heure
            """;
        try (Connection connection = Database.connectDB();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String nomPatient = resultSet.getString("nom_patient");
                String dateStr = resultSet.getString("date");
                String heureStr = resultSet.getString("heure");

                LocalDate date = LocalDate.parse(dateStr);
                LocalTime heure = LocalTime.parse(heureStr);

                RendezVous rdv = new RendezVous(nomPatient, date, heure);
                rendezVousList.add(rdv);
            }
            tableRendezVous.setItems(rendezVousList);
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

    // --- Calcul des statistiques affichées
    private void mettreAJourStatistiques() {
        lblProchainsRdv.setText(String.valueOf(rendezVousList.size()));
        try (Connection conn = Database.connectDB()) {
            // Total patients
            String sqlTotalPatients = "SELECT COUNT(*) FROM Patient";
            try (PreparedStatement ps = conn.prepareStatement(sqlTotalPatients);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    lblTotalPatients.setText(String.valueOf(rs.getInt(1)));
                }
            }

            // Traitements en cours
            String sqlEnCours = "SELECT COUNT(*) FROM traitement WHERE actif = true;";
            try (PreparedStatement ps = conn.prepareStatement(sqlEnCours);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    lblTraitementsActifs.setText(String.valueOf(rs.getInt(1)));
                }
            }

            // Rendez-vous du mois en cours
            String sqlRdvMois = "SELECT COUNT(*) FROM rendez_vous WHERE date >= date('now');";
            try (PreparedStatement ps = conn.prepareStatement(sqlRdvMois);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    lblProchainsRdv.setText(String.valueOf(rs.getInt(1)));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
