package com.example.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
	
	//List<Orders> findByOrderRef(String orderRef);
	List<Orders> findByOrderRefStartsWith(@Param("orderRef") String orderRef);

}
