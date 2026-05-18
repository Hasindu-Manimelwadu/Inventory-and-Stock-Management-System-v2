package com.InventoryManagement.stockmanagement.service;

import com.InventoryManagement.stockmanagement.model.StockInTransaction;
import com.InventoryManagement.stockmanagement.model.StockOutTransaction;
import com.InventoryManagement.stockmanagement.model.StockTransaction;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class StockService {

    private static final String FILE_PATH = "data/stock_transactions.txt";

    // Adds a new transaction — prevents STOCK_OUT from going below zero
    public void addTransaction(StockTransaction transaction) throws IOException {

        List<StockTransaction> existing = getAllTransactions();

        // Validate STOCK_OUT does not exceed available stock
        if (transaction.getTransactionType().equals("STOCK_OUT")) {
            int currentStock = 0;
            for (StockTransaction t : existing) {
                if (t.getProductId().equals(transaction.getProductId())) {
                    if (t.getTransactionType().equals("STOCK_IN")) {
                        currentStock += t.getQuantity();
                    } else {
                        currentStock -= t.getQuantity();
                    }
                }
            }
            if (transaction.getQuantity() > currentStock) {
                throw new IllegalStateException(
                        "Insufficient stock. Available: " + currentStock +
                                ", Requested: " + transaction.getQuantity());
            }
        }

        // Generate next unique ID
        int maxId = 0;
        for (StockTransaction t : existing) {
            try {
                int num = Integer.parseInt(
                        t.getTransactionId().replace("TXN-", "").trim());
                if (num > maxId) maxId = num;
            } catch (NumberFormatException e) {
                // skip non-numeric IDs
            }
        }

        String newId = "TXN-" + String.format("%03d", maxId + 1);
        transaction.setTransactionId(newId);

        BufferedWriter writer = new BufferedWriter(
                new FileWriter(FILE_PATH, true));
        writer.write(transaction.toFileString());
        writer.newLine();
        writer.close();
    }

    // Reads all transactions from the text file
    public List<StockTransaction> getAllTransactions() throws IOException {

        List<StockTransaction> transactions = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return transactions;
        }

        BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH));
        String line;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\|", -1);
            if (parts.length < 6) continue;

            for (int i = 0; i < parts.length; i++) {
                parts[i] = parts[i].trim();
            }

            String type  = parts[1];
            String notes = parts.length > 6 ? parts[6].trim() : "";

            StockTransaction transaction;
            if (type.equals("STOCK_IN")) {
                transaction = new StockInTransaction(
                        parts[0], parts[2], parts[3],
                        Integer.parseInt(parts[4]), parts[5], notes);
            } else {
                transaction = new StockOutTransaction(
                        parts[0], parts[2], parts[3],
                        Integer.parseInt(parts[4]), parts[5], notes);
            }

            transactions.add(transaction);
        }

        reader.close();
        return transactions;
    }

    // Updates quantity and notes of an existing transaction
    public void updateTransaction(String transactionId,
                                  int newQuantity,
                                  String newNotes) throws IOException {

        List<StockTransaction> transactions = getAllTransactions();

        for (StockTransaction t : transactions) {
            if (t.getTransactionId().equals(transactionId)) {
                t.setQuantity(newQuantity);
                t.setNotes(newNotes);
                break;
            }
        }

        rewriteFile(transactions);
    }

    // Deletes a transaction by ID
    public void deleteTransaction(String transactionId) throws IOException {

        List<StockTransaction> transactions = getAllTransactions();
        transactions.removeIf(t -> t.getTransactionId().equals(transactionId));
        rewriteFile(transactions);
    }

    // Rewrites the entire file with the updated list
    private void rewriteFile(List<StockTransaction> transactions)
            throws IOException {

        BufferedWriter writer = new BufferedWriter(
                new FileWriter(FILE_PATH, false));

        for (StockTransaction t : transactions) {
            writer.write(t.toFileString());
            writer.newLine();
        }

        writer.close();
    }

    // Finds a single transaction by ID
    public StockTransaction getTransactionById(String transactionId)
            throws IOException {

        for (StockTransaction t : getAllTransactions()) {
            if (t.getTransactionId().equals(transactionId)) {
                return t;
            }
        }
        return null;
    }

    // Calculates current stock level per product with status
    public List<String[]> getCurrentStockLevels() throws IOException {

        java.util.Map<String, String[]> stockMap =
                new java.util.LinkedHashMap<>();

        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return new ArrayList<>(stockMap.values());
        }

        BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH));
        String line;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\|", -1);
            if (parts.length < 6) continue;

            for (int i = 0; i < parts.length; i++) {
                parts[i] = parts[i].trim();
            }

            String productId   = parts[2];
            String productName = parts[3];
            String type        = parts[1];
            int quantity       = Integer.parseInt(parts[4]);

            if (!stockMap.containsKey(productId)) {
                // 4-element array: [productId, productName, stockLevel, status]
                stockMap.put(productId,
                        new String[]{productId, productName, "0", "In Stock"});
            }

            String[] entry = stockMap.get(productId);
            int current    = Integer.parseInt(entry[2]);

            if (type.equals("STOCK_IN")) {
                current += quantity;
            } else {
                // Never go below zero
                current = Math.max(0, current - quantity);
            }

            // Determine status based on current stock level
            String status;
            if (current <= 0) {
                status = "Out of Stock";
            } else if (current <= 5) {
                status = "Low Stock";
            } else {
                status = "In Stock";
            }

            entry[2] = String.valueOf(current);
            entry[3] = status;
        }

        reader.close();
        return new ArrayList<>(stockMap.values());
    }
}