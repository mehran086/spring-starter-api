package com.codewithmosh.store.repositories;

import com.codewithmosh.store.entities.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @EntityGraph(attributePaths = "category")  //it is joining by itself , thereby reducing the number of queries in hibernate.
    public List<Product> findByCategoryId(Byte categoryId);

    @EntityGraph(attributePaths = "category")
    @Query("SELECT p from Product p")
    List<Product> findAllWithCategory();


}