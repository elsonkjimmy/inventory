package com.inventory.controllers;

import com.inventory.dao.ProductDAO;
import com.inventory.models.Product;
import com.inventory.models.User;
import com.inventory.utils.SceneManager;
import com.inventory.utils.SessionManager;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Contrôleur pour les rapports et statistiques
 */
public class ReportsController implements Initializable {

    @FXML
    private VBox adminSection;
    @FXML
    private Label userNameLabel;
    @FXML
    private Label userRoleLabel;
    @FXML
    private MFXComboBox<String> periodSelector;

    // Stats
    @FXML
    private Label totalRevenueLabel;
    @FXML
    private Label totalSalesLabel;
    @FXML
    private Label totalProductsSoldLabel;
    @FXML
    private Label avgSaleLabel;

    // Charts
    @FXML
    private LineChart<String, Number> salesTrendChart;
    @FXML
    private PieChart categoryPieChart;
    @FXML
    private BarChart<String, Number> paymentMethodChart;

    // Containers
    @FXML
    private VBox topProductsContainer;
    @FXML
    private VBox lowStockReportContainer;

    private final ProductDAO productDAO = new ProductDAO();
    private final NumberFormat currencyFormat = NumberFormat.getInstance(Locale.FRANCE);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!SessionManager.isLoggedIn()) {
            SceneManager.switchTo("login");
            return;
        }

        setupUserInfo();
        setupAdminSection();
        setupPeriodSelector();
        loadReportData();
    }

    private void setupUserInfo() {
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            userNameLabel.setText(user.getFullName());
            userRoleLabel.setText(user.getRoleDisplayName());
        }
    }

    private void setupAdminSection() {
        if (SessionManager.isAdmin()) {
            adminSection.setVisible(true);
            adminSection.setManaged(true);
        }
    }

    private void setupPeriodSelector() {
        periodSelector.setItems(FXCollections.observableArrayList(
                "Aujourd'hui",
                "Cette semaine",
                "Ce mois",
                "Ce trimestre",
                "Cette année",
                "Personnalisé"));
        periodSelector.selectItem("Ce mois");
        periodSelector.setOnAction(e -> loadReportData());
    }

    private void loadReportData() {
        new Thread(() -> {
            try {
                // Données de démonstration
                // TODO: Remplacer par de vraies données de la base

                Platform.runLater(() -> {
                    // Stats
                    totalRevenueLabel.setText(currencyFormat.format(2450000) + " FCFA");
                    totalSalesLabel.setText("156");
                    totalProductsSoldLabel.setText("892");
                    avgSaleLabel.setText(currencyFormat.format(15705) + " FCFA");

                    // Charts
                    loadSalesTrendChart();
                    loadCategoryPieChart();
                    loadPaymentMethodChart();
                    loadTopProducts();
                    loadLowStockReport();
                });

            } catch (Exception e) {
                System.err.println("Erreur chargement rapports: " + e.getMessage());
            }
        }).start();
    }

    private void loadSalesTrendChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ventes");

        series.getData().add(new XYChart.Data<>("Sem 1", 450000));
        series.getData().add(new XYChart.Data<>("Sem 2", 620000));
        series.getData().add(new XYChart.Data<>("Sem 3", 380000));
        series.getData().add(new XYChart.Data<>("Sem 4", 710000));

        salesTrendChart.getData().clear();
        salesTrendChart.getData().add(series);
    }

    private void loadCategoryPieChart() {
        categoryPieChart.setData(FXCollections.observableArrayList(
                new PieChart.Data("Électronique", 35),
                new PieChart.Data("Vêtements", 25),
                new PieChart.Data("Alimentation", 20),
                new PieChart.Data("Maison", 12),
                new PieChart.Data("Autres", 8)));

        // Couleurs personnalisées
        String[] colors = { "#6366F1", "#10B981", "#F59E0B", "#8B5CF6", "#EC4899" };
        int i = 0;
        for (PieChart.Data data : categoryPieChart.getData()) {
            final int colorIndex = i;
            Platform.runLater(() -> {
                if (data.getNode() != null) {
                    data.getNode().setStyle("-fx-pie-color: " + colors[colorIndex % colors.length] + ";");
                }
            });
            i++;
        }
    }

    private void loadPaymentMethodChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Montant");

        series.getData().add(new XYChart.Data<>("Espèces", 1200000));
        series.getData().add(new XYChart.Data<>("Carte", 650000));
        series.getData().add(new XYChart.Data<>("Mobile", 600000));

        paymentMethodChart.getData().clear();
        paymentMethodChart.getData().add(series);
    }

    private void loadTopProducts() {
        topProductsContainer.getChildren().clear();

        // Top 5 produits (données de démo)
        String[][] topProducts = {
                { "iPhone 15 Pro", "45", "4,500,000" },
                { "MacBook Air M3", "23", "2,300,000" },
                { "AirPods Pro", "67", "1,675,000" },
                { "Samsung Galaxy S24", "31", "1,240,000" },
                { "iPad Pro 12.9", "18", "1,080,000" }
        };

        int rank = 1;
        for (String[] product : topProducts) {
            HBox row = createTopProductRow(rank++, product[0], Integer.parseInt(product[1]),
                    Double.parseDouble(product[2].replace(",", "")));
            topProductsContainer.getChildren().add(row);
        }
    }

    private HBox createTopProductRow(int rank, String name, int quantity, double revenue) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10));
        row.setStyle("-fx-background-color: rgba(51, 65, 85, 0.5); -fx-background-radius: 10;");

        // Rank
        Label rankLabel = new Label("#" + rank);
        rankLabel.setMinWidth(30);
        String rankColor = rank == 1 ? "#FFD700" : rank == 2 ? "#C0C0C0" : rank == 3 ? "#CD7F32" : "#94A3B8";
        rankLabel.setStyle("-fx-text-fill: " + rankColor + "; -fx-font-size: 16px; -fx-font-weight: bold;");

        // Product info
        VBox infoBox = new VBox(2);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-text-fill: #F8FAFC; -fx-font-size: 14px; -fx-font-weight: bold;");
        Label qtyLabel = new Label(quantity + " vendus");
        qtyLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px;");
        infoBox.getChildren().addAll(nameLabel, qtyLabel);

        // Revenue
        Label revenueLabel = new Label(currencyFormat.format(revenue) + " F");
        revenueLabel.setStyle("-fx-text-fill: #10B981; -fx-font-size: 14px; -fx-font-weight: bold;");

        row.getChildren().addAll(rankLabel, infoBox, revenueLabel);
        return row;
    }

    private void loadLowStockReport() {
        lowStockReportContainer.getChildren().clear();

        new Thread(() -> {
            List<Product> lowStockProducts = productDAO.findLowStock();

            Platform.runLater(() -> {
                if (lowStockProducts.isEmpty()) {
                    Label emptyLabel = new Label("🎉 Aucun produit en stock faible!");
                    emptyLabel.setStyle("-fx-text-fill: #10B981; -fx-font-size: 14px; -fx-padding: 20;");
                    lowStockReportContainer.getChildren().add(emptyLabel);
                } else {
                    for (Product product : lowStockProducts) {
                        HBox row = createLowStockRow(product);
                        lowStockReportContainer.getChildren().add(row);
                    }
                }
            });
        }).start();
    }

    private HBox createLowStockRow(Product product) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 15, 12, 15));
        row.setStyle(
                "-fx-background-color: rgba(239, 68, 68, 0.1); -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: rgba(239, 68, 68, 0.3); -fx-border-width: 1;");

        // Warning icon
        FontIcon icon = new FontIcon(product.isOutOfStock() ? "fas-times-circle" : "fas-exclamation-triangle");
        icon.setIconSize(20);
        icon.setStyle("-fx-icon-color: " + (product.isOutOfStock() ? "#EF4444" : "#F59E0B") + ";");

        // Product info
        VBox infoBox = new VBox(2);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-text-fill: #F8FAFC; -fx-font-size: 14px; -fx-font-weight: bold;");
        Label codeLabel = new Label(product.getCode());
        codeLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px;");
        infoBox.getChildren().addAll(nameLabel, codeLabel);

        // Stock info
        VBox stockBox = new VBox(4);
        stockBox.setMinWidth(120);
        Label stockLabel = new Label(product.getQuantity() + " / " + product.getAlertThreshold());
        stockLabel.setStyle("-fx-text-fill: #CBD5E1; -fx-font-size: 13px;");

        ProgressBar progressBar = new ProgressBar();
        progressBar.setProgress((double) product.getQuantity() / product.getAlertThreshold());
        progressBar.setPrefWidth(100);
        progressBar.setStyle("-fx-accent: " + (product.isOutOfStock() ? "#EF4444" : "#F59E0B") + ";");

        stockBox.getChildren().addAll(stockLabel, progressBar);

        // Status badge
        Label statusLabel = new Label(product.isOutOfStock() ? "RUPTURE" : "FAIBLE");
        String statusStyle = product.isOutOfStock()
                ? "-fx-background-color: rgba(239, 68, 68, 0.3); -fx-text-fill: #F87171;"
                : "-fx-background-color: rgba(245, 158, 11, 0.3); -fx-text-fill: #FBBF24;";
        statusLabel.setStyle(statusStyle
                + " -fx-padding: 4 10; -fx-background-radius: 20; -fx-font-size: 11px; -fx-font-weight: bold;");

        row.getChildren().addAll(icon, infoBox, stockBox, statusLabel);
        return row;
    }

    // ========== Actions ==========

    @FXML
    private void handleExportPDF() {
        System.out.println("Export PDF...");
        // TODO: Générer PDF avec iText
    }

    @FXML
    private void handleExportLowStock() {
        System.out.println("Export rapport stock faible...");
        // TODO: Export CSV/Excel
    }

    // ========== Navigation ==========

    @FXML
    private void navigateToDashboard() {
        SceneManager.switchTo("dashboard");
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
    private void handleLogout() {
        SessionManager.endSession();
        SceneManager.switchTo("login");
    }
}
