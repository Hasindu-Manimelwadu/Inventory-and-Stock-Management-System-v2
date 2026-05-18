package com.InventoryManagement.categorymanagement.model;

public abstract class Category {
    // Encapsulation: Private fields with public accessors
    private String id;
    private String categoryName;
    private String description;
    private String categoryType;

    public Category(String id, String categoryName, String description, String categoryType) {
        this.id = id;
        this.categoryName = categoryName;
        this.description = description;
        this.categoryType = categoryType;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategoryType() { return categoryType; }
    public void setCategoryType(String categoryType) { this.categoryType = categoryType; }

    // Polymorphism: Subclasses will override this to change display rendering
    public abstract String getDisplayFormat();
}