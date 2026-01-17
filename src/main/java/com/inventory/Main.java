package com.inventory;

import com.inventory.utils.DatabaseConnection;
import com.inventory.utils.SceneManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Point d'entrée principal de l'application Inventory Management System
 * 
 * @author Inventory Team
 * @version 1.0.0
 */
public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        // Test de connexion à la base de données
        if (!DatabaseConnection.testConnection()) {
            System.err.println("⚠️ Impossible de se connecter à la base de données!");
            System.err.println("Vérifiez que MySQL est en cours d'exécution et que les paramètres sont corrects.");
        } else {
            System.out.println("✅ Connexion à la base de données réussie!");
        }

        // Configuration de la fenêtre principale
        stage.setTitle("📦 Inventory Management System");
        stage.setMinWidth(1200);
        stage.setMinHeight(700);

        // Charger l'icône de l'application si disponible
        try {
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png")));
        } catch (Exception e) {
            System.out.println("ℹ️ Icône de l'application non trouvée, utilisation de l'icône par défaut.");
        }

        // Initialiser le gestionnaire de scènes
        SceneManager.initialize(stage);

        // Charger la page de connexion
        SceneManager.switchTo("login");

        stage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void stop() {
        // Fermer la connexion à la base de données
        DatabaseConnection.closeConnection();
        System.out.println("👋 Application fermée. Au revoir!");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
