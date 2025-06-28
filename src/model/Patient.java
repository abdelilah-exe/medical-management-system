package model;
import java.time.LocalDate;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Classe modèle représentant un patient dans l'application MediConnect
 * Stocke les informations personnelles et l'état de sélection d'un patient
 *
 * @author pc
 */

public class Patient {

    // ============================================================
    // ================ ATTRIBUTS ET PROPRIÉTÉS ===================
    // ============================================================

    // --- Informations personnelles du patient
    private int id;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String sexe;
    private String numTelephone;
    private String numSecuriteSociale;
    private boolean sousSurveillance;
    private String dateCreation;

    // --- Propriétés observables
    private final BooleanProperty selected = new SimpleBooleanProperty(false);

    // ============================================================
    // ====================== CONSTRUCTEURS ========================
    // ============================================================

    public Patient(int id, String nom, String prenom, LocalDate dateNaissance, String sexe, String numTelephone, String numSecuriteSociale, String dateCreation) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.sexe = sexe;
        this.numTelephone = numTelephone;
        this.numSecuriteSociale = numSecuriteSociale;
        this.dateCreation = dateCreation;
    }

    public Patient(String nom, String prenom, LocalDate dateNaissance, String sexe, String numTelephone, String numSecuriteSociale) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.sexe = sexe;
        this.numTelephone = numTelephone;
        this.numSecuriteSociale = numSecuriteSociale;
    }

    // ============================================================
    // ================ GETTERS ET SETTERS ===================
    // ============================================================

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public String getSexe() { return sexe; }
    public void setSexe(String sexe) { this.sexe = sexe; }

    public String getNumTelephone() { return numTelephone; }
    public void setNumTelephone(String numTelephone) { this.numTelephone = numTelephone; }

    public String getNumSecuriteSociale() { return numSecuriteSociale; }
    public void setNumSecuriteSociale(String numSecuriteSociale) { this.numSecuriteSociale = numSecuriteSociale; }

    public boolean getSousSurveillance() { return sousSurveillance; }
    public void setSousSurveillance(boolean sousSurveillance) { this.sousSurveillance = sousSurveillance; }

    public String getDateCreation() { return dateCreation; }
    public void setDateCreation(String dateCreation) { this.dateCreation = dateCreation; }

    // ============================================================
    // ============= GESTION DE L'ÉTAT DE SÉLECTION ===============
    // ============================================================

    // Récupère l'état de sélection du patient
    public BooleanProperty selectedProperty() {
        return selected;
    }
    // Vérifie si le patient est sélectionné
    public boolean isSelected() {
        return selected.get();
    }
    // Modifie l'état de sélection du patient
    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }
}