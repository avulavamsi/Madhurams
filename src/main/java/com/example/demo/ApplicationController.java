package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ApplicationController {

	@RequestMapping(value = "/index")
	public String loadIndex(Model model) {
		return "index";
	}
	
	@RequestMapping(value = "/contact")
	public String loadContact(Model model) {
		return "contact";
	}
	
	@RequestMapping(value = "/terms")
	public String loadTerms(Model model) {
		return "terms";
	}
	
	@RequestMapping(value = "/privacy")
	public String loadPrivacy(Model model) {
		return "privacy";
	}

}
