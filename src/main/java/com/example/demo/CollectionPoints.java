package com.example.demo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CollectionPoints")
public class CollectionPoints {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "collectionPoint")
	private String collectionPoint;
	
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCollectionPoint() {
		return collectionPoint;
	}

	public void setCollectionPoint(String collectionPoint) {
		this.collectionPoint = collectionPoint;
	}

}
