package controller.web;

import java.util.List;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import models.Category;
import models.Product;
import dao.CategoryDAO;
import dao.ProductDAO;

@Controller
public class HomeController {

	// THÊM ĐOẠN NÀY: Đón đường dẫn gốc "/" và chuyển tiếp sang "/home"
	@GetMapping("/")
	public String index() {
		return "forward:/home";
	}

	@GetMapping("/home")
	public String showHomepage(Model model, HttpServletRequest request, HttpSession session) {
		List<Product> productList = new ProductDAO().getAllProducts();
		List<Category> categoryList = new CategoryDAO().getAllCategories();

		model.addAttribute("productList", productList);

		ServletContext context = request.getServletContext();
		context.setAttribute("categoryList", categoryList);

		return "Homepage";
	}
}