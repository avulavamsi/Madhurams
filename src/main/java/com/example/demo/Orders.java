package com.example.demo;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;

@Entity
@Table(name = "Orders")
@ComponentScan
public class Orders {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long orderId;

	@Column(name = "name")
	private String name;

	@Column(name = "email")
	private String email;

	@Column(name = "mobile")
	private String mobile;

	@Column(name = "collPoint")
	private String collPoint;
	
	@Column(name = "orderRef")
	private String orderRef ;

	public String getOrderRef() {
		return orderRef;
	}

	public void setOrderRef(String orderRef) {
		this.orderRef = orderRef;
	}

	@Column(name = "orderDate")
	private String orderDate;

	@Column(name = "payment")
	private Boolean payment;

	@Column(name = "delivery")
	private Boolean delivery;

	@OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<OrderItems> orderItems = new HashSet<>();

	public void addOrderItems(OrderItems orderItem) {
		orderItems.add(orderItem);
		orderItem.setOrders(this);
	}

	public Set<OrderItems> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(Set<OrderItems> orderItems) {
		this.orderItems = orderItems;
	}

	public Long getOrderId() {
		return orderId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCollPoint() {
		return collPoint;
	}

	public void setCollPoint(String collPoint) {
		this.collPoint = collPoint;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public Boolean getPayment() {
		return payment;
	}

	public void setPayment(Boolean payment) {
		this.payment = payment;
	}

	public Boolean getDelivery() {
		return delivery;
	}

	public void setDelivery(Boolean delivery) {
		this.delivery = delivery;
	}

}
