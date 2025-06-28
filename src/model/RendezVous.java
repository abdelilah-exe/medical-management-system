package model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Classe modèle représentant un rendez-vous médical dans l'application MediConnect
 * Stocke les informations d'un rendez-vous et l'état de sélection
 * 
 * @author pc
 */

public class RendezVous {
    
    // ============================================================
    // ================ ATTRIBUTS ET PROPRIÉTÉS ===================
    // ============================================================
    
    // --- Informations du rendez-vous
    private int id;
    private String nomPatient;  // Nom du patient associé au rendez-vous
    private LocalDate date;     // Date du rendez-vous
    private LocalTime heure;    // Heure du rendez-vous
    private String motif;
    private String commentaire;
    private int patientId;
    
    // --- Propriétés observables
    private BooleanProperty selected = new SimpleBooleanProperty(false);
    
    // ============================================================
    // ==================== CONSTRUCTEURS =========================
    // ============================================================
    
    // --- Constructeur pour AcceuilController (sans id, heure)
    public RendezVous(String nomPatient, LocalDate date, LocalTime heure) {
        this.nomPatient = nomPatient;
        this.date = date;
        this.heure = heure;
    }
    
    // --- Constructeur pour RendezVousController (sans patientId)
    public RendezVous(int id, String nomPatient, LocalDate date, LocalTime heure, String motif, String commentaire) {
        this.id = id;
        this.nomPatient = nomPatient;
        this.date = date;
        this.heure = heure;
        this.motif = motif;
        this.commentaire = commentaire;
    }
    
    // --- Constructeur pour RendezVousController
    public RendezVous(int id, String nomPatient, LocalDate date, LocalTime heure, String motif, String commentaire, int patientId) {
        this.id = id;
        this.nomPatient = nomPatient;
        this.date = date;
        this.heure = heure;
        this.motif = motif;
        this.commentaire = commentaire;
        this.patientId = patientId;
    }
    
    // ============================================================
    // ================ ACCESSEURS ET MUTATEURS ===================
    // ============================================================
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNomPatient() { return nomPatient; }
    public void setNomPatient(String nomPatient) { this.nomPatient = nomPatient; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public LocalTime getHeure() { return heure; }
    public void setHeure(LocalTime heure) { this.heure = heure; }
    
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    
    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
    
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    
    // ============================================================
    // ============= GESTION DE L'ÉTAT DE SÉLECTION ===============
    // ============================================================
    
    // Récupère l'état de sélection du rendez-vous
    public BooleanProperty selectedProperty() {
        return selected; 
    }
    // Vérifie si le rendez-vous est sélectionné
    public boolean isSelected() { 
        return selected.get(); 
    }
    // Modifie l'état de sélection du rendez-vous
    public void setSelected(boolean selected) { 
        this.selected.set(selected); 
    }
    
    // ============================================================
    // ================ MÉTHODES DE CONVERSION ====================
    // ============================================================
    
    @Override
    public String toString() {
        return "RendezVous{" + "id=" + id + ", nomPatient='" + nomPatient + '\'' + ", date=" + date + ", heure=" + heure + '}';
    }
}
