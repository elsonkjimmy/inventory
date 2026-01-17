package com.inventory.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Gestionnaire de connexion à la base de données MySQL
 * Utilise le pattern Singleton pour garantir une seule connexion
 */
public class DatabaseConnection {

    // Configuration de la base de données
    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DATABASE = "inventory_db_2";
    private static final String USER = "inventory_user";
    private static final String PASSWORD = "jimand123"; // À configurer

    private static final String URL = String.format(
            "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
            HOST, PORT, DATABASE);

    private static Connection connection = null;

    /**
     * Obtenir la connexion à la base de données (Singleton)
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Connexion à MySQL établie avec succès!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver MySQL non trouvé: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion MySQL: " + e.getMessage());
        }
        return connection;
    }

    /**
     * Tester la connexion à la base de données
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Fermer la connexion
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔌 Connexion à la base de données fermée.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la fermeture: " + e.getMessage());
        }
    }

    /**
     * Initialiser les tables de la base de données
     */
    public static void initializeDatabase() {
        Connection conn = getConnection();
        if (conn == null)
            return;

        try (Statement stmt = conn.createStatement()) {

            // Temporairement désactiver les vérifications de clés étrangères
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");

            // Supprimer la table des utilisateurs si elle existe pour s'assurer d'une structure à jour
            stmt.executeUpdate("DROP TABLE IF EXISTS users");
            System.out.println("✅ Table 'users' supprimée (si elle existait).");

            // Réactiver les vérifications de clés étrangères après la recréation de la table users
            // (sera réactivé après toutes les créations de tables pour éviter des problèmes)

            // Table des utilisateurs
            stmt.executeUpdate("""
                        CREATE TABLE users (
                            id INT PRIMARY KEY AUTO_INCREMENT,
                            username VARCHAR(50) UNIQUE NOT NULL,
                            password VARCHAR(255) NOT NULL,
                            full_name VARCHAR(100) NOT NULL,
                            email VARCHAR(100),
                            phone VARCHAR(20),
                            role ENUM('ADMIN', 'GESTIONNAIRE') NOT NULL DEFAULT 'GESTIONNAIRE',
                            is_active BOOLEAN DEFAULT TRUE,
                            last_login DATETIME,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                        )
                    """);

            // Table des catégories
            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS categories (
                            id INT PRIMARY KEY AUTO_INCREMENT,
                            name VARCHAR(100) UNIQUE NOT NULL,
                            description TEXT,
                            color VARCHAR(7) DEFAULT '#6366F1',
                            icon VARCHAR(50) DEFAULT 'fas-folder',
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                        )
                    """);

            // Table des fournisseurs
            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS suppliers (
                            id INT PRIMARY KEY AUTO_INCREMENT,
                            name VARCHAR(100) NOT NULL,
                            contact_name VARCHAR(100),
                            email VARCHAR(100),
                            phone VARCHAR(20),
                            address TEXT,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                        )
                    """);

            // Table des produits
            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS products (
                            id INT PRIMARY KEY AUTO_INCREMENT,
                            code VARCHAR(50) UNIQUE NOT NULL,
                            name VARCHAR(100) NOT NULL,
                            description TEXT,
                            category_id INT,
                            supplier_id INT,
                            purchase_price DECIMAL(10,2) NOT NULL DEFAULT 0,
                            selling_price DECIMAL(10,2) NOT NULL,
                            quantity INT NOT NULL DEFAULT 0,
                            alert_threshold INT NOT NULL DEFAULT 10,
                            expiration_date DATE,
                            image_path VARCHAR(255),
                            is_active BOOLEAN DEFAULT TRUE,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
                            FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE SET NULL
                        )
                    """);

            // Table des promotions
            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS promotions (
                            id INT PRIMARY KEY AUTO_INCREMENT,
                            product_id INT NOT NULL,
                            discount_percentage DECIMAL(5,2) NOT NULL,
                            start_date DATE NOT NULL,
                            end_date DATE NOT NULL,
                            is_active BOOLEAN DEFAULT TRUE,
                            created_by INT,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
                            FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
                        )
                    """);

            // Table des ventes
            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS sales (
                            id INT PRIMARY KEY AUTO_INCREMENT,
                            sale_number VARCHAR(50) UNIQUE NOT NULL,
                            user_id INT NOT NULL,
                            customer_name VARCHAR(100),
                            customer_phone VARCHAR(20),
                            total_amount DECIMAL(12,2) NOT NULL,
                            discount_amount DECIMAL(10,2) DEFAULT 0,
                            tax_amount DECIMAL(10,2) DEFAULT 0,
                            payment_method ENUM('CASH', 'CARD', 'MOBILE') DEFAULT 'CASH',
                            status ENUM('COMPLETED', 'PENDING', 'CANCELLED') DEFAULT 'COMPLETED',
                            notes TEXT,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
                        )
                    """);

            // Table des détails de vente
            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS sale_items (
                            id INT PRIMARY KEY AUTO_INCREMENT,
                            sale_id INT NOT NULL,
                            product_id INT NOT NULL,
                            quantity INT NOT NULL,
                            unit_price DECIMAL(10,2) NOT NULL,
                            discount_percentage DECIMAL(5,2) DEFAULT 0,
                            subtotal DECIMAL(12,2) NOT NULL,
                            FOREIGN KEY (sale_id) REFERENCES sales(id) ON DELETE CASCADE,
                            FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT
                        )
                    """);

            // Table des mouvements de stock
            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS stock_movements (
                            id INT PRIMARY KEY AUTO_INCREMENT,
                            product_id INT NOT NULL,
                            user_id INT NOT NULL,
                            movement_type ENUM('IN', 'OUT', 'ADJUSTMENT') NOT NULL,
                            quantity INT NOT NULL,
                            reason VARCHAR(255),
                            reference_id INT,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
                        )
                    """);

            // Table des notifications
            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS notifications (
                            id INT PRIMARY KEY AUTO_INCREMENT,
                            user_id INT,
                            title VARCHAR(100) NOT NULL,
                            message TEXT NOT NULL,
                            type ENUM('INFO', 'WARNING', 'ERROR', 'SUCCESS') DEFAULT 'INFO',
                            is_read BOOLEAN DEFAULT FALSE,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                        )
                    """);

            // Table des logs d'activité
            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS activity_logs (
                            id INT PRIMARY KEY AUTO_INCREMENT,
                            user_id INT,
                            action VARCHAR(100) NOT NULL,
                            entity_type VARCHAR(50),
                            entity_id INT,
                            details TEXT,
                            ip_address VARCHAR(45),
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
                        )
                    """);

            System.out.println("✅ Toutes les tables ont été créées/vérifiées avec succès!");

            // Réactiver les vérifications de clés étrangères
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'initialisation des tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Insérer un admin par défaut si aucun utilisateur n'existe
     */
    public static void insertDefaultAdmin() {
        Connection conn = getConnection();
        if (conn == null)
            return;

        try (Statement stmt = conn.createStatement()) {
            // Vérifier si un admin existe
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE role = 'ADMIN'");
            rs.next();
            if (rs.getInt(1) == 0) {
                // Mot de passe par défaut: admin123 (haché avec BCrypt)
                String hashedPassword = org.mindrot.jbcrypt.BCrypt.hashpw("admin123",
                        org.mindrot.jbcrypt.BCrypt.gensalt());
                stmt.executeUpdate(String.format("""
                            INSERT INTO users (username, password, full_name, email, role)
                            VALUES ('admin', '%s', 'Administrateur Principal', 'admin@inventory.local', 'ADMIN')
                        """, hashedPassword));
                System.out.println("✅ Utilisateur admin créé! (login: admin, password: admin123)");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la création de l'admin: " + e.getMessage());
        }
    }
}
