package utils;

import model.Utilisateur;

public class SessionManager {
    private static Utilisateur utilisateurActuel;

    public static void setUtilisateurActuel(Utilisateur utilisateur) {
        utilisateurActuel = utilisateur;
    }

    public static Utilisateur getUtilisateurActuel() {
        return utilisateurActuel;
    }

    public static void clearSession() {
        utilisateurActuel = null;
    }
}
