package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Classe utilitaire pour gérer la connexion à la base de données
 * Fournit un accès à la base de données SQLite de l'application MediConnect
 * @author pc
 */

public class Database {
    // ========== Méthodes de connexion ==========
    /**
     * Établit une connexion à la base de données SQLite
     * 
     * @return Objet Connection si la connexion est réussie, null sinon
     */
    public static Connection connectDB() {
        Connection conn = null;

        try {
            String url = "jdbc:sqlite:src/resources/database/MedicalTreatmentAppDB.db"; // chemin relatif vers le fichier .db
            conn = DriverManager.getConnection(url);
            
            try ( // Activer le support des clés étrangères (pour ON DELETE CASCADE)
                    Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        return conn;
    }
}