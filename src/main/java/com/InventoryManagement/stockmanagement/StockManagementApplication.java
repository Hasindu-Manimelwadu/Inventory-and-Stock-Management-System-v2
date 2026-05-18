package com.InventoryManagement.stockmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.InventoryManagement",
        "com.example",
        "com.inventory"
})
public class StockManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(StockManagementApplication.class, args);
    }
}