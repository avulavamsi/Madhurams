package com.example.demo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

@Controller
public class OrdersController {

	@Autowired
	private OrdersRepository ordersRepository;

	@Autowired
	private ProductsRepository productsRepository;

	@Autowired
	private CollectionPointsRepository collectionPointsRepository;

	@Autowired
	private PaypalService paypalService;

	public static final String PAYPAL_SUCCESS_URL = "order-summary";
	public static final String PAYPAL_CANCEL_URL = "index";
	
	public Orders masterOrder;

	@RequestMapping(value = "/shop")
	public String index(Model model) {
		model.addAttribute("products", productsRepository.findAll());
		model.addAttribute("collectionPoints",
				collectionPointsRepository.findAll(Sort.by(Sort.Direction.ASC, "collectionPoint")));
		return "shop";
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String createOrder(@ModelAttribute("orders") Orders order, @RequestParam Map<String, String> allParams,
			HttpServletRequest request) {

		String orderReference = generateReference(order.getCollPoint(), order.getMobile(), order.getName());
		int refnum = ordersRepository.findByOrderRefStartsWith(orderReference).size() + 1;
		orderReference = orderReference + refnum;
		order.setOrderRef(orderReference);

		LocalDateTime timeStamp = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		order.setOrderDate(timeStamp.format(formatter));

		List<Products> products = productsRepository.findAll();

		products.forEach(product -> {

			if (allParams.get(product.getProductId()) != null) {
				OrderItems orderItems = new OrderItems();
				orderItems.setProductId(product.getProductId());
				orderItems.setQuantity(Integer.parseInt(allParams.get(product.getProductId())));
				orderItems.setUnitPrice(productsRepository.findByProductId(product.getProductId()).getUnitPrice());
				order.addOrderItems(orderItems);
			}
		});

		masterOrder = order;
		
		String cancelUrl = URLUtils.getBaseURl(request) + "/" + PAYPAL_CANCEL_URL;
		String successUrl = URLUtils.getBaseURl(request) + "/" + PAYPAL_SUCCESS_URL;
		try {
			Payment payment = paypalService.createPayment(Double.parseDouble(allParams.get("total")), "GBP", PaypalPaymentMethod.paypal,
					PaypalPaymentIntent.sale, "Total Cost:", cancelUrl, successUrl);
			for (Links links : payment.getLinks()) {
				if (links.getRel().equals("approval_url")) {
					return "redirect:" + links.getHref();
				}
			}
		} catch (PayPalRESTException e) {
//			log.error(e.getMessage());
		}

//		ordersRepository.save(order);

		return "redirect";
	}

	private String generateReference(String collPoint, String mobile, String name) {

		String reference = collPoint.substring(0, 2) + "mdh01" + mobile.substring(6, 11) + name.substring(0, 3);

		return reference.toLowerCase();
	}

	@RequestMapping(method = RequestMethod.GET, value = PAYPAL_CANCEL_URL)
	public String cancelPay() {
		return "index";
	}

	@RequestMapping(method = RequestMethod.GET, value = PAYPAL_SUCCESS_URL)
	public String successPay(Model model, @RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
		model.addAttribute("orderList", masterOrder.getOrderItems());
		try {
			Payment payment = paypalService.executePayment(paymentId, payerId);
			if (payment.getState().equals("approved")) {
				return "order-summary";
			}
		} catch (PayPalRESTException e) {
//			log.error(e.getMessage());
		}
		return "redirect:/";
	}

}
