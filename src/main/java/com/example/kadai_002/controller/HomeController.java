package com.example.kadai_002.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.kadai_002.entity.Category;
import com.example.kadai_002.entity.Store;
import com.example.kadai_002.repository.CategoryRepository;
import com.example.kadai_002.repository.StoreRepository;

@Controller

public class HomeController {
	
	private final StoreRepository storeRepository;
	private final CategoryRepository categoryRepository;
    
    public HomeController(StoreRepository storeRepository, CategoryRepository categoryRepository) {
		this.storeRepository = storeRepository;
		this.categoryRepository = categoryRepository;
    }
    
	@GetMapping("/")
	public String index(@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "categoryName", required = false) String categoryName,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			Model model) 
	{             
		
        List<Store> newStores = storeRepository.findTop10ByOrderByCreatedAtDesc();
        model.addAttribute("newStores", newStores); 
        model.addAttribute("categoryName", categoryName);
		List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);

        
		return "index";
	}   
}
