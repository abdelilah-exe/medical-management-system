package utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Classe utilitaire pour afficher des boîtes de dialogue et des alertes
 * Fournit des méthodes statiques pour différents types d'alertes dans l'application MediConnect
 * @author pc
 */

public class AlertMessage {
    
    // ========== Alertes d'information ==========
    /**
     * Affiche une alerte d'information
     * 
     * @param titre Titre de l'alerte
     * @param header En-tête de l'alerte
     * @param message Message de l'alerte
     */
    public static void showInfoAlert(String titre, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // ========== Alertes d'erreur ==========
    /**
     * Affiche une alerte d'erreur
     * 
     * @param titre Titre de l'alerte
     * @param header En-tête de l'alerte
     * @param message Message de l'alerte
     */
    public static void showErrorAlert(String titre, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // ========== Alertes d'avertissement ==========
    /**
     * Affiche une alerte d'avertissement
     * 
     * @param titre Titre de l'alerte
     * @param header En-tête de l'alerte
     * @param message Message de l'alerte
     */
    public static void showWarningAlert(String titre, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // ========== Alertes de confirmation ==========
    /**
     * Affiche une boîte de dialogue de confirmation
     * 
     * @param titre Titre de la boîte de dialogue
     * @param header En-tête de la boîte de dialogue
     * @param message Message de la boîte de dialogue
     * @return true si l'utilisateur clique sur OK, false sinon
     */
    public static boolean showConfirmationAlert(String titre, String header, String message){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titre);
        alert.setHeaderText(header);
        alert.setContentText(message);
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get().equals(ButtonType.OK)) {
            return true;
        } else {
            return false;
        }
    }
}