package com.inventory.utils;

import com.inventory.models.User;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Gestionnaire de session utilisateur
 * Gère l'authentification et la déconnexion automatique après inactivité
 */
public class SessionManager {

    private static User currentUser = null;
    private static LocalDateTime lastActivity = null;
    private static Timer inactivityTimer = null;

    // Durée d'inactivité avant déconnexion automatique (30 minutes)
    private static final long INACTIVITY_TIMEOUT = 30 * 60 * 1000; // 30 minutes en millisecondes

    /**
     * Démarrer une session pour un utilisateur
     */
    public static void startSession(User user) {
        currentUser = user;
        lastActivity = LocalDateTime.now();
        startInactivityTimer();
        System.out.println("🔐 Session démarrée pour: " + user.getFullName());
    }

    /**
     * Terminer la session en cours
     */
    public static void endSession() {
        if (currentUser != null) {
            System.out.println("🔓 Session terminée pour: " + currentUser.getFullName());
        }
        currentUser = null;
        lastActivity = null;
        stopInactivityTimer();
    }

    /**
     * Vérifier si un utilisateur est connecté
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Obtenir l'utilisateur connecté
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Vérifier si l'utilisateur est admin
     */
    public static boolean isAdmin() {
        return currentUser != null && "ADMIN".equals(currentUser.getRole());
    }

    /**
     * Vérifier si l'utilisateur est gestionnaire
     */
    public static boolean isGestionnaire() {
        return currentUser != null && "GESTIONNAIRE".equals(currentUser.getRole());
    }

    /**
     * Mettre à jour l'activité (à appeler lors d'une action utilisateur)
     */
    public static void updateActivity() {
        lastActivity = LocalDateTime.now();
    }

    /**
     * Obtenir le temps depuis la dernière activité
     */
    public static LocalDateTime getLastActivity() {
        return lastActivity;
    }

    /**
     * Démarrer le timer d'inactivité
     */
    private static void startInactivityTimer() {
        stopInactivityTimer();
        inactivityTimer = new Timer(true);
        inactivityTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (lastActivity != null) {
                    long elapsed = System.currentTimeMillis() -
                            java.sql.Timestamp.valueOf(lastActivity).getTime();
                    if (elapsed >= INACTIVITY_TIMEOUT) {
                        System.out.println("⏱️ Déconnexion automatique pour inactivité");
                        javafx.application.Platform.runLater(() -> {
                            endSession();
                            SceneManager.switchTo("login");
                        });
                    }
                }
            }
        }, INACTIVITY_TIMEOUT, 60000); // Vérifier toutes les minutes
    }

    /**
     * Arrêter le timer d'inactivité
     */
    private static void stopInactivityTimer() {
        if (inactivityTimer != null) {
            inactivityTimer.cancel();
            inactivityTimer = null;
        }
    }
}
