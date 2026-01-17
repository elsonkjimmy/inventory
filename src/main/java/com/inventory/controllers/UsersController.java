package com.inventory.controllers;

import com.inventory.dao.UserDAO;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Contrôleur pour la gestion des utilisateurs (Admin uniquement)
 */
public class UsersController implements Initializable {

    @FXML
    private Label userNameLabel;
    @FXML
    private Label userRoleLabel;
    @FXML
    private MFXTextField searchField;

    @FXML
    private Label totalUsersLabel;
    @FXML
    private Label adminCountLabel;
    @FXML
    private Label managerCountLabel;

    @FXML
    private VBox usersTableContainer;

    private final UserDAO userDAO = new UserDAO();
    private List<User> allUsers;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Vérifier l'authentification et les droits admin
        if (!SessionManager.isLoggedIn()) {
            SceneManager.switchTo("login");
            return;
        }

        if (!SessionManager.isAdmin()) {
            SceneManager.switchTo("dashboard");
            return;
        }

        setupUserInfo();
        setupSearch();
        loadUsers();
    }

    private void setupUserInfo() {
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            userNameLabel.setText(user.getFullName());
            userRoleLabel.setText(user.getRoleDisplayName());
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterUsers(newVal);
        });
    }

    private void loadUsers() {
        new Thread(() -> {
            try {
                allUsers = userDAO.findAll();

                Platform.runLater(() -> {
                    updateStats();
                    displayUsers(allUsers);
                });
            } catch (Exception e) {
                System.err.println("Erreur chargement utilisateurs: " + e.getMessage());
            }
        }).start();
    }

    private void updateStats() {
        int total = allUsers.size();
        int admins = (int) allUsers.stream().filter(User::isAdmin).count();
        int managers = (int) allUsers.stream().filter(User::isGestionnaire).count();

        totalUsersLabel.setText(String.valueOf(total));
        adminCountLabel.setText(String.valueOf(admins));
        managerCountLabel.setText(String.valueOf(managers));
    }

    private void filterUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            displayUsers(allUsers);
            return;
        }

        String searchText = query.toLowerCase().trim();
        List<User> filtered = allUsers.stream()
                .filter(u -> u.getFullName().toLowerCase().contains(searchText) ||
                        u.getUsername().toLowerCase().contains(searchText) ||
                        (u.getEmail() != null && u.getEmail().toLowerCase().contains(searchText)))
                .collect(Collectors.toList());

        displayUsers(filtered);
    }

    private void displayUsers(List<User> users) {
        usersTableContainer.getChildren().clear();

        if (users.isEmpty()) {
            VBox emptyState = new VBox(15);
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setPadding(new Insets(60));

            FontIcon icon = new FontIcon("fas-users-slash");
            icon.setIconSize(64);
            icon.setStyle("-fx-icon-color: #475569;");

            Label label = new Label("Aucun utilisateur trouvé");
            label.setStyle("-fx-text-fill: #64748B; -fx-font-size: 16px;");

            emptyState.getChildren().addAll(icon, label);
            usersTableContainer.getChildren().add(emptyState);
            return;
        }

        // Header
        HBox header = createTableHeader();
        usersTableContainer.getChildren().add(header);

        // Rows
        for (User user : users) {
            HBox row = createUserRow(user);
            usersTableContainer.getChildren().add(row);
        }
    }

    private HBox createTableHeader() {
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: #334155; -fx-background-radius: 12 12 0 0;");

        header.getChildren().addAll(
                createHeaderLabel("UTILISATEUR", 200),
                createHeaderLabel("EMAIL", 180),
                createHeaderLabel("RÔLE", 120),
                createHeaderLabel("STATUT", 100),
                createHeaderLabel("DERNIÈRE CONNEXION", 150),
                createHeaderLabel("ACTIONS", 120));

        return header;
    }

    private Label createHeaderLabel(String text, double width) {
        Label label = new Label(text);
        label.setPrefWidth(width);
        label.setMinWidth(width);
        label.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 11px; -fx-font-weight: bold;");
        return label;
    }

    private HBox createUserRow(User user) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(15, 20, 15, 20));
        row.setStyle(
                "-fx-background-color: transparent; -fx-border-width: 0 0 1 0; -fx-border-color: rgba(148, 163, 184, 0.1);");

        row.setOnMouseEntered(e -> row.setStyle(
                "-fx-background-color: rgba(99, 102, 241, 0.08); -fx-border-width: 0 0 1 0; -fx-border-color: rgba(148, 163, 184, 0.1);"));
        row.setOnMouseExited(e -> row.setStyle(
                "-fx-background-color: transparent; -fx-border-width: 0 0 1 0; -fx-border-color: rgba(148, 163, 184, 0.1);"));

        // User info with avatar
        HBox userBox = new HBox(12);
        userBox.setPrefWidth(200);
        userBox.setAlignment(Pos.CENTER_LEFT);

        StackPane avatar = new StackPane();
        avatar.setPrefSize(40, 40);
        avatar.setMaxSize(40, 40);
        String avatarColor = user.isAdmin() ? "#6366F1" : "#10B981";
        avatar.setStyle("-fx-background-color: " + avatarColor + "33; -fx-background-radius: 20;");

        FontIcon userIcon = new FontIcon(user.isAdmin() ? "fas-user-shield" : "fas-user");
        userIcon.setIconSize(18);
        userIcon.setStyle("-fx-icon-color: " + avatarColor + ";");
        avatar.getChildren().add(userIcon);

        VBox nameBox = new VBox(2);
        Label nameLabel = new Label(user.getFullName());
        nameLabel.setStyle("-fx-text-fill: #F8FAFC; -fx-font-size: 14px; -fx-font-weight: bold;");
        Label usernameLabel = new Label("@" + user.getUsername());
        usernameLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px;");
        nameBox.getChildren().addAll(nameLabel, usernameLabel);

        userBox.getChildren().addAll(avatar, nameBox);

        // Email
        Label emailLabel = new Label(user.getEmail() != null ? user.getEmail() : "-");
        emailLabel.setPrefWidth(180);
        emailLabel.setStyle("-fx-text-fill: #CBD5E1; -fx-font-size: 13px;");

        // Role badge
        Label roleLabel = new Label(user.getRoleDisplayName());
        roleLabel.setPrefWidth(120);
        String roleStyle = user.isAdmin()
                ? "-fx-background-color: rgba(99, 102, 241, 0.2); -fx-text-fill: #A5B4FC;"
                : "-fx-background-color: rgba(16, 185, 129, 0.2); -fx-text-fill: #34D399;";
        roleLabel.setStyle(roleStyle
                + " -fx-padding: 4 10; -fx-background-radius: 20; -fx-font-size: 11px; -fx-font-weight: bold;");

        // Status
        Label statusLabel = new Label(user.isActive() ? "Actif" : "Inactif");
        statusLabel.setPrefWidth(100);
        String statusStyle = user.isActive()
                ? "-fx-background-color: rgba(16, 185, 129, 0.2); -fx-text-fill: #34D399;"
                : "-fx-background-color: rgba(239, 68, 68, 0.2); -fx-text-fill: #F87171;";
        statusLabel.setStyle(statusStyle
                + " -fx-padding: 4 10; -fx-background-radius: 20; -fx-font-size: 11px; -fx-font-weight: bold;");

        // Last login
        Label lastLoginLabel = new Label(user.getLastLogin() != null
                ? user.getLastLogin().format(dateFormatter)
                : "Jamais");
        lastLoginLabel.setPrefWidth(150);
        lastLoginLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 12px;");

        // Actions
        HBox actionsBox = new HBox(8);
        actionsBox.setPrefWidth(120);
        actionsBox.setAlignment(Pos.CENTER_LEFT);

        MFXButton editBtn = new MFXButton();
        FontIcon editIcon = new FontIcon("fas-edit");
        editIcon.setIconSize(14);
        editIcon.setStyle("-fx-icon-color: #6366F1;");
        editBtn.setGraphic(editIcon);
        editBtn.setStyle("-fx-background-color: transparent;");
        editBtn.setOnAction(e -> handleEditUser(user));

        MFXButton toggleBtn = new MFXButton();
        FontIcon toggleIcon = new FontIcon(user.isActive() ? "fas-ban" : "fas-check");
        toggleIcon.setIconSize(14);
        toggleIcon.setStyle("-fx-icon-color: " + (user.isActive() ? "#F59E0B" : "#10B981") + ";");
        toggleBtn.setGraphic(toggleIcon);
        toggleBtn.setStyle("-fx-background-color: transparent;");
        toggleBtn.setOnAction(e -> handleToggleUser(user));

        // Can't modify yourself or delete the only admin
        boolean isSelf = user.getId() == SessionManager.getCurrentUser().getId();
        if (isSelf) {
            toggleBtn.setDisable(true);
        }

        actionsBox.getChildren().addAll(editBtn, toggleBtn);

        row.getChildren().addAll(userBox, emailLabel, roleLabel, statusLabel, lastLoginLabel, actionsBox);
        return row;
    }

    // ========== Actions ==========

    @FXML
    private void handleAddUser() {
        System.out.println("Ajouter un utilisateur...");
        // TODO: Ouvrir dialog d'ajout
    }

    private void handleEditUser(User user) {
        System.out.println("Modifier l'utilisateur: " + user.getFullName());
        // TODO: Ouvrir dialog de modification
    }

    private void handleToggleUser(User user) {
        System.out.println("Toggle utilisateur: " + user.getFullName());
        // TODO: Activer/désactiver l'utilisateur
    }

    @FXML
    private void refreshUsers() {
        loadUsers();
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
    private void navigateToReports() {
        SceneManager.switchTo("reports");
    }

    @FXML
    private void handleLogout() {
        SessionManager.endSession();
        SceneManager.switchTo("login");
    }
}
