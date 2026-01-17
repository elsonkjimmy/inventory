package com.inventory.controllers;

import com.inventory.models.User;
import com.inventory.utils.SceneManager;
import com.inventory.utils.SessionManager;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Contrôleur pour le tableau de bord principal
 */
public class DashboardController implements Initializable {

    // Navigation
    @FXML
    private MFXButton navDashboard;
    @FXML
    private MFXButton navProducts;
    @FXML
    private MFXButton navCategories;
    @FXML
    private MFXButton navSales;
    @FXML
    private MFXButton navUsers;
    @FXML
    private MFXButton navReports;
    @FXML
    private VBox adminSection;

    // User info
    @FXML
    private Label userNameLabel;
    @FXML
    private Label userRoleLabel;
    @FXML
    private Label greetingLabel;
    @FXML
    private Label dateLabel;

    // Stats
    @FXML
    private Label totalProductsLabel;
    @FXML
    private Label todaySalesLabel;
    @FXML
    private Label lowStockLabel;
    @FXML
    private Label totalRevenueLabel;
    @FXML
    private Label salesChangeLabel;

    // Charts
    @FXML
    private BarChart<String, Number> salesChart;
    @FXML
    private PieChart categoriesChart;

    // Containers
    @FXML
    private VBox lowStockContainer;
    @FXML
    private VBox activityContainer;
    @FXML
    private Circle notificationBadge;

    private final NumberFormat currencyFormat = NumberFormat.getInstance(Locale.FRANCE);
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.FRENCH);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Vérifier si l'utilisateur est connecté
        if (!SessionManager.isLoggedIn()) {
            SceneManager.switchTo("login");
            return;
        }

        // Initialiser l'interface
        setupUserInfo();
        setupGreeting();
        setupAdminSection();
        loadDashboardData();
        playEntryAnimation();
    }

    private void setupUserInfo() {
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            userNameLabel.setText(user.getFullName());
            userRoleLabel.setText(user.getRoleDisplayName());
        }
    }

    private void setupGreeting() {
        User user = SessionManager.getCurrentUser();
        LocalDateTime now = LocalDateTime.now();
        String greeting;

        int hour = now.getHour();
        if (hour < 12) {
            greeting = "Bonjour";
        } else if (hour < 18) {
            greeting = "Bon après-midi";
        } else {
            greeting = "Bonsoir";
        }

        if (user != null) {
            greetingLabel.setText(greeting + ", " + user.getFullName().split(" ")[0] + " 👋");
        }

        // Formatter la date avec majuscule au début
        String formattedDate = LocalDate.now().format(dateFormatter);
        dateLabel.setText(formattedDate.substring(0, 1).toUpperCase() + formattedDate.substring(1));
    }

    private void setupAdminSection() {
        // Afficher la section admin si l'utilisateur est admin
        if (SessionManager.isAdmin()) {
            adminSection.setVisible(true);
            adminSection.setManaged(true);
        }
    }

    private void loadDashboardData() {
        // Charger les stats en arrière-plan
        new Thread(() -> {
            try {
                // TODO: Remplacer par de vraies données de la base
                int totalProducts = 156;
                double todaySales = 125000;
                int lowStock = 8;
                double totalRevenue = 2450000;

                Platform.runLater(() -> {
                    // Animer les compteurs
                    animateCounter(totalProductsLabel, 0, totalProducts, "");
                    animateCounter(todaySalesLabel, 0, (int) todaySales, " FCFA");
                    animateCounter(lowStockLabel, 0, lowStock, "");
                    animateCounter(totalRevenueLabel, 0, (int) totalRevenue, " FCFA");

                    // Charger les graphiques
                    loadSalesChart();
                    loadCategoriesChart();

                    // Afficher badge notification si stock faible
                    if (lowStock > 0) {
                        notificationBadge.setVisible(true);
                        notificationBadge.setManaged(true);
                    }
                });

            } catch (Exception e) {
                System.err.println("Erreur chargement données: " + e.getMessage());
            }
        }).start();
    }

    private void loadSalesChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ventes");

        // Données de démonstration
        series.getData().add(new XYChart.Data<>("Lun", 45000));
        series.getData().add(new XYChart.Data<>("Mar", 62000));
        series.getData().add(new XYChart.Data<>("Mer", 38000));
        series.getData().add(new XYChart.Data<>("Jeu", 71000));
        series.getData().add(new XYChart.Data<>("Ven", 55000));
        series.getData().add(new XYChart.Data<>("Sam", 89000));
        series.getData().add(new XYChart.Data<>("Dim", 42000));

        salesChart.getData().clear();
        salesChart.getData().add(series);

        // Appliquer style aux barres
        Platform.runLater(() -> {
            salesChart.lookupAll(".default-color0.chart-bar").forEach(node -> {
                node.setStyle("-fx-bar-fill: linear-gradient(to top, #6366F1, #818CF8);");
            });
        });
    }

    private void loadCategoriesChart() {
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("Électronique", 35),
                new PieChart.Data("Vêtements", 25),
                new PieChart.Data("Alimentation", 20),
                new PieChart.Data("Maison", 12),
                new PieChart.Data("Autres", 8));

        categoriesChart.setData(pieData);

        // Appliquer couleurs personnalisées
        Platform.runLater(() -> {
            String[] colors = { "#6366F1", "#10B981", "#F59E0B", "#8B5CF6", "#EC4899" };
            int i = 0;
            for (PieChart.Data data : categoriesChart.getData()) {
                data.getNode().setStyle("-fx-pie-color: " + colors[i % colors.length] + ";");
                i++;
            }
        });
    }

    private void animateCounter(Label label, int from, int to, String suffix) {
        Timeline timeline = new Timeline();
        IntegerHolder holder = new IntegerHolder(from);

        KeyFrame keyFrame = new KeyFrame(Duration.millis(20), event -> {
            int step = Math.max(1, (to - from) / 50);
            holder.value = Math.min(holder.value + step, to);
            label.setText(formatNumber(holder.value) + suffix);
        });

        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(50);
        timeline.play();

        // S'assurer que la valeur finale est correcte
        timeline.setOnFinished(e -> label.setText(formatNumber(to) + suffix));
    }

    private String formatNumber(int number) {
        return currencyFormat.format(number);
    }

    private void playEntryAnimation() {
        // Animation d'entrée pour les cards (optionnel)
    }

    // ========== Navigation Methods ==========

    @FXML
    private void navigateToDashboard() {
        setActiveNav(navDashboard);
    }

    @FXML
    private void navigateToProducts() {
        SceneManager.switchTo("products");
    }

    @FXML
    private void navigateToCategories() {
        SceneManager.switchTo("categories");
    }

    @FXML
    private void navigateToSales() {
        SceneManager.switchTo("sales");
    }

    @FXML
    private void navigateToUsers() {
        if (SessionManager.isAdmin()) {
            SceneManager.switchTo("users");
        }
    }

    @FXML
    private void navigateToReports() {
        SceneManager.switchTo("reports");
    }

    private void setActiveNav(MFXButton activeButton) {
        // Retirer la classe active de tous les boutons
        navDashboard.getStyleClass().remove("active");
        navProducts.getStyleClass().remove("active");
        navCategories.getStyleClass().remove("active");
        navSales.getStyleClass().remove("active");
        if (navUsers != null)
            navUsers.getStyleClass().remove("active");
        if (navReports != null)
            navReports.getStyleClass().remove("active");

        // Ajouter la classe active au bouton sélectionné
        if (!activeButton.getStyleClass().contains("active")) {
            activeButton.getStyleClass().add("active");
        }
    }

    // ========== Action Methods ==========

    @FXML
    private void handleLogout() {
        SessionManager.endSession();
        SceneManager.switchTo("login");
    }

    @FXML
    private void handleNewSale() {
        SceneManager.switchTo("sales");
    }

    @FXML
    private void handleAddProduct() {
        SceneManager.switchTo("products");
    }

    @FXML
    private void handleGenerateReport() {
        SceneManager.switchTo("reports");
    }

    @FXML
    private void handleViewAlerts() {
        // TODO: Afficher popup d'alertes
        System.out.println("Affichage des alertes...");
    }

    // Helper class for animation
    private static class IntegerHolder {
        int value;

        IntegerHolder(int initial) {
            this.value = initial;
        }
    }
}
