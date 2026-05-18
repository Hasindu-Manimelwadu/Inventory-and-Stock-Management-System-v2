package com.InventoryManagement.product.controller;

import com.InventoryManagement.product.model.Product;
import com.InventoryManagement.product.service.ProductFileService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductFileService productFileService;

    public ProductController(ProductFileService productFileService) {
        this.productFileService = productFileService;
    }

    @GetMapping
    public String listProducts(@RequestParam(required = false) String keyword, Model model) {
        List<Product> all = productFileService.getAllProducts();
        List<Product> displayed;

        if (keyword != null && !keyword.isBlank()) {
            displayed = productFileService.searchProducts(keyword);
            model.addAttribute("keyword", keyword);
        } else {
            displayed = all;
            model.addAttribute("keyword", "");
        }

        long totalProducts = all.size();
        long inStock       = all.stream().filter(p -> p.getStockStatus().equals("In Stock")).count();
        long lowStock      = all.stream().filter(p -> p.getStockStatus().equals("Low Stock")).count();
        long outOfStock    = all.stream().filter(p -> p.getStockStatus().equals("Out of Stock")).count();

        model.addAttribute("products",      displayed);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("inStock",       inStock);
        model.addAttribute("lowStock",      lowStock);
        model.addAttribute("outOfStock",    outOfStock);

        return "products";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("product",   new Product());
        model.addAttribute("pageTitle", "Add Product");
        return "product-form";
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute Product product) {
        if (product.getId() == 0) {
            productFileService.addProduct(product);
        } else {
            productFileService.updateProduct(product);
        }
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        Product product = productFileService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + id));
        model.addAttribute("product",   product);
        model.addAttribute("pageTitle", "Edit Product");
        return "product-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable int id) {
        productFileService.deleteProduct(id);
        return "redirect:/products";
    }
}