package com.inventory.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Modèle représentant un produit
 */
public class Product {

    private int id;
    private String code;
    private String name;
    private String description;
    private int categoryId;
    private String categoryName;
    private int supplierId;
    private String supplierName;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private int quantity;
    private int alertThreshold;
    private LocalDate expirationDate;
    private String imagePath;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructeur par défaut
    public Product() {
        this.purchasePrice = BigDecimal.ZERO;
        this.sellingPrice = BigDecimal.ZERO;
        this.quantity = 0;
        this.alertThreshold = 10;
        this.isActive = true;
    }

    // Constructeur simplifié
    public Product(String code, String name, BigDecimal sellingPrice, int quantity) {
        this();
        this.code = code;
        this.name = name;
        this.sellingPrice = sellingPrice;
        this.quantity = quantity;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getAlertThreshold() {
        return alertThreshold;
    }

    public void setAlertThreshold(int alertThreshold) {
        this.alertThreshold = alertThreshold;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Méthodes utilitaires

    /**
     * Vérifie si le stock est en dessous du seuil d'alerte
     */
    public boolean isLowStock() {
        return quantity <= alertThreshold;
    }

    /**
     * Vérifie si le produit est en rupture de stock
     */
    public boolean isOutOfStock() {
        return quantity <= 0;
    }

    /**
     * Vérifie si le produit est périmé
     */
    public boolean isExpired() {
        if (expirationDate == null)
            return false;
        return LocalDate.now().isAfter(expirationDate);
    }

    /**
     * Vérifie si le produit expire bientôt (dans les 30 jours)
     */
    public boolean isExpiringSoon() {
        if (expirationDate == null)
            return false;
        LocalDate thirtyDaysFromNow = LocalDate.now().plusDays(30);
        return expirationDate.isBefore(thirtyDaysFromNow) && !isExpired();
    }

    /**
     * Calcule la marge bénéficiaire
     */
    public BigDecimal getProfitMargin() {
        if (purchasePrice == null || purchasePrice.compareTo(BigDecimal.ZERO) == 0) {
            return sellingPrice;
        }
        return sellingPrice.subtract(purchasePrice);
    }

    /**
     * Calcule le pourcentage de marge
     */
    public double getProfitMarginPercentage() {
        if (purchasePrice == null || purchasePrice.compareTo(BigDecimal.ZERO) == 0) {
            return 100.0;
        }
        return getProfitMargin()
                .divide(purchasePrice, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    /**
     * Retourne le statut du stock sous forme de texte
     */
    public String getStockStatus() {
        if (isOutOfStock())
            return "Rupture";
        if (isLowStock())
            return "Faible";
        return "En stock";
    }

    /**
     * Retourne la classe CSS pour le statut du stock
     */
    public String getStockStatusClass() {
        if (isOutOfStock())
            return "out-of-stock";
        if (isLowStock())
            return "low-stock";
        return "in-stock";
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", sellingPrice=" + sellingPrice +
                '}';
    }
}
