package com.example.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductsRepository extends JpaRepository<Products, Long> {
	
	Products findByProductId(String productId);
	
	@Query(value = "select * from products where activeFlag = 1", nativeQuery = true)
	public List<Products> findActiveProducts();


}
