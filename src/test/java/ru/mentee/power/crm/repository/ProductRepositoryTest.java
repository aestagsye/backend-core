package ru.mentee.power.crm.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Product;

@SpringBootTest
@Transactional
class ProductRepositoryTest {

  @Autowired private ProductRepository productRepository;

  @BeforeEach
  void setUp() {
    productRepository.deleteAll();
  }

  private static Product newProduct(String name, String sku, BigDecimal price, boolean active) {
    Product product = new Product();
    product.setName(name);
    product.setSku(sku);
    product.setPrice(price);
    product.setActive(active);
    return product;
  }

  @Test
  void shouldSaveAndFindProduct() {
    Product product = newProduct("CRM License", "CRM-001", BigDecimal.valueOf(1999.99), true);

    Product saved = productRepository.save(product);

    Optional<Product> found = productRepository.findById(saved.getId());
    assertThat(found).isPresent();
    assertThat(found.get().getName()).isEqualTo("CRM License");
    assertThat(found.get().getSku()).isEqualTo("CRM-001");
    assertThat(found.get().getPrice()).isEqualByComparingTo("1999.99");
    assertThat(found.get().getActive()).isTrue();
  }

  @Test
  void shouldFindBySku() {
    productRepository.save(newProduct("Support Plan", "SUP-001", BigDecimal.valueOf(499.00), true));

    Optional<Product> found = productRepository.findBySku("SUP-001");

    assertThat(found).isPresent();
    assertThat(found.get().getName()).isEqualTo("Support Plan");
  }

  @Test
  void shouldReturnEmptyWhenSkuNotFound() {
    productRepository.save(newProduct("Support Plan", "SUP-001", BigDecimal.valueOf(499.00), true));

    Optional<Product> found = productRepository.findBySku("UNKNOWN");

    assertThat(found).isEmpty();
  }

  @Test
  void shouldFindOnlyActiveProducts() {
    productRepository.save(newProduct("Core CRM", "CRM-CORE", BigDecimal.valueOf(999.00), true));
    productRepository.save(
        newProduct("Legacy Module", "CRM-OLD", BigDecimal.valueOf(299.00), false));
    productRepository.save(
        newProduct("Analytics Pack", "CRM-ANA", BigDecimal.valueOf(799.00), true));

    List<Product> activeProducts = productRepository.findByActiveTrue();

    assertThat(activeProducts).hasSize(2);
    assertThat(activeProducts)
        .extracting(Product::getSku)
        .containsExactlyInAnyOrder("CRM-CORE", "CRM-ANA");
  }

  @Test
  void shouldDeleteProduct() {
    Product saved =
        productRepository.save(newProduct("Starter", "CRM-START", BigDecimal.valueOf(99.00), true));

    productRepository.delete(saved);

    assertThat(productRepository.findById(saved.getId())).isEmpty();
  }

  @Test
  void shouldReturnEmptyForNonExistentId() {
    Optional<Product> found = productRepository.findById(UUID.randomUUID());

    assertThat(found).isEmpty();
  }
}
