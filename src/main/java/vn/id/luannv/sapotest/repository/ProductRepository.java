package vn.id.luannv.sapotest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.id.luannv.sapotest.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
