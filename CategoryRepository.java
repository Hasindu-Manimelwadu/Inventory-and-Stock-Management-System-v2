package com.category;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {
    private static final String FILE_PATH = "C:\\category_db\\categories.txt";


    private void writeAllToFile(List<Category> list) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(FILE_PATH, false)))) { // false කියන්නේ පරණ ඒවා මකලා අලුතින් ලියනවා
            for (Category c : list) {
                out.println(c.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void save(Category category) {
        new File("C:\\category_db").mkdirs();
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(FILE_PATH, true)))) { // true කියන්නේ අගට එකතු කරනවා
            out.println(category.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public List<Category> findAll() {
        List<Category> list = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) {
                    list.add(new Category(data[0], data[1], data[2]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }


    public void update(Category updatedCategory) {
        List<Category> list = findAll();
        for (int i = 0; i < list.size(); i++) {

            if (list.get(i).getId().equalsIgnoreCase(updatedCategory.getId())) {
                list.set(i, updatedCategory);
                break;
            }
        }
        writeAllToFile(list);
    }


    public void delete(String id) {
        List<Category> list = findAll();

        list.removeIf(category -> category.getId().equalsIgnoreCase(id));
        writeAllToFile(list);
    }
}