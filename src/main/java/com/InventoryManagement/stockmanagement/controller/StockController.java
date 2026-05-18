package com.InventoryManagement.stockmanagement.controller;

import com.InventoryManagement.stockmanagement.model.StockInTransaction;
import com.InventoryManagement.stockmanagement.model.StockOutTransaction;
import com.InventoryManagement.stockmanagement.model.StockTransaction;
import com.InventoryManagement.stockmanagement.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/stock")
public class StockController {

    @Autowired
    private StockService stockService;

    // Shows the stock list with optional search and type filter
    @GetMapping("/list")
    public String listTransactions(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type,
            Model model) {

        try {
            List<StockTransaction> transactions = stockService.getAllTransactions();

            if (search != null && !search.isEmpty()) {
                String keyword = search.toLowerCase();
                transactions = transactions.stream()
                        .filter(t -> t.getProductId().toLowerCase().contains(keyword)
                                || t.getProductName().toLowerCase().contains(keyword))
                        .toList();
            }

            if (type != null && !type.isEmpty()) {
                transactions = transactions.stream()
                        .filter(t -> t.getTransactionType().equals(type))
                        .toList();
            }

            model.addAttribute("transactions", transactions);
            model.addAttribute("search", search);
            model.addAttribute("type", type);

        } catch (IOException e) {
            model.addAttribute("error", "Failed to load transactions: " + e.getMessage());
        }

        return "stockmanagement/stockList";
    }

    // Shows the add stock entry form
    @GetMapping("/add")
    public String showAddForm() {
        return "stockmanagement/addStock";
    }

    // Handles add stock form submission
    @PostMapping("/add")
    public String addTransaction(
            @RequestParam String productId,
            @RequestParam String productName,
            @RequestParam int quantity,
            @RequestParam String transactionType,
            @RequestParam String date,
            @RequestParam(required = false, defaultValue = "") String notes,
            Model model) {

        try {
            StockTransaction transaction;

            if (transactionType.equals("STOCK_IN")) {
                transaction = new StockInTransaction(
                        "", productId, productName, quantity, date, notes);
            } else {
                transaction = new StockOutTransaction(
                        "", productId, productName, quantity, date, notes);
            }

            stockService.addTransaction(transaction);
            return "redirect:/stock/list";

        } catch (IllegalStateException e) {
            // Insufficient stock — show error message on the form
            model.addAttribute("error", e.getMessage());
            return "stockmanagement/addStock";

        } catch (IOException e) {
            model.addAttribute("error", "Failed to save transaction: " + e.getMessage());
            return "stockmanagement/addStock";
        }
    }

    // Shows update form pre-filled with existing transaction data
    @GetMapping("/update")
    public String showUpdateForm(@RequestParam String id, Model model) {

        try {
            StockTransaction transaction = stockService.getTransactionById(id);

            if (transaction == null) {
                return "redirect:/stock/list";
            }

            model.addAttribute("transaction", transaction);

        } catch (IOException e) {
            model.addAttribute("error", "Failed to load transaction: " + e.getMessage());
        }

        return "stockmanagement/adjustStock";
    }

    // Handles update stock form submission
    @PostMapping("/update")
    public String updateTransaction(
            @RequestParam String transactionId,
            @RequestParam int quantity,
            @RequestParam(required = false, defaultValue = "") String notes,
            Model model) {

        try {
            stockService.updateTransaction(transactionId, quantity, notes);
            return "redirect:/stock/list";

        } catch (IOException e) {
            model.addAttribute("error", "Failed to update transaction: " + e.getMessage());
            return "stockmanagement/adjustStock";
        }
    }

    // Handles delete transaction request
    @GetMapping("/delete")
    public String deleteTransaction(@RequestParam String id) {

        try {
            stockService.deleteTransaction(id);
        } catch (IOException e) {
            System.out.println("Delete failed: " + e.getMessage());
        }

        return "redirect:/stock/list";
    }

    // Shows the stock dashboard with current stock levels
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {

        try {
            List<String[]> stockLevels         = stockService.getCurrentStockLevels();
            List<StockTransaction> transactions = stockService.getAllTransactions();

            long totalStockIn  = transactions.stream()
                    .filter(t -> t.getTransactionType().equals("STOCK_IN")).count();
            long totalStockOut = transactions.stream()
                    .filter(t -> t.getTransactionType().equals("STOCK_OUT")).count();

            model.addAttribute("stockLevels",   stockLevels);
            model.addAttribute("totalStockIn",  totalStockIn);
            model.addAttribute("totalStockOut", totalStockOut);
            model.addAttribute("totalProducts", stockLevels.size());

        } catch (IOException e) {
            model.addAttribute("error", "Failed to load dashboard: " + e.getMessage());
        }

        return "stockmanagement/stockDashboard";
    }
}