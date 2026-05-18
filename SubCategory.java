package com.example.categorymanagement.model;

public class SubCategory extends Category {
    private String parentCategoryId;
    private String parentCategoryName;

    public SubCategory(String id, String categoryName, String description, String parentCategoryId, String parentCategoryName) {
        super(id, categoryName, description, "SUB");
        this.parentCategoryId = parentCategoryId;
        this.parentCategoryName = parentCategoryName;
    }

    public String getParentCategoryId() { return parentCategoryId; }
    public void setParentCategoryId(String parentCategoryId) { this.parentCategoryId = parentCategoryId; }

    public String getParentCategoryName() { return parentCategoryName; }
    public void setParentCategoryName(String parentCategoryName) { this.parentCategoryName = parentCategoryName; }

    @Override
    public String getDisplayFormat() {
        // Polymorphism: Nested path structure display format unique to SubCategories
        return parentCategoryName + " ➔ " + getCategoryName();
    }
}