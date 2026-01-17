package com.inventory.models;

import java.time.LocalDateTime;

/**
 * Modèle représentant une catégorie de produits
 */
public class Category {

    private int id;
    private String name;
    private String description;
    private String color;
    private String icon;
    private int productCount;
    private LocalDateTime createdAt;

    // Constructeur par défaut
    public Category() {
        this.color = "#6366F1";
        this.icon = "fas-folder";
    }

    // Constructeur avec paramètres
    public Category(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }

    // Constructeur complet
    public Category(int id, String name, String description, String color, String icon, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.color = color;
        this.icon = icon;
        this.createdAt = createdAt;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return name;
    }
}
