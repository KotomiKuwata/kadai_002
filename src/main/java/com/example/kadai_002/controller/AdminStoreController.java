package com.example.kadai_002.controller;


import java.beans.PropertyEditorSupport;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.kadai_002.entity.Category;
import com.example.kadai_002.entity.Store;
import com.example.kadai_002.form.StoreEditForm;
import com.example.kadai_002.form.StoreRegisterForm;
import com.example.kadai_002.repository.CategoryRepository;
import com.example.kadai_002.repository.StoreRepository;
import com.example.kadai_002.service.StoreService;

@Controller
@RequestMapping("/admin/stores")
public class AdminStoreController {
	private final StoreRepository storeRepository;
	private final StoreService storeService;
	private final CategoryRepository categoryRepository;

	@Autowired
	public AdminStoreController(StoreRepository storeRepository, StoreService storeService, CategoryRepository categoryRepository) {
		this.storeRepository = storeRepository;
		this.storeService = storeService;
		this.categoryRepository = categoryRepository;
	}

	@GetMapping
	public String index(Model model, @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable, @RequestParam(name = "keyword", required = false) String keyword,@RequestParam(name = "categoryName", required = false) String categoryName) {
		Page<Store> storePage;

		if (categoryName != null && !categoryName.isEmpty()) {
			storePage = storeRepository.findByCategory_Name(categoryName, pageable);
		} else if (keyword != null && !keyword.isEmpty()) {
			storePage = storeRepository.findByNameLike("%" + keyword + "%", pageable);
		} else {
			storePage = storeRepository.findAll(pageable);
		}

		model.addAttribute("storePage", storePage);
		model.addAttribute("keyword", keyword);
		model.addAttribute("categoryName", categoryName);
		List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        
		return "admin/stores/index";
	}

	@GetMapping("/{id}")
	public String show(@PathVariable(name = "id") Integer id, Model model) {
		Store store = storeRepository.getReferenceById(id);
		model.addAttribute("store", store);
		return "admin/stores/show";
	}

	@GetMapping("/register")
	public String register(Model model) {
		List<Category> categories = categoryRepository.findAll();
		model.addAttribute("categories", categories);
		model.addAttribute("storeRegisterForm", new StoreRegisterForm());
		return "admin/stores/register";
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Time.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				try {
					// "HH:mm"の形式で入力を受け取り、java.sql.Timeに変換
					SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
					setValue(new Time(sdf.parse(text).getTime()));
				} catch (ParseException e) {
					setValue(null);
				}
			}

			@Override
			public String getAsText() {
				Time value = (Time) getValue();
				return (value != null ? value.toString() : "");
			}
		});
	}

	@PostMapping("/create")
	public String create(@ModelAttribute @Validated StoreRegisterForm storeRegisterForm, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			List<Category> categories = categoryRepository.findAll();
			model.addAttribute("categories", categories);
			return "admin/stores/register";
		}

		storeService.create(storeRegisterForm);
		redirectAttributes.addFlashAttribute("successMessage", "店舗を登録しました。");
		return "redirect:/admin/stores";
	}

	@GetMapping("/{id}/edit")
	public String edit(@PathVariable(name = "id") Integer id, Model model) {
		Store store = storeRepository.getReferenceById(id);
		List<Category> categories = categoryRepository.findAll();
		String imageName = store.getImageName();
		StoreEditForm storeEditForm = new StoreEditForm(store.getId(), store.getName(), null, store.getDescription(), store.getOpeningHours(), store.getClosingTime(), store.getPostalCode(), store.getAddress(), store.getPhoneNumber(), store.getClosedDay(), store.getCategoryId());

		model.addAttribute("imageName", imageName);
		model.addAttribute("storeEditForm", storeEditForm);
		model.addAttribute("categories", categories);

		return "admin/stores/edit";
	}

	@PostMapping("/{id}/update")
	public String update(@ModelAttribute @Validated StoreEditForm storeEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {        
		if (bindingResult.hasErrors()) {
			return "admin/stores/edit";
		}

		storeService.update(storeEditForm);
		redirectAttributes.addFlashAttribute("successMessage", "店舗情報を編集しました。");

		return "redirect:/admin/stores";
	} 

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {        
		storeRepository.deleteById(id);

		redirectAttributes.addFlashAttribute("successMessage", "店舗情報を削除しました。");

		return "redirect:/admin/stores";
	} 
	

}