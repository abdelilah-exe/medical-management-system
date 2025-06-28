package model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Classe modèle représentant un traitement médical dans l'application MediConnect.
 * Stocke les informations d'un traitement et l'état de sélection.
 * 
 * @author pc
 */
public class Traitement {
    // ============================================================
    // ================ ATTRIBUTS ET PROPRIÉTÉS ===================
    // ============================================================
    // --- Informations sur le traitement
    private int id;
    private String nomPatient;           // Nom du patient associé au traitement
    private String type;                 // Type de traitement (ex: chimiothérapie, radiothérapie)
    private String posologie;
    private LocalDate dateDebut;         // Date de début du traitement
    private LocalDate dateFin;           // Date de fin du traitement (peut être null si en cours)
    private String statut;               // Statut du traitement (ex: En cours, Terminé)
    private String description;          // Description supplémentaire du traitement
    private int patientId;

    // --- État de sélection pour intégration avec les vues (ex: TableView)
    private final BooleanProperty selected = new SimpleBooleanProperty(false);

    // --- Formatage des dates pour affichage utilisateur
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ============================================================
    // ========================= CONSTRUCTEURS =========================
    // ============================================================
    // --- Constructeur sans description
    public Traitement(String nomPatient, String type, LocalDate dateDebut, LocalDate dateFin, String statut) {
        this.nomPatient = nomPatient;
        this.type = type;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
    }

    // --- Constructeur avec description
    public Traitement(String nomPatient, String type, LocalDate dateDebut, LocalDate dateFin, String statut, String description) {
        this(nomPatient, type, dateDebut, dateFin, statut);
        this.description = description;
    }
    
    public Traitement(int id, String nomPatient, String posologie, String type, LocalDate dateDebut, LocalDate dateFin, String statut, String description, int patientId) {
        this.id = id;
        this.nomPatient = nomPatient;
        this.posologie = posologie;
        this.type = type;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.description = description;
        this.patientId = patientId;
    }

    // ============================================================
    // ===================== ACCESSEURS / MUTATEURS =====================
    // ============================================================
    // --- Getters et Setters principaux
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNomPatient() { return nomPatient; }
    public void setNomPatient(String nomPatient) { this.nomPatient = nomPatient; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getPosologie() { return posologie; }
    public void setPosologie(String posologie) { this.posologie = posologie; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    // --- Formatage des dates pour affichage
    public String getDateDebutFormatted() {
        return dateDebut != null ? dateDebut.format(DATE_FORMATTER) : "";
    }
    public String getDateFinFormatted() {
        return dateFin != null ? dateFin.format(DATE_FORMATTER) : "-";
    }

    // ============================================================
    // ========== PROPRIÉTÉS OBSERVABLES ET BINDINGS (non @FXML) ==========
    // ============================================================
    // --- Propriété de sélection pour liaison avec des composants JavaFX
    public BooleanProperty selectedProperty() {
        return selected;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    // ============================================================
    // ================ MÉTHODES UTILITAIRES INTERNES =================
    // ============================================================
    // --- Représentation texte de l'objet (utile pour le débogage ou l'affichage)
    @Override
    public String toString() {
        return "Traitement{" +
                "nomPatient='" + nomPatient + '\'' +
                ", type='" + type + '\'' +
                ", dateDebut=" + getDateDebutFormatted() +
                ", dateFin=" + getDateFinFormatted() +
                ", statut='" + statut + '\'' +
                '}';
    }
}
