package com.inventory.controllers;

import com.inventory.dao.CategoryDAO;
import com.inventory.dao.ProductDAO;
import com.inventory.models.Category;
import com.inventory.models.Product;
import com.inventory.models.User;
import com.inventory.utils.SceneManager;
import com.inventory.utils.SessionManager;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Contrôleur pour la gestion des produits
 */
public class ProductsController implements Initializable {

    // Navigation
    @FXML
    private VBox adminSection;
    @FXML
    private Label userNameLabel;
    @FXML
    private Label userRoleLabel;

    // Filters
    @FXML
    private MFXTextField searchField;
    @FXML
    private MFXComboBox<String> categoryFilter;
    @FXML
    private MFXComboBox<String> stockFilter;

    // Stats
    @FXML
    private Label totalProductsLabel;
    @FXML
    private Label inStockLabel;
    @FXML
    private Label lowStockLabel;
    @FXML
    private Label outOfStockLabel;

    // Table
    @FXML
    private VBox productsTableContainer;

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final NumberFormat currencyFormat = NumberFormat.getInstance(Locale.FRANCE);

    private List<Product> allProducts;
    private List<Category> allCategories;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!SessionManager.isLoggedIn()) {
            SceneManager.switchTo("login");
            return;
        }

        setupUserInfo();
        setupAdminSection();
        setupFilters();
        loadData();
        setupSearch();
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

    private void setupFilters() {
        // Stock filter options
        stockFilter.getItems().addAll(
                "Tous les stocks",
                "En stock",
                "Stock faible",
                "Rupture de stock");
        stockFilter.selectFirst();

        // Filter listeners
        stockFilter.setOnAction(e -> filterProducts());
        categoryFilter.setOnAction(e -> filterProducts());
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterProducts();
        });
    }

    private void loadData() {
        new Thread(() -> {
            try {
                // Load categories
                allCategories = categoryDAO.findAll();

                // Load products
                allProducts = productDAO.findAll();

                Platform.runLater(() -> {
                    // Populate category filter
                    ObservableList<String> categoryNames = FXCollections.observableArrayList("Toutes catégories");
                    categoryNames.addAll(allCategories.stream().map(Category::getName).collect(Collectors.toList()));
                    categoryFilter.setItems(categoryNames);
                    categoryFilter.selectFirst();

                    // Update stats and table
                    updateStats();
                    displayProducts(allProducts);
                });
            } catch (Exception e) {
                System.err.println("Erreur chargement données: " + e.getMessage());
            }
        }).start();
    }

    private void updateStats() {
        int total = allProducts.size();
        int inStock = (int) allProducts.stream().filter(p -> !p.isLowStock() && !p.isOutOfStock()).count();
        int lowStock = (int) allProducts.stream().filter(p -> p.isLowStock() && !p.isOutOfStock()).count();
        int outOfStock = (int) allProducts.stream().filter(Product::isOutOfStock).count();

        totalProductsLabel.setText(String.valueOf(total));
        inStockLabel.setText(String.valueOf(inStock));
        lowStockLabel.setText(String.valueOf(lowStock));
        outOfStockLabel.setText(String.valueOf(outOfStock));
    }

    private void filterProducts() {
        String searchText = searchField.getText().toLowerCase().trim();
        String selectedCategory = categoryFilter.getValue();
        String selectedStock = stockFilter.getValue();

        List<Product> filtered = allProducts.stream()
                .filter(p -> {
                    // Search filter
                    if (!searchText.isEmpty()) {
                        boolean matches = p.getName().toLowerCase().contains(searchText) ||
                                p.getCode().toLowerCase().contains(searchText) ||
                                (p.getDescription() != null && p.getDescription().toLowerCase().contains(searchText));
                        if (!matches)
                            return false;
                    }

                    // Category filter
                    if (selectedCategory != null && !selectedCategory.equals("Toutes catégories")) {
                        if (p.getCategoryName() == null || !p.getCategoryName().equals(selectedCategory)) {
                            return false;
                        }
                    }

                    // Stock filter
                    if (selectedStock != null) {
                        switch (selectedStock) {
                            case "En stock":
                                if (p.isLowStock() || p.isOutOfStock())
                                    return false;
                                break;
                            case "Stock faible":
                                if (!p.isLowStock() || p.isOutOfStock())
                                    return false;
                                break;
                            case "Rupture de stock":
                                if (!p.isOutOfStock())
                                    return false;
                                break;
                        }
                    }

                    return true;
                })
                .collect(Collectors.toList());

        displayProducts(filtered);
    }

    private void displayProducts(List<Product> products) {
        productsTableContainer.getChildren().clear();

        if (products.isEmpty()) {
            VBox emptyState = createEmptyState();
            productsTableContainer.getChildren().add(emptyState);
            return;
        }

        // Create table header
        HBox header = createTableHeader();
        productsTableContainer.getChildren().add(header);

        // Create product rows
        for (Product product : products) {
            HBox row = createProductRow(product);
            productsTableContainer.getChildren().add(row);
        }
    }

    private HBox createTableHeader() {
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: #334155; -fx-background-radius: 12 12 0 0;");

        header.getChildren().addAll(
                createHeaderLabel("CODE", 100),
                createHeaderLabel("PRODUIT", 200),
                createHeaderLabel("CATÉGORIE", 120),
                createHeaderLabel("PRIX", 100),
                createHeaderLabel("QUANTITÉ", 100),
                createHeaderLabel("STATUT", 100),
                createHeaderLabel("ACTIONS", 150));

        return header;
    }

    private Label createHeaderLabel(String text, double width) {
        Label label = new Label(text);
        label.setPrefWidth(width);
        label.setMinWidth(width);
        label.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 11px; -fx-font-weight: bold;");
        return label;
    }

    private HBox createProductRow(Product product) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(15, 20, 15, 20));
        row.setStyle(
                "-fx-background-color: transparent; -fx-border-width: 0 0 1 0; -fx-border-color: rgba(148, 163, 184, 0.1);");

        // Hover effect
        row.setOnMouseEntered(e -> row.setStyle(
                "-fx-background-color: rgba(99, 102, 241, 0.08); -fx-border-width: 0 0 1 0; -fx-border-color: rgba(148, 163, 184, 0.1);"));
        row.setOnMouseExited(e -> row.setStyle(
                "-fx-background-color: transparent; -fx-border-width: 0 0 1 0; -fx-border-color: rgba(148, 163, 184, 0.1);"));

        // Code
        Label codeLabel = new Label(product.getCode());
        codeLabel.setPrefWidth(100);
        codeLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 13px;");

        // Product Name & Description
        VBox nameBox = new VBox(2);
        nameBox.setPrefWidth(200);
        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-text-fill: #F8FAFC; -fx-font-size: 14px; -fx-font-weight: bold;");
        Label descLabel = new Label(product.getDescription() != null
                ? (product.getDescription().length() > 30 ? product.getDescription().substring(0, 30) + "..."
                        : product.getDescription())
                : "");
        descLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px;");
        nameBox.getChildren().addAll(nameLabel, descLabel);

        // Category
        Label categoryLabel = new Label(product.getCategoryName() != null ? product.getCategoryName() : "-");
        categoryLabel.setPrefWidth(120);
        categoryLabel.setStyle("-fx-text-fill: #CBD5E1; -fx-font-size: 13px;");

        // Price
        Label priceLabel = new Label(currencyFormat.format(product.getSellingPrice()) + " FCFA");
        priceLabel.setPrefWidth(100);
        priceLabel.setStyle("-fx-text-fill: #10B981; -fx-font-size: 14px; -fx-font-weight: bold;");

        // Quantity
        Label quantityLabel = new Label(String.valueOf(product.getQuantity()));
        quantityLabel.setPrefWidth(100);
        quantityLabel.setStyle("-fx-text-fill: #F8FAFC; -fx-font-size: 14px;");

        // Status badge
        Label statusLabel = new Label(product.getStockStatus());
        statusLabel.setPrefWidth(100);
        String statusStyle = switch (product.getStockStatusClass()) {
            case "in-stock" -> "-fx-background-color: rgba(16, 185, 129, 0.2); -fx-text-fill: #34D399;";
            case "low-stock" -> "-fx-background-color: rgba(245, 158, 11, 0.2); -fx-text-fill: #FBBF24;";
            case "out-of-stock" -> "-fx-background-color: rgba(239, 68, 68, 0.2); -fx-text-fill: #F87171;";
            default -> "-fx-background-color: rgba(99, 102, 241, 0.2); -fx-text-fill: #A5B4FC;";
        };
        statusLabel.setStyle(statusStyle
                + " -fx-padding: 4 10; -fx-background-radius: 20; -fx-font-size: 11px; -fx-font-weight: bold;");

        // Actions
        HBox actionsBox = new HBox(8);
        actionsBox.setPrefWidth(150);
        actionsBox.setAlignment(Pos.CENTER_LEFT);

        MFXButton editBtn = createActionButton("fas-edit", "#6366F1", "Modifier");
        editBtn.setOnAction(e -> handleEditProduct(product));

        MFXButton deleteBtn = createActionButton("fas-trash", "#EF4444", "Supprimer");
        deleteBtn.setOnAction(e -> handleDeleteProduct(product));

        MFXButton viewBtn = createActionButton("fas-eye", "#10B981", "Voir");
        viewBtn.setOnAction(e -> handleViewProduct(product));

        actionsBox.getChildren().addAll(viewBtn, editBtn, deleteBtn);

        row.getChildren().addAll(codeLabel, nameBox, categoryLabel, priceLabel, quantityLabel, statusLabel, actionsBox);
        return row;
    }

    private MFXButton createActionButton(String iconLiteral, String color, String tooltip) {
        MFXButton btn = new MFXButton();
        FontIcon icon = new FontIcon(iconLiteral);
        icon.setIconSize(14);
        icon.setStyle("-fx-icon-color: " + color + ";");
        btn.setGraphic(icon);
        btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        return btn;
    }

    private VBox createEmptyState() {
        VBox emptyState = new VBox(15);
        emptyState.setAlignment(Pos.CENTER);
        emptyState.setPadding(new Insets(60));

        FontIcon icon = new FontIcon("fas-box-open");
        icon.setIconSize(64);
        icon.setStyle("-fx-icon-color: #475569;");

        Label title = new Label("Aucun produit trouvé");
        title.setStyle("-fx-text-fill: #CBD5E1; -fx-font-size: 20px; -fx-font-weight: bold;");

        Label desc = new Label("Ajoutez votre premier produit ou modifiez vos filtres de recherche");
        desc.setStyle("-fx-text-fill: #64748B; -fx-font-size: 14px;");

        MFXButton addBtn = new MFXButton("Ajouter un produit");
        addBtn.getStyleClass().addAll("btn", "btn-primary");
        addBtn.setOnAction(e -> handleAddProduct());

        emptyState.getChildren().addAll(icon, title, desc, addBtn);
        return emptyState;
    }

    // ========== Action Handlers ==========

    @FXML
    private void handleAddProduct() {
        // TODO: Ouvrir le dialog d'ajout de produit
        System.out.println("Ajouter un produit...");
    }

    private void handleEditProduct(Product product) {
        // TODO: Ouvrir le dialog de modification
        System.out.println("Modifier le produit: " + product.getName());
    }

    private void handleDeleteProduct(Product product) {
        // TODO: Confirmation et suppression
        System.out.println("Supprimer le produit: " + product.getName());
    }

    private void handleViewProduct(Product product) {
        // TODO: Ouvrir le détail du produit
        System.out.println("Voir le produit: " + product.getName());
    }

    @FXML
    private void refreshProducts() {
        loadData();
    }

    // ========== Navigation ==========

    @FXML
    private void navigateToDashboard() {
        SceneManager.switchTo("dashboard");
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

    @FXML
    private void handleLogout() {
        SessionManager.endSession();
        SceneManager.switchTo("login");
    }
}
