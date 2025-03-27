package org.example.service;

import java.util.Optional;

import org.example.model.Product;
import org.example.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
public class ProductService {

  private final ProductRepository productRepository;

  @PersistenceContext
  private EntityManager entityManager;

  @Transactional
  public Product testJpaLocking(Long id, double newPrice) {
    Product product = entityManager.find(Product.class, id);
    try {
      Thread.sleep(3000);  // 3-second delay
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    product.setPrice(newPrice);
    entityManager.flush();
    return product;
  }

  public ProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @Transactional
  public Product saveProduct(Product product) {
    return productRepository.save(product);
  }

  @Transactional
  public Product updateProductWithOptimisticLock(Long id, double newPrice) {
    Optional<Product> productOpt = productRepository.findById(id);

    if (productOpt.isPresent()) {
      Product product = productOpt.get();
      product.setPrice(newPrice);

      try {
        Thread.sleep(3000);  // delay
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      try {
        return productRepository.save(product);
      } catch (OptimisticLockException e) {
        throw new RuntimeException("Optimistic lock exception occurred");
      }
    } else {
      throw new RuntimeException("Product not found");
    }
  }

  @Transactional
  public Product updateProductWithPessimisticLock(Long id, double newPrice) {
    Optional<Product> productOpt = productRepository.findByIdForUpdate(id);

    try {
      Thread.sleep(20000);  // 20 s3c delay
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    if (productOpt.isPresent()) {
      Product product = productOpt.get();
      product.setPrice(newPrice);
      return productRepository.save(product);
    } else {
      throw new RuntimeException("Product not found");
    }
  }

  @Transactional
  public Product updateProductWithPessimisticLockNoTimeDelay(Long id, double newPrice) {

    Query q = entityManager.createNativeQuery(
        "SELECT SETTING_VALUE FROM INFORMATION_SCHEMA.SETTINGS WHERE SETTING_NAME='DEFAULT_LOCK_TIMEOUT'");
    System.out.println("Current lock timeout: " + q.getSingleResult());
    Optional<Product> productOpt = productRepository.findByIdForUpdate(id);

    if (productOpt.isPresent()) {
      Product product = productOpt.get();
      product.setPrice(newPrice);
      return productRepository.save(product);
    } else {
      throw new RuntimeException("Product not found");
    }
  }
}