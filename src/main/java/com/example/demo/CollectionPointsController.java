package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CollectionPointsController {
	@Autowired
	CollectionPointsRepository collectionPointsRepository;

	@GetMapping("/collectionpoints")
	public String showCollectionPoints(Model model) {
		model.addAttribute("collectionPoints",
				collectionPointsRepository.findAll(Sort.by(Sort.Direction.ASC, "collectionPoint")));
		return "collectionpoints";
	}

	@GetMapping("/addpoint")
	public String addPoint(CollectionPoints collectionPoint) {
		return "add-collectionpoint";
	}

	@PostMapping("/savepoint")
	public String savePoint(@ModelAttribute("collectionPoints") CollectionPoints collectionPoint,
			@RequestParam(name = "collectionpoint") String collectionPt) {
		collectionPoint.setCollectionPoint(collectionPt);
		collectionPointsRepository.save(collectionPoint);
		return "redirect:/collectionpoints";
	}

	@GetMapping("/delete/point/{id}")
	public String deletePoint(@PathVariable("id") long id, Model model) {
		CollectionPoints collectionPoint = collectionPointsRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
		collectionPointsRepository.delete(collectionPoint);

		return "redirect:/collectionpoints";
	}

}
