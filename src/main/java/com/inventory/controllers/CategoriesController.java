package com.inventory.controllers;

import com.inventory.dao.CategoryDAO;
import com.inventory.models.Category;
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

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Contrôleur pour la gestion des catégories
 */
public class CategoriesController implements Initializable {

    @FXML
    private VBox adminSection;
    @FXML
    private Label userNameLabel;
    @FXML
    private Label userRoleLabel;
    @FXML
    private MFXTextField searchField;
    @FXML
    private Label categoryCountLabel;
    @FXML
    private FlowPane categoriesContainer;

    private final CategoryDAO categoryDAO = new CategoryDAO();
    private List<Category> allCategories;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!SessionManager.isLoggedIn()) {
            SceneManager.switchTo("login");
            return;
        }

        setupUserInfo();
        setupAdminSection();
        setupSearch();
        loadCategories();
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
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterCategories(newVal);
        });
    }

    private void loadCategories() {
        new Thread(() -> {
            try {
                allCategories = categoryDAO.findAll();

                Platform.runLater(() -> {
                    categoryCountLabel.setText(allCategories.size() + " catégories");
                    displayCategories(allCategories);
                });
            } catch (Exception e) {
                System.err.println("Erreur chargement catégories: " + e.getMessage());
            }
        }).start();
    }

    private void filterCategories(String query) {
        if (query == null || query.trim().isEmpty()) {
            displayCategories(allCategories);
            return;
        }

        String searchText = query.toLowerCase().trim();
        List<Category> filtered = allCategories.stream()
                .filter(c -> c.getName().toLowerCase().contains(searchText) ||
                        (c.getDescription() != null && c.getDescription().toLowerCase().contains(searchText)))
                .collect(Collectors.toList());

        displayCategories(filtered);
    }

    private void displayCategories(List<Category> categories) {
        categoriesContainer.getChildren().clear();

        if (categories.isEmpty()) {
            VBox emptyState = createEmptyState();
            categoriesContainer.getChildren().add(emptyState);
            return;
        }

        for (Category category : categories) {
            VBox card = createCategoryCard(category);
            categoriesContainer.getChildren().add(card);
        }
    }

    private VBox createCategoryCard(Category category) {
        VBox card = new VBox(15);
        card.setPrefWidth(280);
        card.setPrefHeight(180);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.TOP_LEFT);
        card.setStyle("""
                    -fx-background-color: #1E293B;
                    -fx-background-radius: 16;
                    -fx-border-radius: 16;
                    -fx-border-color: rgba(148, 163, 184, 0.1);
                    -fx-border-width: 1;
                    -fx-cursor: hand;
                """);

        // Hover effect
        String hoverStyle = "-fx-background-color: #1E293B; " +
                "-fx-background-radius: 16; " +
                "-fx-border-radius: 16; " +
                "-fx-border-color: " + category.getColor() + "; " +
                "-fx-border-width: 2; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(99, 102, 241, 0.3), 15, 0, 0, 5);";

        String normalStyle = "-fx-background-color: #1E293B; " +
                "-fx-background-radius: 16; " +
                "-fx-border-radius: 16; " +
                "-fx-border-color: rgba(148, 163, 184, 0.1); " +
                "-fx-border-width: 1; " +
                "-fx-cursor: hand;";

        card.setOnMouseEntered(e -> card.setStyle(hoverStyle));
        card.setOnMouseExited(e -> card.setStyle(normalStyle));

        // Icon
        StackPane iconContainer = new StackPane();
        iconContainer.setPrefSize(50, 50);
        iconContainer.setMaxSize(50, 50);
        iconContainer.setStyle("-fx-background-color: " + category.getColor() + "33; -fx-background-radius: 12;");

        FontIcon icon = new FontIcon(category.getIcon() != null ? category.getIcon() : "fas-folder");
        icon.setIconSize(24);
        icon.setStyle("-fx-icon-color: " + category.getColor() + ";");
        iconContainer.getChildren().add(icon);

        // Name
        Label nameLabel = new Label(category.getName());
        nameLabel.setStyle("-fx-text-fill: #F8FAFC; -fx-font-size: 18px; -fx-font-weight: bold;");

        // Description
        Label descLabel = new Label(
                category.getDescription() != null ? category.getDescription() : "Aucune description");
        descLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 13px;");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(240);

        // Product count
        HBox statsBox = new HBox(8);
        statsBox.setAlignment(Pos.CENTER_LEFT);
        FontIcon productIcon = new FontIcon("fas-box");
        productIcon.setIconSize(14);
        productIcon.setStyle("-fx-icon-color: #94A3B8;");
        Label countLabel = new Label(category.getProductCount() + " produits");
        countLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 12px;");
        statsBox.getChildren().addAll(productIcon, countLabel);

        // Actions
        HBox actionsBox = new HBox(10);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);

        MFXButton editBtn = new MFXButton();
        FontIcon editIcon = new FontIcon("fas-edit");
        editIcon.setIconSize(14);
        editIcon.setStyle("-fx-icon-color: #6366F1;");
        editBtn.setGraphic(editIcon);
        editBtn.setStyle("-fx-background-color: transparent;");
        editBtn.setOnAction(e -> handleEditCategory(category));

        MFXButton deleteBtn = new MFXButton();
        FontIcon deleteIcon = new FontIcon("fas-trash");
        deleteIcon.setIconSize(14);
        deleteIcon.setStyle("-fx-icon-color: #EF4444;");
        deleteBtn.setGraphic(deleteIcon);
        deleteBtn.setStyle("-fx-background-color: transparent;");
        deleteBtn.setOnAction(e -> handleDeleteCategory(category));

        actionsBox.getChildren().addAll(editBtn, deleteBtn);

        // Bottom row
        HBox bottomRow = new HBox();
        bottomRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(statsBox, Priority.ALWAYS);
        bottomRow.getChildren().addAll(statsBox, actionsBox);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(iconContainer, nameLabel, descLabel, spacer, bottomRow);
        return card;
    }

    private VBox createEmptyState() {
        VBox emptyState = new VBox(15);
        emptyState.setAlignment(Pos.CENTER);
        emptyState.setPadding(new Insets(60));
        emptyState.setMinWidth(600);

        FontIcon icon = new FontIcon("fas-folder-open");
        icon.setIconSize(64);
        icon.setStyle("-fx-icon-color: #475569;");

        Label title = new Label("Aucune catégorie");
        title.setStyle("-fx-text-fill: #CBD5E1; -fx-font-size: 20px; -fx-font-weight: bold;");

        Label desc = new Label("Créez votre première catégorie pour organiser vos produits");
        desc.setStyle("-fx-text-fill: #64748B; -fx-font-size: 14px;");

        MFXButton addBtn = new MFXButton("Créer une catégorie");
        addBtn.getStyleClass().addAll("btn", "btn-primary");
        addBtn.setOnAction(e -> handleAddCategory());

        emptyState.getChildren().addAll(icon, title, desc, addBtn);
        return emptyState;
    }

    // ========== Action Handlers ==========

    @FXML
    private void handleAddCategory() {
        System.out.println("Ajouter une catégorie...");
        // TODO: Ouvrir dialog d'ajout
    }

    private void handleEditCategory(Category category) {
        System.out.println("Modifier la catégorie: " + category.getName());
        // TODO: Ouvrir dialog de modification
    }

    private void handleDeleteCategory(Category category) {
        System.out.println("Supprimer la catégorie: " + category.getName());
        // TODO: Confirmation et suppression
    }

    @FXML
    private void refreshCategories() {
        loadCategories();
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
