package com.example.categorymanagement.service;

import com.example.categorymanagement.model.Category;
import com.example.categorymanagement.model.MainCategory;
import com.example.categorymanagement.model.SubCategory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    private final String FILE_PATH = "data/categories.txt";

    public CategoryService() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("Failed to initialize text database initialization: " + e.getMessage());
        }
    }

    // READ All Categories from text file
    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] tokens = line.split("\\|");
                String id = tokens[0];
                String name = tokens[1];
                String desc = tokens[2];
                String type = tokens[3];

                if ("MAIN".equals(type)) {
                    list.add(new MainCategory(id, name, desc));
                } else {
                    String pId = tokens.length > 4 ? tokens[4] : "";
                    String pName = tokens.length > 5 ? tokens[5] : "";
                    list.add(new SubCategory(id, name, desc, pId, pName));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Core Saver Engine
    private void saveAll(List<Category> list) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Category c : list) {
                if (c instanceof MainCategory) {
                    bw.write(c.getId() + "|" + c.getCategoryName() + "|" + c.getDescription() + "|MAIN\n");
                } else if (c instanceof SubCategory) {
                    SubCategory sub = (SubCategory) c;
                    bw.write(sub.getId() + "|" + sub.getCategoryName() + "|" + sub.getDescription() + "|SUB|" + sub.getParentCategoryId() + "|" + sub.getParentCategoryName() + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // CREATE
    public void createCategory(String name, String description, String type, String parentId, String parentName) {
        List<Category> list = getAllCategories();
        String uniqueId = "CAT" + (System.currentTimeMillis() % 10000);
        if ("MAIN".equals(type)) {
            list.add(new MainCategory(uniqueId, name, description));
        } else {
            list.add(new SubCategory(uniqueId, name, description, parentId, parentName));
        }
        saveAll(list);
    }

    public Category getById(String id) {
        return getAllCategories().stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
    }

    // UPDATE
    public void updateCategory(String id, String name, String description) {
        List<Category> list = getAllCategories();
        for (Category c : list) {
            if (c.getId().equals(id)) {
                c.setCategoryName(name);
                c.setDescription(description);
                break;
            }
        }
        saveAll(list);
    }

    // DELETE
    public void deleteCategory(String id) {
        List<Category> list = getAllCategories();
        list.removeIf(c -> c.getId().equals(id));
        saveAll(list);
    }
}
