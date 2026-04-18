package ru.mentee.power.crm.repository;

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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ProductRepositoryTest {

  @Autowired
  private ProductRepository productRepository;

  @BeforeEach
  void setUp() {
    productRepository.deleteAll();
  }

  @Test
  void shouldSaveAndFindProduct() {
    Product product = new Product(null, "CRM License", "CRM-001", BigDecimal.valueOf(1999.99), true);

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
    productRepository.save(new Product(null, "Support Plan", "SUP-001", BigDecimal.valueOf(499.00), true));

    Optional<Product> found = productRepository.findBySku("SUP-001");

    assertThat(found).isPresent();
    assertThat(found.get().getName()).isEqualTo("Support Plan");
  }

  @Test
  void shouldReturnEmptyWhenSkuNotFound() {
    productRepository.save(new Product(null, "Support Plan", "SUP-001", BigDecimal.valueOf(499.00), true));

    Optional<Product> found = productRepository.findBySku("UNKNOWN");

    assertThat(found).isEmpty();
  }

  @Test
  void shouldFindOnlyActiveProducts() {
    productRepository.save(new Product(null, "Core CRM", "CRM-CORE", BigDecimal.valueOf(999.00), true));
    productRepository.save(new Product(null, "Legacy Module", "CRM-OLD", BigDecimal.valueOf(299.00), false));
    productRepository.save(new Product(null, "Analytics Pack", "CRM-ANA", BigDecimal.valueOf(799.00), true));

    List<Product> activeProducts = productRepository.findByActiveTrue();

    assertThat(activeProducts).hasSize(2);
    assertThat(activeProducts)
            .extracting(Product::getSku)
            .containsExactlyInAnyOrder("CRM-CORE", "CRM-ANA");
  }

  @Test
  void shouldDeleteProduct() {
    Product saved = productRepository.save(
            new Product(null, "Starter", "CRM-START", BigDecimal.valueOf(99.00), true)
    );

    productRepository.delete(saved);

    assertThat(productRepository.findById(saved.getId())).isEmpty();
  }

  @Test
  void shouldReturnEmptyForNonExistentId() {
    Optional<Product> found = productRepository.findById(UUID.randomUUID());

    assertThat(found).isEmpty();
  }
}
