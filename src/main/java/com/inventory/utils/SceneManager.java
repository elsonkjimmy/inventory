package com.inventory.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire de scènes pour naviguer entre les différentes vues
 */
public class SceneManager {

    private static Stage primaryStage;
    private static Scene mainScene;
    private static final Map<String, String> scenes = new HashMap<>();
    private static Object currentController;

    static {
        // Enregistrer toutes les scènes disponibles
        scenes.put("login", "/fxml/LoginView.fxml");
        scenes.put("dashboard", "/fxml/DashboardView.fxml");
        scenes.put("products", "/fxml/ProductsView.fxml");
        scenes.put("categories", "/fxml/CategoriesView.fxml");
        scenes.put("sales", "/fxml/SalesView.fxml");
        scenes.put("users", "/fxml/UsersView.fxml");
        scenes.put("reports", "/fxml/ReportsView.fxml");
        scenes.put("settings", "/fxml/SettingsView.fxml");
    }

    /**
     * Initialiser le gestionnaire avec le stage principal
     */
    public static void initialize(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Changer de scène
     */
    public static void switchTo(String sceneName) {
        String fxmlPath = scenes.get(sceneName);
        if (fxmlPath == null) {
            System.err.println("❌ Scène non trouvée: " + sceneName);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();
            currentController = loader.getController();

            if (mainScene == null) {
                mainScene = new Scene(root, 1400, 850);
                loadStylesheets(mainScene);
                primaryStage.setScene(mainScene);
            } else {
                mainScene.setRoot(root);
            }

            System.out.println("📄 Scène chargée: " + sceneName);

        } catch (IOException e) {
            System.err.println("❌ Erreur de chargement de la scène '" + sceneName + "': " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Charger les feuilles de style
     */
    private static void loadStylesheets(Scene scene) {
        try {
            // Style principal
            String mainCss = SceneManager.class.getResource("/css/style.css").toExternalForm();
            scene.getStylesheets().add(mainCss);

            // Style des composants
            String componentsCss = SceneManager.class.getResource("/css/components.css").toExternalForm();
            scene.getStylesheets().add(componentsCss);

            System.out.println("🎨 Styles CSS chargés avec succès!");
        } catch (Exception e) {
            System.err.println("⚠️ Certains fichiers CSS n'ont pas pu être chargés: " + e.getMessage());
        }
    }

    /**
     * Obtenir le contrôleur actuel
     */
    @SuppressWarnings("unchecked")
    public static <T> T getCurrentController() {
        return (T) currentController;
    }

    /**
     * Obtenir le stage principal
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Obtenir la scène principale
     */
    public static Scene getMainScene() {
        return mainScene;
    }
}
