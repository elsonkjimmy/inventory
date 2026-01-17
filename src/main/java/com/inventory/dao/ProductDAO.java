package com.inventory.dao;

import com.inventory.models.Product;
import com.inventory.utils.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object pour les opérations sur les produits
 */
public class ProductDAO {

    /**
     * Créer un nouveau produit
     */
    public boolean create(Product product) {
        String sql = """
                    INSERT INTO products (code, name, description, category_id, supplier_id,
                                          purchase_price, selling_price, quantity, alert_threshold,
                                          expiration_date, image_path)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, product.getCode());
            stmt.setString(2, product.getName());
            stmt.setString(3, product.getDescription());

            if (product.getCategoryId() > 0) {
                stmt.setInt(4, product.getCategoryId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            if (product.getSupplierId() > 0) {
                stmt.setInt(5, product.getSupplierId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            stmt.setBigDecimal(6, product.getPurchasePrice());
            stmt.setBigDecimal(7, product.getSellingPrice());
            stmt.setInt(8, product.getQuantity());
            stmt.setInt(9, product.getAlertThreshold());

            if (product.getExpirationDate() != null) {
                stmt.setDate(10, Date.valueOf(product.getExpirationDate()));
            } else {
                stmt.setNull(10, Types.DATE);
            }

            stmt.setString(11, product.getImagePath());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    product.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur création produit: " + e.getMessage());
        }
        return false;
    }

    /**
     * Mettre à jour un produit
     */
    public boolean update(Product product) {
        String sql = """
                    UPDATE products SET
                        code = ?, name = ?, description = ?, category_id = ?, supplier_id = ?,
                        purchase_price = ?, selling_price = ?, quantity = ?, alert_threshold = ?,
                        expiration_date = ?, image_path = ?, is_active = ?
                    WHERE id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getCode());
            stmt.setString(2, product.getName());
            stmt.setString(3, product.getDescription());

            if (product.getCategoryId() > 0) {
                stmt.setInt(4, product.getCategoryId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            if (product.getSupplierId() > 0) {
                stmt.setInt(5, product.getSupplierId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            stmt.setBigDecimal(6, product.getPurchasePrice());
            stmt.setBigDecimal(7, product.getSellingPrice());
            stmt.setInt(8, product.getQuantity());
            stmt.setInt(9, product.getAlertThreshold());

            if (product.getExpirationDate() != null) {
                stmt.setDate(10, Date.valueOf(product.getExpirationDate()));
            } else {
                stmt.setNull(10, Types.DATE);
            }

            stmt.setString(11, product.getImagePath());
            stmt.setBoolean(12, product.isActive());
            stmt.setInt(13, product.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur mise à jour produit: " + e.getMessage());
        }
        return false;
    }

    /**
     * Supprimer un produit (soft delete)
     */
    public boolean delete(int id) {
        String sql = "UPDATE products SET is_active = FALSE WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur suppression produit: " + e.getMessage());
        }
        return false;
    }

    /**
     * Supprimer définitivement un produit
     */
    public boolean hardDelete(int id) {
        String sql = "DELETE FROM products WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur suppression définitive produit: " + e.getMessage());
        }
        return false;
    }

    /**
     * Trouver un produit par ID
     */
    public Optional<Product> findById(int id) {
        String sql = """
                    SELECT p.*, c.name as category_name, s.name as supplier_name
                    FROM products p
                    LEFT JOIN categories c ON p.category_id = c.id
                    LEFT JOIN suppliers s ON p.supplier_id = s.id
                    WHERE p.id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur recherche produit: " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Trouver un produit par code
     */
    public Optional<Product> findByCode(String code) {
        String sql = """
                    SELECT p.*, c.name as category_name, s.name as supplier_name
                    FROM products p
                    LEFT JOIN categories c ON p.category_id = c.id
                    LEFT JOIN suppliers s ON p.supplier_id = s.id
                    WHERE p.code = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur recherche produit par code: " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Obtenir tous les produits actifs
     */
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String sql = """
                    SELECT p.*, c.name as category_name, s.name as supplier_name
                    FROM products p
                    LEFT JOIN categories c ON p.category_id = c.id
                    LEFT JOIN suppliers s ON p.supplier_id = s.id
                    WHERE p.is_active = TRUE
                    ORDER BY p.name
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur liste produits: " + e.getMessage());
        }
        return products;
    }

    /**
     * Rechercher des produits
     */
    public List<Product> search(String query) {
        List<Product> products = new ArrayList<>();
        String sql = """
                    SELECT p.*, c.name as category_name, s.name as supplier_name
                    FROM products p
                    LEFT JOIN categories c ON p.category_id = c.id
                    LEFT JOIN suppliers s ON p.supplier_id = s.id
                    WHERE p.is_active = TRUE
                      AND (p.name LIKE ? OR p.code LIKE ? OR p.description LIKE ?)
                    ORDER BY p.name
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + query + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur recherche produits: " + e.getMessage());
        }
        return products;
    }

    /**
     * Obtenir les produits par catégorie
     */
    public List<Product> findByCategory(int categoryId) {
        List<Product> products = new ArrayList<>();
        String sql = """
                    SELECT p.*, c.name as category_name, s.name as supplier_name
                    FROM products p
                    LEFT JOIN categories c ON p.category_id = c.id
                    LEFT JOIN suppliers s ON p.supplier_id = s.id
                    WHERE p.is_active = TRUE AND p.category_id = ?
                    ORDER BY p.name
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur liste produits par catégorie: " + e.getMessage());
        }
        return products;
    }

    /**
     * Obtenir les produits avec stock faible
     */
    public List<Product> findLowStock() {
        List<Product> products = new ArrayList<>();
        String sql = """
                    SELECT p.*, c.name as category_name, s.name as supplier_name
                    FROM products p
                    LEFT JOIN categories c ON p.category_id = c.id
                    LEFT JOIN suppliers s ON p.supplier_id = s.id
                    WHERE p.is_active = TRUE AND p.quantity <= p.alert_threshold
                    ORDER BY p.quantity ASC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur liste produits stock faible: " + e.getMessage());
        }
        return products;
    }

    /**
     * Obtenir les produits périmés ou proches de la péremption
     */
    public List<Product> findExpiringSoon(int daysAhead) {
        List<Product> products = new ArrayList<>();
        String sql = """
                    SELECT p.*, c.name as category_name, s.name as supplier_name
                    FROM products p
                    LEFT JOIN categories c ON p.category_id = c.id
                    LEFT JOIN suppliers s ON p.supplier_id = s.id
                    WHERE p.is_active = TRUE
                      AND p.expiration_date IS NOT NULL
                      AND p.expiration_date <= DATE_ADD(CURDATE(), INTERVAL ? DAY)
                    ORDER BY p.expiration_date ASC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, daysAhead);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur liste produits expirant: " + e.getMessage());
        }
        return products;
    }

    /**
     * Mettre à jour la quantité d'un produit
     */
    public boolean updateQuantity(int productId, int quantityChange) {
        String sql = "UPDATE products SET quantity = quantity + ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantityChange);
            stmt.setInt(2, productId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur mise à jour quantité: " + e.getMessage());
        }
        return false;
    }

    /**
     * Compter le nombre total de produits actifs
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM products WHERE is_active = TRUE";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur comptage produits: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Compter les produits avec stock faible
     */
    public int countLowStock() {
        String sql = "SELECT COUNT(*) FROM products WHERE is_active = TRUE AND quantity <= alert_threshold";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur comptage produits stock faible: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Générer un code produit unique
     */
    public String generateProductCode() {
        String prefix = "PRD";
        String sql = "SELECT MAX(CAST(SUBSTRING(code, 4) AS UNSIGNED)) FROM products WHERE code LIKE 'PRD%'";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                int nextNumber = rs.getInt(1) + 1;
                return String.format("%s%06d", prefix, nextNumber);
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Erreur génération code produit: " + e.getMessage());
        }
        return prefix + "000001";
    }

    /**
     * Mapper un ResultSet vers un objet Product
     */
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("id"));
        product.setCode(rs.getString("code"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setCategoryId(rs.getInt("category_id"));
        product.setSupplierId(rs.getInt("supplier_id"));
        product.setPurchasePrice(rs.getBigDecimal("purchase_price"));
        product.setSellingPrice(rs.getBigDecimal("selling_price"));
        product.setQuantity(rs.getInt("quantity"));
        product.setAlertThreshold(rs.getInt("alert_threshold"));
        product.setImagePath(rs.getString("image_path"));
        product.setActive(rs.getBoolean("is_active"));

        // Catégorie et fournisseur (jointures)
        try {
            product.setCategoryName(rs.getString("category_name"));
            product.setSupplierName(rs.getString("supplier_name"));
        } catch (SQLException e) {
            // Colonnes peuvent ne pas exister
        }

        Date expirationDate = rs.getDate("expiration_date");
        if (expirationDate != null) {
            product.setExpirationDate(expirationDate.toLocalDate());
        }

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            product.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            product.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return product;
    }
}
