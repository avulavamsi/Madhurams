package com.example.demo;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@Controller
public class OrdersController {
	
	static final org.slf4j.Logger log = 
	        LoggerFactory.getLogger(MadhuramsApplication.class);
	
	private float orderTotal;

	List<OrderSummary> orderSummaryList = new ArrayList<OrderSummary>();

	@Autowired
	private OrdersRepository ordersRepository;

	@Autowired
	private ProductsRepository productsRepository;

	@Autowired
	private CollectionPointsRepository collectionPointsRepository;

	@Autowired
	private EmailService emailService;

	@Value("${STRIPE_PUBLIC_KEY}")
	private String stripePublicKey;

	@Value("${STRIPE_SECRET_KEY}")
	private String secretKey;

	public Orders masterOrder;

	public Model orderModel;

	private static final DecimalFormat df = new DecimalFormat("0.00");

	@RequestMapping(value = "/shop")
	public String index(Model model) {

		model.addAttribute("products", productsRepository.findAll());
		model.addAttribute("collectionPoints",
				collectionPointsRepository.findAll(Sort.by(Sort.Direction.ASC, "collectionPoint")));
		model.addAttribute("total", "0.00");
		return "shop";
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String createOrder(@ModelAttribute("orders") Orders order, @RequestParam Map<String, String> allParams,
			HttpServletRequest request, Model model) throws StripeException {

		orderTotal = 0;
		orderSummaryList.clear();
		
		log.info("Proceed to save");

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

				if (Integer.parseInt(allParams.get(product.getProductId())) != 0) {

					// Order & items Model
					OrderItems orderItems = new OrderItems();
					orderItems.setProductId(product.getProductId());
					orderItems.setQuantity(Integer.parseInt(allParams.get(product.getProductId())));
					orderItems.setUnitPrice(productsRepository.findByProductId(product.getProductId()).getUnitPrice());
					order.addOrderItems(orderItems);

					// Summary Page Model
					OrderSummary orderSummary = new OrderSummary();
					orderSummary.setProductName(product.getProductName());
					orderSummary.setQuantity(Integer.parseInt(allParams.get(product.getProductId())));
					orderSummary
							.setUnitPrice(productsRepository.findByProductId(product.getProductId()).getUnitPrice());
					orderSummary.setItemTotal(productsRepository.findByProductId(product.getProductId()).getUnitPrice()
							* Integer.parseInt(allParams.get(product.getProductId())));

					orderTotal = orderTotal + productsRepository.findByProductId(product.getProductId()).getUnitPrice()
							* Integer.parseInt(allParams.get(product.getProductId()));

					orderSummaryList.add(orderSummary);
				}
			}
		});

		masterOrder = order;

		Stripe.apiKey = secretKey;

		List<SessionCreateParams.LineItem> itemList = new ArrayList<SessionCreateParams.LineItem>();

		for (OrderSummary prd : orderSummaryList) {

			System.out.println(prd.getQuantity());

			itemList.add(
					SessionCreateParams.LineItem
							.builder().setQuantity(
									(long) prd.getQuantity())
							.setPriceData(SessionCreateParams.LineItem.PriceData.builder().setCurrency("gbp")
									.setUnitAmountDecimal(BigDecimal.valueOf(prd.getUnitPrice() * 100))
									.setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
											.setName(prd.getProductName()).build())
									.build())
							.build());
		}
		
		String successUrl = URLUtils.getBaseURl(request) + "/success";

		SessionCreateParams params = SessionCreateParams.builder().setMode(SessionCreateParams.Mode.PAYMENT)
				.setSuccessUrl(successUrl).addAllLineItem(itemList).build();

		Session session = Session.create(params);

		return "redirect:" + session.getUrl();

	}

	@ExceptionHandler(StripeException.class)
	public String handleError(Model model, StripeException ex) {
		System.out.println("message" + ex.getMessage());
		model.addAttribute("error", ex.getMessage());
		return "order-summary";
	}

	private String generateReference(String collPoint, String mobile, String name) {
		String reference = collPoint.substring(0, 2) + "mdh23" + mobile.substring(6, 11) + name.substring(0, 3);
		return reference.toLowerCase();
	}

	@RequestMapping(value = "/success")
	public String loadSuccess(HttpServletRequest request, Model model) {
		
		//Save order
		masterOrder.setPayment(true);
		ordersRepository.save(masterOrder);
		
		/// send the order confirmation email
		BigDecimal bd_total = new BigDecimal(df.format(orderTotal));
		emailService.sendOrderConfirmationEmail(masterOrder.getEmail(), masterOrder.getOrderRef(), orderSummaryList, bd_total, masterOrder.getCollPoint());
		
		model.addAttribute("orderRef", masterOrder.getOrderRef());
		return "order-summary";
	}

}
