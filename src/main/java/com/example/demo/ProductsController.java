package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProductsController {

	@Autowired
	ProductsRepository productsRepository;

	@GetMapping("/products")
	public String showProductList(Model model) {
		model.addAttribute("products", productsRepository.findAll());
		return "products";
	}

	@GetMapping("/edit/{id}")
	public String showUpdateForm(@PathVariable("id") long id, Model model) {
		Products product = productsRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid Product Id:" + id));

		model.addAttribute("product", product);
		return "update-product";
	}

	@PostMapping("/update/{id}")
	public String updateProduct(@PathVariable("id") long id, @Validated Products product, BindingResult result,
			Model model) {
		if (result.hasErrors()) {
			product.setId(id);
			return "update-product";
		}

		productsRepository.save(product);

		return "redirect:/products";
	}

	@GetMapping("/addproduct")
	public String showSignUpForm(Products product) {
		return "add-product";
	}

	@PostMapping("/saveproduct")
	public String addProduct(@ModelAttribute("products") Products product, @RequestParam(name = "productid") String pid,
			@RequestParam(name = "productname") String pname, @RequestParam(name = "unitprice") float up) {

		product.setProductId(pid);
		product.setProductName(pname);
		product.setUnitPrice(up);

		productsRepository.save(product);
		return "redirect:/products";
	}

	@GetMapping("/delete/{id}")
	public String deleteProduct(@PathVariable("id") long id, Model model) {
		Products product = productsRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
		productsRepository.delete(product);

		return "redirect:/products";
	}

}
