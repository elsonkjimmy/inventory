package com.inventory.controllers;

import com.inventory.dao.ProductDAO;
import com.inventory.models.Product;
import com.inventory.models.Sale;
import com.inventory.models.SaleItem;
import com.inventory.models.User;
import com.inventory.utils.SceneManager;
import com.inventory.utils.SessionManager;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import org.kordamp.ikonli.javafx.FontIcon;

import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Contrôleur pour le module de ventes (POS)
 */
public class SalesController implements Initializable {

    @FXML
    private VBox adminSection;
    @FXML
    private Label userNameLabel;
    @FXML
    private Label userRoleLabel;

    @FXML
    private MFXTextField productSearchField;
    @FXML
    private VBox productsListContainer;

    @FXML
    private VBox cartItemsContainer;
    @FXML
    private Label cartItemsCountLabel;
    @FXML
    private Label subtotalLabel;
    @FXML
    private Label discountLabel;
    @FXML
    private Label totalLabel;

    @FXML
    private MFXTextField customerNameField;
    @FXML
    private MFXTextField customerPhoneField;

    @FXML
    private MFXButton cashBtn;
    @FXML
    private MFXButton cardBtn;
    @FXML
    private MFXButton mobileBtn;

    private final ProductDAO productDAO = new ProductDAO();
    private final NumberFormat currencyFormat = NumberFormat.getInstance(Locale.FRANCE);

    private List<Product> allProducts;
    private Sale currentSale;
    private String selectedPaymentMethod = "CASH";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!SessionManager.isLoggedIn()) {
            SceneManager.switchTo("login");
            return;
        }

        currentSale = new Sale();
        currentSale.setSaleNumber(Sale.generateSaleNumber());
        currentSale.setUserId(SessionManager.getCurrentUser().getId());

        setupUserInfo();
        setupAdminSection();
        setupSearch();
        loadProducts();
        updateCartDisplay();
        selectCashPayment();
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

    private void setupSearch() {
        productSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterProducts(newVal);
        });
    }

    private void loadProducts() {
        new Thread(() -> {
            try {
                allProducts = productDAO.findAll();
                Platform.runLater(() -> displayProducts(allProducts));
            } catch (Exception e) {
                System.err.println("Erreur chargement produits: " + e.getMessage());
            }
        }).start();
    }

    private void filterProducts(String query) {
        if (query == null || query.trim().isEmpty()) {
            displayProducts(allProducts);
            return;
        }

        String searchText = query.toLowerCase().trim();
        List<Product> filtered = allProducts.stream()
                .filter(p -> p.getName().toLowerCase().contains(searchText) ||
                        p.getCode().toLowerCase().contains(searchText))
                .toList();

        displayProducts(filtered);
    }

    private void displayProducts(List<Product> products) {
        productsListContainer.getChildren().clear();

        if (products.isEmpty()) {
            Label emptyLabel = new Label("Aucun produit trouvé");
            emptyLabel.setStyle("-fx-text-fill: #64748B; -fx-padding: 30;");
            productsListContainer.getChildren().add(emptyLabel);
            return;
        }

        for (Product product : products) {
            HBox productRow = createProductRow(product);
            productsListContainer.getChildren().add(productRow);
        }
    }

    private HBox createProductRow(Product product) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 15, 12, 15));
        row.setStyle("""
                    -fx-background-color: #1E293B;
                    -fx-background-radius: 10;
                    -fx-cursor: hand;
                """);

        // Hover & click
        row.setOnMouseEntered(e -> row.setStyle("""
                    -fx-background-color: rgba(99, 102, 241, 0.15);
                    -fx-background-radius: 10;
                    -fx-cursor: hand;
                """));
        row.setOnMouseExited(e -> row.setStyle("""
                    -fx-background-color: #1E293B;
                    -fx-background-radius: 10;
                    -fx-cursor: hand;
                """));
        row.setOnMouseClicked(e -> addToCart(product));

        // Product info
        VBox infoBox = new VBox(2);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-text-fill: #F8FAFC; -fx-font-size: 14px; -fx-font-weight: bold;");

        Label codeLabel = new Label(product.getCode());
        codeLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px;");

        infoBox.getChildren().addAll(nameLabel, codeLabel);

        // Stock indicator
        Label stockLabel = new Label("Qté: " + product.getQuantity());
        String stockColor = product.isOutOfStock() ? "#EF4444" : product.isLowStock() ? "#F59E0B" : "#10B981";
        stockLabel.setStyle("-fx-text-fill: " + stockColor + "; -fx-font-size: 12px;");

        // Price
        Label priceLabel = new Label(currencyFormat.format(product.getSellingPrice()) + " F");
        priceLabel.setStyle("-fx-text-fill: #10B981; -fx-font-size: 16px; -fx-font-weight: bold;");

        // Add button
        MFXButton addBtn = new MFXButton();
        FontIcon addIcon = new FontIcon("fas-plus");
        addIcon.setIconSize(14);
        addIcon.setStyle("-fx-icon-color: #6366F1;");
        addBtn.setGraphic(addIcon);
        addBtn.setStyle("-fx-background-color: rgba(99, 102, 241, 0.2); -fx-background-radius: 8;");
        addBtn.setOnAction(e -> addToCart(product));
        addBtn.setDisable(product.isOutOfStock());

        row.getChildren().addAll(infoBox, stockLabel, priceLabel, addBtn);
        return row;
    }

    private void addToCart(Product product) {
        if (product.isOutOfStock()) {
            System.out.println("Produit en rupture de stock!");
            return;
        }

        // Check if already in cart
        SaleItem existingItem = currentSale.getItems().stream()
                .filter(item -> item.getProductId() == product.getId())
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + 1);
        } else {
            SaleItem newItem = new SaleItem(product, 1);
            currentSale.addItem(newItem);
        }

        updateCartDisplay();
    }

    private void updateCartDisplay() {
        cartItemsContainer.getChildren().clear();

        if (currentSale.getItems().isEmpty()) {
            VBox emptyCart = new VBox(10);
            emptyCart.setAlignment(Pos.CENTER);
            emptyCart.setPadding(new Insets(40));

            FontIcon icon = new FontIcon("fas-shopping-cart");
            icon.setIconSize(48);
            icon.setStyle("-fx-icon-color: #475569;");

            Label label = new Label("Panier vide");
            label.setStyle("-fx-text-fill: #64748B; -fx-font-size: 14px;");

            emptyCart.getChildren().addAll(icon, label);
            cartItemsContainer.getChildren().add(emptyCart);
        } else {
            for (SaleItem item : currentSale.getItems()) {
                HBox cartRow = createCartItemRow(item);
                cartItemsContainer.getChildren().add(cartRow);
            }
        }

        // Update totals
        int totalItems = currentSale.getTotalItems();
        cartItemsCountLabel.setText(totalItems + " article" + (totalItems > 1 ? "s" : ""));
        subtotalLabel.setText(currencyFormat.format(currentSale.getTotalAmount()) + " FCFA");
        discountLabel.setText(currencyFormat.format(currentSale.getDiscountAmount()) + " FCFA");
        totalLabel.setText(currencyFormat.format(currentSale.getFinalAmount()) + " FCFA");
    }

    private HBox createCartItemRow(SaleItem item) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 10, 12, 10));
        row.setStyle("-fx-background-color: #0F172A; -fx-background-radius: 10; -fx-border-radius: 10;");

        // Info
        VBox infoBox = new VBox(2);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        Label nameLabel = new Label(item.getProductName());
        nameLabel.setStyle("-fx-text-fill: #F8FAFC; -fx-font-size: 13px; -fx-font-weight: bold;");

        Label priceLabel = new Label(currencyFormat.format(item.getUnitPrice()) + " F x " + item.getQuantity());
        priceLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 11px;");

        infoBox.getChildren().addAll(nameLabel, priceLabel);

        // Quantity controls
        HBox qtyControls = new HBox(5);
        qtyControls.setAlignment(Pos.CENTER);

        MFXButton minusBtn = new MFXButton("-");
        minusBtn.setStyle(
                "-fx-background-color: #334155; -fx-text-fill: #F8FAFC; -fx-min-width: 28; -fx-min-height: 28;");
        minusBtn.setOnAction(e -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                currentSale.recalculateTotal();
                updateCartDisplay();
            } else {
                currentSale.removeItem(item);
                updateCartDisplay();
            }
        });

        Label qtyLabel = new Label(String.valueOf(item.getQuantity()));
        qtyLabel.setStyle("-fx-text-fill: #F8FAFC; -fx-font-size: 14px; -fx-min-width: 30; -fx-alignment: center;");

        MFXButton plusBtn = new MFXButton("+");
        plusBtn.setStyle(
                "-fx-background-color: #334155; -fx-text-fill: #F8FAFC; -fx-min-width: 28; -fx-min-height: 28;");
        plusBtn.setOnAction(e -> {
            item.setQuantity(item.getQuantity() + 1);
            currentSale.recalculateTotal();
            updateCartDisplay();
        });

        qtyControls.getChildren().addAll(minusBtn, qtyLabel, plusBtn);

        // Subtotal
        Label subtotalLabel = new Label(currencyFormat.format(item.getSubtotal()) + " F");
        subtotalLabel.setStyle(
                "-fx-text-fill: #10B981; -fx-font-size: 14px; -fx-font-weight: bold; -fx-min-width: 80; -fx-alignment: center-right;");

        row.getChildren().addAll(infoBox, qtyControls, subtotalLabel);
        return row;
    }

    // ========== Payment Methods ==========

    @FXML
    private void selectCashPayment() {
        selectedPaymentMethod = "CASH";
        updatePaymentButtonStyles();
    }

    @FXML
    private void selectCardPayment() {
        selectedPaymentMethod = "CARD";
        updatePaymentButtonStyles();
    }

    @FXML
    private void selectMobilePayment() {
        selectedPaymentMethod = "MOBILE";
        updatePaymentButtonStyles();
    }

    private void updatePaymentButtonStyles() {
        String activeStyle = "-fx-background-color: #6366F1; -fx-text-fill: white;";
        String inactiveStyle = "-fx-background-color: #334155; -fx-text-fill: #CBD5E1;";

        cashBtn.setStyle(selectedPaymentMethod.equals("CASH") ? activeStyle : inactiveStyle);
        cardBtn.setStyle(selectedPaymentMethod.equals("CARD") ? activeStyle : inactiveStyle);
        mobileBtn.setStyle(selectedPaymentMethod.equals("MOBILE") ? activeStyle : inactiveStyle);
    }

    // ========== Actions ==========

    @FXML
    private void clearCart() {
        currentSale = new Sale();
        currentSale.setSaleNumber(Sale.generateSaleNumber());
        currentSale.setUserId(SessionManager.getCurrentUser().getId());
        updateCartDisplay();
    }

    @FXML
    private void handleValidateSale() {
        if (currentSale.getItems().isEmpty()) {
            System.out.println("Le panier est vide!");
            return;
        }

        currentSale.setPaymentMethod(selectedPaymentMethod);
        currentSale.setCustomerName(customerNameField.getText());
        currentSale.setCustomerPhone(customerPhoneField.getText());

        // TODO: Sauvegarder la vente en base de données
        // TODO: Mettre à jour le stock
        // TODO: Générer le reçu

        System.out.println("=== VENTE VALIDÉE ===");
        System.out.println("Numéro: " + currentSale.getSaleNumber());
        System.out.println("Total: " + currencyFormat.format(currentSale.getFinalAmount()) + " FCFA");
        System.out.println("Paiement: " + currentSale.getPaymentMethodDisplay());
        System.out.println("Articles: " + currentSale.getTotalItems());

        // Reset for next sale
        clearCart();
        customerNameField.clear();
        customerPhoneField.clear();
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
