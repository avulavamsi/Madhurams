package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductsRepository extends JpaRepository<Products, Long> {
	
	Products findByProductId(String productId);


}
