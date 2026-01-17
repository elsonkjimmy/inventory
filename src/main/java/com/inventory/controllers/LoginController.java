package com.inventory.controllers;

import com.inventory.dao.UserDAO;
import com.inventory.models.User;
import com.inventory.utils.DatabaseConnection;
import com.inventory.utils.SceneManager;
import com.inventory.utils.SessionManager;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Contrôleur pour la page de connexion
 */
public class LoginController implements Initializable {

    @FXML
    private StackPane rootPane;
    @FXML
    private VBox loginCard;
    @FXML
    private MFXTextField usernameField;
    @FXML
    private MFXPasswordField passwordField;
    @FXML
    private MFXButton loginButton;
    @FXML
    private Label errorLabel;
    @FXML
    private Label statusLabel;

    private final UserDAO userDAO = new UserDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Animation d'entrée
        playEntryAnimation();

        // Initialiser la base de données
        initializeDatabase();

        // Configuration des champs
        setupFields();

        // Effacer le message d'erreur quand l'utilisateur tape
        usernameField.textProperty().addListener((obs, old, newVal) -> clearError());
        passwordField.textProperty().addListener((obs, old, newVal) -> clearError());
    }

    private void initializeDatabase() {
        statusLabel.setText("Connexion à la base de données...");

        new Thread(() -> {
            // Initialiser les tables
            DatabaseConnection.initializeDatabase();
            // Créer l'admin par défaut si nécessaire
            DatabaseConnection.insertDefaultAdmin();

            Platform.runLater(() -> {
                if (DatabaseConnection.testConnection()) {
                    statusLabel.setText("✅ Prêt à se connecter");
                    statusLabel.setStyle("-fx-text-fill: #10B981;");
                } else {
                    statusLabel.setText("❌ Base de données non disponible");
                    statusLabel.setStyle("-fx-text-fill: #EF4444;");
                }
            });
        }).start();
    }

    private void setupFields() {
        // Action sur Enter dans le champ mot de passe
        passwordField.setOnAction(event -> handleLogin());

        // Focus initial sur le champ username
        Platform.runLater(() -> usernameField.requestFocus());
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Validation
        if (username.isEmpty()) {
            showError("Veuillez entrer votre nom d'utilisateur");
            usernameField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showError("Veuillez entrer votre mot de passe");
            passwordField.requestFocus();
            return;
        }

        // Animation de chargement
        loginButton.setDisable(true);
        loginButton.setText("Connexion en cours...");

        // Authentification en arrière-plan
        new Thread(() -> {
            Optional<User> userOpt = userDAO.authenticate(username, password);

            Platform.runLater(() -> {
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    SessionManager.startSession(user);

                    // Animation de succès puis redirection
                    showSuccess("Bienvenue, " + user.getFullName() + " !");

                    PauseTransition pause = new PauseTransition(Duration.seconds(1));
                    pause.setOnFinished(e -> SceneManager.switchTo("dashboard"));
                    pause.play();
                } else {
                    showError("Nom d'utilisateur ou mot de passe incorrect");
                    shakeLoginCard();
                    loginButton.setDisable(false);
                    loginButton.setText("Se connecter");
                    passwordField.clear();
                    passwordField.requestFocus();
                }
            });
        }).start();
    }

    private void showError(String message) {
        errorLabel.setText("⚠️ " + message);
        errorLabel.setStyle("-fx-text-fill: #EF4444;");
        errorLabel.setVisible(true);

        // Animation fade in
        FadeTransition fade = new FadeTransition(Duration.millis(200), errorLabel);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private void showSuccess(String message) {
        errorLabel.setText("✅ " + message);
        errorLabel.setStyle("-fx-text-fill: #10B981;");
        errorLabel.setVisible(true);
    }

    private void clearError() {
        errorLabel.setVisible(false);
    }

    private void shakeLoginCard() {
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), loginCard);
        shake.setFromX(-10);
        shake.setToX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.setOnFinished(e -> loginCard.setTranslateX(0));
        shake.play();
    }

    private void playEntryAnimation() {
        // Fade in du card
        loginCard.setOpacity(0);
        loginCard.setTranslateY(30);

        FadeTransition fade = new FadeTransition(Duration.millis(600), loginCard);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(600), loginCard);
        slide.setFromY(30);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition parallel = new ParallelTransition(fade, slide);
        parallel.play();
    }
}
