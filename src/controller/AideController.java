package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import utils.AlertMessage;
import utils.SceneManager;

/**
 * Contrôleur pour la gestion de l'aide dans l'application MediConnect.
 * Fournit des liens vers les ressources d'assistance et la documentation.
 * 
 * @author pc
 */

public class AideController implements Initializable {

    // ============================================================
    // ================= COMPOSANTS INTERFACE FXML ================
    // ============================================================
    // --- Navigation
    @FXML private Button btnAccueil;
    @FXML private Button btnPatients;
    @FXML private Button btnTraitements;
    @FXML private Button btnRendezVous;
    @FXML private Button btnStatistiques;
    @FXML private Button btnParametres;
    
    // --- Actions principales
    @FXML private Hyperlink emailLink1;
    @FXML private Hyperlink emailLink2;
    @FXML private Hyperlink emailLink3;
    @FXML private Hyperlink emailLink4;

    // ==============================================================================================================================
    // ================= MÉTHODE D’INITIALISATION (Méthode appelée automatiquement après le chargement du fichier FXML) =============
    // ==============================================================================================================================
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialisation des liens
        emailLink1.setOnAction(e -> handleEmailLink(emailLink1.getText()));
        emailLink2.setOnAction(e -> handleEmailLink(emailLink2.getText()));
        emailLink3.setOnAction(e -> handleEmailLink(emailLink3.getText()));
        emailLink4.setOnAction(e -> handleEmailLink(emailLink4.getText()));
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
        SceneManager.switchScene(btnParametres, "/view/SettingsView.fxml", "MediConnect - Settings", 1500, 750);
        System.out.println("Navigation vers la vue Paramètres");
    }
    
    @FXML
    private void handleAideClick(ActionEvent event) {
        System.out.println("Page actuelle");
    }
    
    // ============================================================
    // ================= ACTIONS SUR LA VUE PRINCIPALE ============
    // ============================================================
    @FXML
    private void handleEmailLink(String email) {
        try {
            Desktop.getDesktop().mail(new URI("mailto:" + email));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            AlertMessage.showInfoAlert("Information",
                    "Problème d'ouverture du client mail",
                    "Impossible d'ouvrir votre client mail. Veuillez contacter support@nomdappli.com manuellement."
            );
        }
    }
}