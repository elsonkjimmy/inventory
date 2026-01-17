package com.inventory.models;

import java.math.BigDecimal;

/**
 * Modèle représentant un élément d'une vente
 */
public class SaleItem {

    private int id;
    private int saleId;
    private int productId;
    private String productCode;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal discountPercentage;
    private BigDecimal subtotal;

    // Constructeur par défaut
    public SaleItem() {
        this.quantity = 1;
        this.unitPrice = BigDecimal.ZERO;
        this.discountPercentage = BigDecimal.ZERO;
        this.subtotal = BigDecimal.ZERO;
    }

    // Constructeur avec produit
    public SaleItem(Product product, int quantity) {
        this();
        this.productId = product.getId();
        this.productCode = product.getCode();
        this.productName = product.getName();
        this.unitPrice = product.getSellingPrice();
        this.quantity = quantity;
        calculateSubtotal();
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        calculateSubtotal();
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateSubtotal();
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
        calculateSubtotal();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    // Méthodes utilitaires

    /**
     * Calcule le sous-total avec remise
     */
    public void calculateSubtotal() {
        BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(quantity));

        if (discountPercentage != null && discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discount = total.multiply(discountPercentage)
                    .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
            total = total.subtract(discount);
        }

        this.subtotal = total;
    }

    /**
     * Retourne le montant de la remise
     */
    public BigDecimal getDiscountAmount() {
        if (discountPercentage == null || discountPercentage.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(quantity));
        return total.multiply(discountPercentage)
                .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return "SaleItem{" +
                "productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", subtotal=" + subtotal +
                '}';
    }
}
