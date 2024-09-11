package com.example.kadai_002.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.kadai_002.entity.Category;
import com.example.kadai_002.form.CategoryEditForm;
import com.example.kadai_002.form.CategoryRegisterForm;
import com.example.kadai_002.repository.CategoryRepository;
import com.example.kadai_002.service.CategoryService;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {

	private final CategoryRepository categoryRepository;
	private final CategoryService categoryService;

	public AdminCategoryController(CategoryRepository categoryRepository, CategoryService categoryService) {
		this.categoryRepository = categoryRepository;
		this.categoryService = categoryService;
	}

	@GetMapping
	public String index(Model model,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			@RequestParam(name = "keyword", required = false) String keyword) {
		Page<Category> categoryPage;

		if (keyword != null && !keyword.isEmpty()) {
			categoryPage = categoryRepository.findByNameLike("%" + keyword + "%", pageable);
		} else {
			categoryPage = categoryRepository.findAll(pageable);
		}

		model.addAttribute("categoryPage", categoryPage);

		model.addAttribute("keyword", keyword);

		return "admin/categories/index";
	}

	@GetMapping("/register")
	public String register(Model model) {
		model.addAttribute("categoryRegisterForm", new CategoryRegisterForm());
		return "admin/categories/register";
	}

	@PostMapping("/create")
	public String create(@ModelAttribute @Validated CategoryRegisterForm categoryRegisterForm,
			BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "admin/categories/register";
		}

		categoryService.create(categoryRegisterForm);
		redirectAttributes.addFlashAttribute("successMessage", "民宿を登録しました。");

		return "redirect:/admin/categories";
	}

	@GetMapping("/{id}/edit")
	public String edit(@PathVariable(name = "id") Integer id, Model model) {
		Category category = categoryRepository.getReferenceById(id);
		CategoryEditForm categoryEditForm = new CategoryEditForm(category.getId(), category.getName());

		model.addAttribute("categoryEditForm", categoryEditForm);

		return "admin/categories/edit";
	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
		try {
			categoryService.deleteById(id);
			redirectAttributes.addFlashAttribute("successMessage", "カテゴリを削除しました。");
		} catch (DataIntegrityViolationException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "このカテゴリは使用中のため削除できません。");
		}
		return "redirect:/admin/categories";
	}

	@PostMapping("/{id}/update")
	public String update(@ModelAttribute @Validated CategoryEditForm categoryEditForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "admin/categories/edit";
		}

		categoryService.update(categoryEditForm);
		redirectAttributes.addFlashAttribute("successMessage", "カテゴリ情報を編集しました。");

		return "redirect:/admin/categories";
	}
}
