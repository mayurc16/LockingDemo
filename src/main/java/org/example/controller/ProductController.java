package org.example.controller;


import org.example.model.Product;
import org.example.service.ProductService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @PostMapping
  public Product createProduct(@RequestBody Product product) {
    return productService.saveProduct(product);
  }

  @PutMapping("/optimistic/{id}")
  public Product updateOptimistic(@PathVariable Long id, @RequestParam double price) {
    return productService.updateProductWithOptimisticLock(id, price);
  }


  @PutMapping("/pessimistic/{id}")
  public Product updatePessimistic(@PathVariable Long id, @RequestParam double price) {
    return productService.updateProductWithPessimisticLock(id, price);
  }

  @PutMapping("/pessimistic/fast/{id}")
  public Product updatePessimisticNoTimeDelay(@PathVariable Long id, @RequestParam double price) {
    return productService.updateProductWithPessimisticLockNoTimeDelay(id, price);
  }
}