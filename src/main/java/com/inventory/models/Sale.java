package com.inventory.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Modèle représentant une vente
 */
public class Sale {

    private int id;
    private String saleNumber;
    private int userId;
    private String userName;
    private String customerName;
    private String customerPhone;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private String paymentMethod; // CASH, CARD, MOBILE
    private String status; // COMPLETED, PENDING, CANCELLED
    private String notes;
    private LocalDateTime createdAt;
    private List<SaleItem> items;

    // Constructeur par défaut
    public Sale() {
        this.totalAmount = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
        this.taxAmount = BigDecimal.ZERO;
        this.paymentMethod = "CASH";
        this.status = "COMPLETED";
        this.items = new ArrayList<>();
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSaleNumber() {
        return saleNumber;
    }

    public void setSaleNumber(String saleNumber) {
        this.saleNumber = saleNumber;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<SaleItem> getItems() {
        return items;
    }

    public void setItems(List<SaleItem> items) {
        this.items = items;
    }

    // Méthodes utilitaires

    public void addItem(SaleItem item) {
        this.items.add(item);
        recalculateTotal();
    }

    public void removeItem(SaleItem item) {
        this.items.remove(item);
        recalculateTotal();
    }

    public void recalculateTotal() {
        this.totalAmount = items.stream()
                .map(SaleItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getFinalAmount() {
        return totalAmount
                .subtract(discountAmount != null ? discountAmount : BigDecimal.ZERO)
                .add(taxAmount != null ? taxAmount : BigDecimal.ZERO);
    }

    public int getTotalItems() {
        return items.stream().mapToInt(SaleItem::getQuantity).sum();
    }

    public String getPaymentMethodDisplay() {
        return switch (paymentMethod) {
            case "CASH" -> "Espèces";
            case "CARD" -> "Carte";
            case "MOBILE" -> "Mobile Money";
            default -> paymentMethod;
        };
    }

    public String getStatusDisplay() {
        return switch (status) {
            case "COMPLETED" -> "Terminée";
            case "PENDING" -> "En attente";
            case "CANCELLED" -> "Annulée";
            default -> status;
        };
    }

    public String getStatusClass() {
        return switch (status) {
            case "COMPLETED" -> "badge-success";
            case "PENDING" -> "badge-warning";
            case "CANCELLED" -> "badge-danger";
            default -> "badge-primary";
        };
    }

    /**
     * Génère un numéro de vente unique
     */
    public static String generateSaleNumber() {
        return "VNT-" + System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Sale{" +
                "id=" + id +
                ", saleNumber='" + saleNumber + '\'' +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                '}';
    }
}
