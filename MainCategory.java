package com.example.categorymanagement.model;

public class MainCategory extends Category {

    // Inheritance: Extends the base Category attributes
    public MainCategory(String id, String categoryName, String description) {
        super(id, categoryName, description, "MAIN");
    }

    @Override
    public String getDisplayFormat() {
        // Polymorphism: Simple clean display format for parent levels
        return getCategoryName();
    }
}