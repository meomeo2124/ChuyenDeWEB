package controller.web;

import dao.CategoryDAO;
import models.Category;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/admin/categories")
public class CategoryAdminController {

    private final CategoryDAO categoryDAO = new CategoryDAO();

    // 1. Hiển thị danh sách danh mục (Thay thế ManageCategoryServlet)
    @GetMapping
    public String manageCategories(Model model) {
        try {
            List<Category> categoryList = categoryDAO.getAllCategories();
            model.addAttribute("categoryList", categoryList);
            return "admin/admin_categories"; // Trỏ vào WEB-INF/views/admin/admin_categories.jsp
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error";
        }
    }

    // 2. Thêm mới danh mục (Thay thế InsertCategoryServlet)
    @PostMapping("/add")
    public String insertCategory(@RequestParam("title") String title,
                                 @RequestParam("description") String description) {
        try {
            Category category = new Category();
            category.setTitle(title);
            category.setDescription(description);

            boolean isAdded = categoryDAO.addCategory(category);

            if (isAdded) {
                return "redirect:/admin/categories";
            } else {
                String errorMsg = URLEncoder.encode("Không thể thêm danh mục!", StandardCharsets.UTF_8);
                return "redirect:/admin/categories?error=" + errorMsg;
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = URLEncoder.encode("Lỗi hệ thống: " + e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/admin/categories?error=" + errorMsg;
        }
    }

    // 3. Hiển thị form chỉnh sửa (Thay thế EditCategoryServlet - GET)
    @GetMapping("/edit")
    public String editCategoryForm(@RequestParam("id") int categoryId, Model model) {
        try {
            Category category = categoryDAO.getCategoryById(categoryId);
            if (category != null) {
                model.addAttribute("category", category);
                return "admin/edit_category"; // Trỏ vào WEB-INF/views/admin/edit_category.jsp
            } else {
                String errorMsg = URLEncoder.encode("Danh mục không tồn tại!", StandardCharsets.UTF_8);
                return "redirect:/admin/categories?error=" + errorMsg;
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = URLEncoder.encode("Lỗi: " + e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/admin/categories?error=" + errorMsg;
        }
    }

    // 4. Thực thi cập nhật danh mục (Thay thế EditCategoryServlet - POST)
    @PostMapping("/edit")
    public String updateCategory(@RequestParam("id") int id,
                                 @RequestParam("title") String title,
                                 @RequestParam("description") String description) {
        try {
            Category category = new Category();
            category.setId(id);
            category.setTitle(title);
            category.setDescription(description);

            boolean isUpdated = categoryDAO.updateCategory(category);

            if (isUpdated) {
                return "redirect:/admin/categories";
            } else {
                String errorMsg = URLEncoder.encode("Không thể cập nhật danh mục!", StandardCharsets.UTF_8);
                return "redirect:/admin/categories?error=" + errorMsg;
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = URLEncoder.encode("Lỗi hệ thống: " + e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/admin/categories?error=" + errorMsg;
        }
    }

    // 5. Xóa danh mục (Thay thế DeleteCategoryServlet)
    @GetMapping("/delete")
    public String deleteCategory(@RequestParam("id") int categoryId) {
        try {
            boolean isDeleted = categoryDAO.deleteCategory(categoryId);

            if (isDeleted) {
                return "redirect:/admin/categories";
            } else {
                String errorMsg = URLEncoder.encode("Không thể xóa danh mục!", StandardCharsets.UTF_8);
                return "redirect:/admin/categories?error=" + errorMsg;
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = URLEncoder.encode("Lỗi khi xóa: " + e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/admin/categories?error=" + errorMsg;
        }
    }
}