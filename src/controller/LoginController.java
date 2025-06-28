package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import model.Utilisateur;
import utils.AlertMessage;
import utils.Database;
import utils.SceneManager;
import utils.SessionManager;

/**
 * Contrôleur pour la gestion de l'authentification dans l'application MediConnect.
 * Permet aux utilisateurs de se connecter et d'accéder aux fonctionnalités de l'application.
 * 
 * @author pc
 */
public class LoginController {

    // ============================================================
    // ================ COMPOSANTS INTERFACE FXML =================
    // ============================================================

    // --- Champs de saisie
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox ShowPasswordCheckbox;

    @FXML
    private TextField visiblePasswordField;



    // --- Actions principales
    @FXML private Button loginButton;
    @FXML private Hyperlink registerLink;

    // ============================================================
    // ============== ATTRIBUTS ET STRUCTURES DE DONNÉES ==========
    // ============================================================

    // --- Base de données
    
    private PreparedStatement prepare;
    private ResultSet result;

    // ==============================================================================================================================
    // ================= MÉTHODE D’INITIALISATION (Méthode appelée automatiquement après le chargement du fichier FXML) =============
    // ==============================================================================================================================
    @FXML
    private void initialize() {
        // Initialisation des composants si nécessaire
    }

    // ============================================================
    // ========== ACTIONS SUR LA VUE PRINCIPALE ===================
    // ============================================================

    // --- Connexion de l'utilisateur
    @FXML
    private void handleLoginClick(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (!isValidLogin(username, password)) {
            AlertMessage.showErrorAlert("Erreur", "Champs vides", "Veuillez remplir tous les champs.");
            return;
        }

        // Charger les données de l'utilisateur
        String loginSQL = "SELECT * FROM utilisateur WHERE nom_utilisateur = ? AND mot_de_passe = ?";
        try (Connection connect = Database.connectDB();) {
            prepare = connect.prepareStatement(loginSQL);
            prepare.setString(1, username);
            prepare.setString(2, password);
            result = prepare.executeQuery();
            if (result.next()) {
                AlertMessage.showInfoAlert("Succès", "Connexion réussie", "Vous êtes connecté avec succès !");
                Utilisateur utilisateurConnecte = new Utilisateur();
                utilisateurConnecte.setNomUtilisateur(result.getString("nom_utilisateur"));
                utilisateurConnecte.setDerniereConnexion(java.time.LocalDateTime.now());
                utilisateurConnecte.setRole(result.getString("role"));

                SessionManager.setUtilisateurActuel(utilisateurConnecte);


                SceneManager.switchScene(loginButton, "/view/AcceuilView.fxml", "MediConnect - Acceuil", 1500, 750);
                System.out.println("Navigation vers la vue d'acceuil");
            } else {
                AlertMessage.showErrorAlert("Erreur", "Échec de la connexion", "Nom d'utilisateur ou mot de passe invalide.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertMessage.showErrorAlert("Erreur", "Erreur de la base de données", "Erreur de la base de données : " + e.getMessage());
        }
    }
    
    // --- Redirection vers l'inscription
    @FXML
    private void handleRegisterClick(ActionEvent event) throws IOException {
        SceneManager.switchScene(registerLink, "/view/RegisterView.fxml", "MediConnect - Inscription", 1500, 750);
    }

    // ============================================================
    // =================== MÉTHODES AUXILIAIRES ===================
    // ============================================================

    // --- Validation des données
    private boolean isValidLogin(String username, String password) {
        return username != null && !username.isEmpty() && password != null && !password.isEmpty();
    }
    @FXML
    private void togglePasswordVisibility() {
        if (ShowPasswordCheckbox.isSelected()) {
            visiblePasswordField.setText(passwordField.getText());
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
        } else {
            passwordField.setText(visiblePasswordField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
        }
    }

}
