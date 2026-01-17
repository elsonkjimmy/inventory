package com.inventory.dao;

import com.inventory.models.Category;
import com.inventory.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object pour les opérations sur les catégories
 */
public class CategoryDAO {

    /**
     * Créer une nouvelle catégorie
     */
    public boolean create(Category category) {
        String sql = """
                    INSERT INTO categories (name, description, color, icon)
                    VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.setString(3, category.getColor());
            stmt.setString(4, category.getIcon());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    category.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur création catégorie: " + e.getMessage());
        }
        return false;
    }

    /**
     * Mettre à jour une catégorie
     */
    public boolean update(Category category) {
        String sql = """
                    UPDATE categories SET
                        name = ?, description = ?, color = ?, icon = ?
                    WHERE id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.setString(3, category.getColor());
            stmt.setString(4, category.getIcon());
            stmt.setInt(5, category.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur mise à jour catégorie: " + e.getMessage());
        }
        return false;
    }

    /**
     * Supprimer une catégorie
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM categories WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur suppression catégorie: " + e.getMessage());
        }
        return false;
    }

    /**
     * Trouver une catégorie par ID
     */
    public Optional<Category> findById(int id) {
        String sql = """
                    SELECT c.*, COUNT(p.id) as product_count
                    FROM categories c
                    LEFT JOIN products p ON c.id = p.category_id AND p.is_active = TRUE
                    WHERE c.id = ?
                    GROUP BY c.id
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToCategory(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur recherche catégorie: " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Trouver une catégorie par nom
     */
    public Optional<Category> findByName(String name) {
        String sql = "SELECT * FROM categories WHERE name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToCategory(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur recherche catégorie: " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Obtenir toutes les catégories
     */
    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();
        String sql = """
                    SELECT c.*, COUNT(p.id) as product_count
                    FROM categories c
                    LEFT JOIN products p ON c.id = p.category_id AND p.is_active = TRUE
                    GROUP BY c.id
                    ORDER BY c.name
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur liste catégories: " + e.getMessage());
        }
        return categories;
    }

    /**
     * Rechercher des catégories
     */
    public List<Category> search(String query) {
        List<Category> categories = new ArrayList<>();
        String sql = """
                    SELECT c.*, COUNT(p.id) as product_count
                    FROM categories c
                    LEFT JOIN products p ON c.id = p.category_id AND p.is_active = TRUE
                    WHERE c.name LIKE ? OR c.description LIKE ?
                    GROUP BY c.id
                    ORDER BY c.name
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + query + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur recherche catégories: " + e.getMessage());
        }
        return categories;
    }

    /**
     * Compter le nombre total de catégories
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM categories";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur comptage catégories: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Mapper un ResultSet vers un objet Category
     */
    private Category mapResultSetToCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setId(rs.getInt("id"));
        category.setName(rs.getString("name"));
        category.setDescription(rs.getString("description"));
        category.setColor(rs.getString("color"));
        category.setIcon(rs.getString("icon"));

        try {
            category.setProductCount(rs.getInt("product_count"));
        } catch (SQLException e) {
            // Column might not exist in all queries
            category.setProductCount(0);
        }

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            category.setCreatedAt(createdAt.toLocalDateTime());
        }

        return category;
    }
}
