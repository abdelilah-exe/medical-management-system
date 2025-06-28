package controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import model.Utilisateur;
import utils.AlertMessage;
import utils.Database;
import utils.SceneManager;
import utils.SessionManager;


/**
 * Contrôleur pour la gestion des paramètres dans l'application MediConnect.
 * Permet la configuration des préférences utilisateur et l'accès aux fonctionnalités système.
 * @author pc
 */


public class SettingsController implements Initializable {

    // ============================================================
    // ================= COMPOSANTS INTERFACE FXML ================
    // ============================================================
    
    // --- Navigation
    @FXML private Button btnAccueil;
    @FXML private Button btnPatients;
    @FXML private Button btnTraitements;
    @FXML private Button btnRendezVous;
    @FXML private Button btnStatistiques;
    @FXML private Button btnAide;
    
    // --- Actions principales
    @FXML private Button btnDeconnexion;
    
    // --- Configuration
    @FXML private Label lblNomUtilisateur;
    @FXML private Label lblDerniereConnexion;
    @FXML private PasswordField ancienMotDePasseField;
    @FXML private PasswordField nouveauMotDePasseField;
    @FXML private PasswordField confirmerMotDePasseField;

    // ==============================================================================================================================
    // ================= MÉTHODE D’INITIALISATION (Méthode appelée automatiquement après le chargement du fichier FXML) =============
    // ==============================================================================================================================
    @Override

    public void initialize(URL location, ResourceBundle resources) {
        Utilisateur utilisateur = SessionManager.getUtilisateurActuel();
        if (utilisateur != null) {
            lblNomUtilisateur.setText(utilisateur.getNomUtilisateur());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            lblDerniereConnexion.setText(utilisateur.getDerniereConnexion().format(formatter));
        }
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
        System.out.println("Page actuelle");
    }
    
    @FXML
    private void handleAideClick(ActionEvent event) {
        SceneManager.switchScene(btnAide, "/view/AideView.fxml", "MediConnect - Aide", 1500, 750);
        System.out.println("Navigation vers la vue Aide");
    }

    // ============================================================
    // ================= ACTIONS SUR LA VUE PRINCIPALE ============
    // ============================================================
    @FXML
    private void handleChangerMotDePasse() {
        //changer mot de passe
        String ancien = ancienMotDePasseField.getText().trim();
        String nouveau = nouveauMotDePasseField.getText().trim();
        String confirmer = confirmerMotDePasseField.getText().trim();

        if (ancien.isEmpty() || nouveau.isEmpty() || confirmer.isEmpty()) {
            AlertMessage.showErrorAlert("Erreur", "Champs vides", "Veuillez remplir tous les champs.");
            return;
        }

        if (!nouveau.equals(confirmer)) {
            AlertMessage.showErrorAlert("Erreur", "Incohérence", "Les nouveaux mots de passe ne correspondent pas.");
            return;
        }

        String username = SessionManager.getUtilisateurActuel().getNomUtilisateur();

        try (Connection conn = Database.connectDB()) {
            String checkQuery = "SELECT * FROM utilisateur WHERE nom_utilisateur = ? AND mot_de_passe = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            checkStmt.setString(2, ancien);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                AlertMessage.showErrorAlert("Erreur", "Mot de passe incorrect", "L'ancien mot de passe est erroné.");
                return;
            }

            String updateQuery = "UPDATE utilisateur SET mot_de_passe = ? WHERE nom_utilisateur = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setString(1, nouveau);
            updateStmt.setString(2, username);
            int rows = updateStmt.executeUpdate();

            if (rows > 0) {
                AlertMessage.showInfoAlert("Succès", "Mot de passe changé", "Votre mot de passe a été mis à jour.");
                ancienMotDePasseField.clear();
                nouveauMotDePasseField.clear();
                confirmerMotDePasseField.clear();
            } else {
                AlertMessage.showErrorAlert("Erreur", "Échec", "La mise à jour a échoué.");
            }

        } catch (Exception e) {
            AlertMessage.showErrorAlert("Erreur", "Base de données", "Une erreur est survenue : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeconnexion() {
        boolean confirmed = AlertMessage.showConfirmationAlert("Confirmation", "Déconnexion", "Êtes-vous sûr de vouloir vous déconnecter ?");

        if (confirmed) {
            SceneManager.switchScene(btnDeconnexion, "/view/LoginView.fxml", "MediConnect - Login", 1500, 750);
            System.out.println("Navigation vers LoginView");
        }
    }
}