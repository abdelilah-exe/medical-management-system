package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.sql.*;
import java.util.*;

import javafx.scene.layout.VBox;
import utils.SceneManager;
import utils.Database;
import javafx.scene.shape.Rectangle;



/**
 * Contrôleur pour la gestion des statistiques dans l'application MediConnect.
 * Affiche les données statistiques et graphiques des patients et traitements.
 * 
 * @author pc
 */

public class StatistiquesController implements Initializable {

    // ============================================================
    // ================ COMPOSANTS INTERFACE FXML ================
    // ============================================================
    
    // --- Navigation
    @FXML private VBox chartTypeTraitements;
    @FXML private Button btnAccueil;
    @FXML private Button btnPatients;
    @FXML private Button btnTraitements;
    @FXML private Button btnRendezVous;
    @FXML private Button btnStatistiques;
    @FXML private Button btnParametres;
    @FXML private Button btnAide;
    
    // --- Affichage des données
    @FXML private Label lblTotalPatients;
    @FXML private Label lblTraitementsEnCours;
    @FXML private Label lblTraitementsTermines;
    @FXML private Label lblRdvMois;
    
    // --- Graphiques
    @FXML private StackPane barChartContainer;
    
    // ============================================================
    // ============== ATTRIBUTS ET STRUCTURES DE DONNÉES ==========
    // ============================================================
    
    private BarChart<String, Number> barChartTraitementsMois;

    // ==============================================================================================================================
    // ================= MÉTHODE D’INITIALISATION (Méthode appelée automatiquement après le chargement du fichier FXML) =============
    // ==============================================================================================================================
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Création des axes pour le graphique
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setSide(Side.BOTTOM);
        yAxis.setSide(Side.LEFT);

        // Création du graphique à barres avec les axes configurés
        barChartTraitementsMois = new BarChart<>(xAxis, yAxis);
        barChartTraitementsMois.setLegendVisible(false);
        
        // Ajout du graphique au conteneur StackPane
        barChartContainer.getChildren().clear();
        barChartContainer.getChildren().add(barChartTraitementsMois);

        
        // Chargement des données statistiques
        initStatistiques();
        
        // Configuration du graphique avec les données mensuelles
        initBarChart();
        //CSS de barchart
        barChartTraitementsMois.getStylesheets().add("data:text/css,.default-color0.chart-bar { -fx-bar-fill: black; }");
        loadTraitementsFrequents();
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
        System.out.println("Page actuelle");
    }

    @FXML
    private void handleParametresClick(ActionEvent event) {
        SceneManager.switchScene(btnParametres, "/view/SettingsView.fxml", "MediConnect - Settings", 1500, 750);
        System.out.println("Navigation vers la vue ParamÃ¨tres");
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
    void handleExporterClick(ActionEvent event) {
        System.out.println("Exportation des statistiques");
    }
    
    @FXML
    void handleOptionsClick(ActionEvent event) {
        System.out.println("Affichage des options");
    }
    
    // ============================================================
    // =================== MÉTHODES AUXILIAIRES ===================
    // ============================================================



    private void initBarChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        Map<String, Integer> data = getTraitementsParMois();

        // Ajout des données récupérées
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        // Si tu veux vraiment ajouter Janvier et Février fixes, mets-les dans getTraitementsParMois()
        // ou commente les lignes suivantes
        // series.getData().add(new XYChart.Data<>("Janvier", 30));
        // series.getData().add(new XYChart.Data<>("Février", 20));

        barChartTraitementsMois.getData().clear();  // Nettoie les anciennes données
        barChartTraitementsMois.getData().add(series);
    }

    private void initStatistiques() {
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
                    lblTraitementsEnCours.setText(String.valueOf(rs.getInt(1)));
                }
            }

            // Traitements terminés
            String sqlTermines = "SELECT COUNT(*) FROM traitement WHERE actif = false;";
            try (PreparedStatement ps = conn.prepareStatement(sqlTermines);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    lblTraitementsTermines.setText(String.valueOf(rs.getInt(1)));
                }
            }

            // Rendez-vous du mois en cours
            String sqlRdvMois = "SELECT COUNT(*) FROM rendez_vous WHERE strftime('%Y-%m', date) = strftime('%Y-%m', 'now');";
            try (PreparedStatement ps = conn.prepareStatement(sqlRdvMois);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    lblRdvMois.setText(String.valueOf(rs.getInt(1)));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //recupération des mois depuis BD
    private Map<String, Integer> getTraitementsParMois() {
        Map<String, Integer> data = new LinkedHashMap<>();

        String sql = "SELECT strftime('%m', date_debut) AS mois, COUNT(*) as total "
                + "FROM traitement "
                + "GROUP BY mois ORDER BY mois ASC";

        try (Connection conn = Database.connectDB();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String mois = getNomMois(rs.getString("mois")); // "01" → "Janvier"
                int total = rs.getInt("total");
                data.put(mois, total);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    //changer les nombres a String
    private String getNomMois(String numeroMois) {
        switch (numeroMois) {
            case "01": return "Janvier";
            case "02": return "Février";
            case "03": return "Mars";
            case "04": return "Avril";
            case "05": return "Mai";
            case "06": return "Juin";
            case "07": return "Juillet";
            case "08": return "Août";
            case "09": return "Septembre";
            case "10": return "Octobre";
            case "11": return "Novembre";
            case "12": return "Décembre";
            default: return numeroMois;
        }

    }
    //recupération des données depuis BD des Types de traitemets
    private void loadTraitementsFrequents() {
        try {
            Connection conn = Database.connectDB();

            // Obtenir les traitements les plus fréquents
            String query = "SELECT type, COUNT(*) as nombre FROM traitement GROUP BY type ORDER BY nombre DESC LIMIT 5";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            double maxWidth = 520; // largeur maximale de la barre
            int maxValue = 0;

            // Première passe : trouver la valeur max pour normaliser les barres
            List<Map.Entry<String, Integer>> traitements = new ArrayList<>();
            while (rs.next()) {
                String type = rs.getString("type");

                int nombre = rs.getInt("nombre");
                traitements.add(Map.entry(type, nombre));
                if (nombre > maxValue) maxValue = nombre;
            }

            for (Map.Entry<String, Integer> entry : traitements) {
                String type = entry.getKey();
                int nombre = entry.getValue();

                // HBox : libellé + valeur
                HBox hbox = new HBox(5);
                hbox.setAlignment(Pos.CENTER_LEFT);

                Label lblType = new Label(type);
                lblType.setStyle("-fx-font-size: 13px;");

                HBox spacer = new HBox();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Label lblValue = new Label(String.valueOf(nombre));
                lblValue.setStyle("-fx-font-size: 13px;");

                HBox rightBox = new HBox(lblValue);
                rightBox.setAlignment(Pos.CENTER_RIGHT);
                rightBox.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(rightBox, Priority.ALWAYS);

                hbox.getChildren().addAll(lblType, rightBox);

                // StackPane : barre grise + barre noire
                StackPane stack = new StackPane();
                Rectangle bgBar = new Rectangle(maxWidth, 14);
                bgBar.setArcWidth(7);
                bgBar.setArcHeight(7);
                bgBar.setStyle("-fx-fill: #eeeeee;");

                double calculatedWidth = (nombre * maxWidth) / maxValue;
                Rectangle fgBar = new Rectangle(calculatedWidth, 14);
                fgBar.setArcWidth(7);
                fgBar.setArcHeight(7);
                fgBar.setStyle("-fx-fill: #000000;");
                StackPane.setAlignment(fgBar, Pos.CENTER_LEFT);

                stack.getChildren().addAll(bgBar, fgBar);

                chartTypeTraitements.getChildren().addAll(hbox, stack);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}