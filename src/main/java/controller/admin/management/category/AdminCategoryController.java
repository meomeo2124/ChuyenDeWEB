package controller.admin.management.category;

import dao.CategoryDAO;
import models.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Logger;

@Controller
@RequestMapping("/admin/category")
public class AdminCategoryController {

    private static final Logger LOGGER = Logger.getLogger(AdminCategoryController.class.getName());
    private final CategoryDAO categoryDAO;

    @Autowired
    public AdminCategoryController(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    // 1. HIỂN THỊ DANH SÁCH DANH MỤC
    @GetMapping("/manage")
    public String manageCategories(Model model,
                                   @RequestParam(value = "msg", required = false) String msg,
                                   @RequestParam(value = "error", required = false) String error) {
        try {
            List<Category> categoryList = categoryDAO.getAllCategories();
            model.addAttribute("categoryList", categoryList);

            if (msg != null) model.addAttribute("msg", msg);
            if (error != null) model.addAttribute("error", error);
            return "admin/admin_categories";
        } catch (Exception e) {
            LOGGER.severe("Error getting all categories: " + e.getMessage());
            return "redirect:/error";
        }
    }

    // 2. XỬ LÝ THÊM DANH MỤC MỚI
    @PostMapping("/insert")
    public String insertCategory(@RequestParam("title") String title,
                                 @RequestParam("description") String description) {
        if (title == null || title.trim().isEmpty()) {
            String error = URLEncoder.encode("Tên danh mục không được để trống.", StandardCharsets.UTF_8);
            return "redirect:/admin/category/manage?error=" + error;
        }

        try {
            Category category = new Category();
            category.setTitle(title);
            category.setDescription(description);

            boolean isAdded = categoryDAO.addCategory(category);
            if (isAdded) {
                String msg = URLEncoder.encode("Thêm danh mục thành công!", StandardCharsets.UTF_8);
                return "redirect:/admin/category/manage?msg=" + msg;
            } else {
                String error = URLEncoder.encode("Không thể thêm danh mục.", StandardCharsets.UTF_8);
                return "redirect:/admin/category/manage?error=" + error;
            }
        } catch (Exception e) {
            LOGGER.severe("Error inserting category: " + e.getMessage());
            String error = URLEncoder.encode("Lỗi hệ thống khi thêm danh mục.", StandardCharsets.UTF_8);
            return "redirect:/admin/category/manage?error=" + error;
        }
    }

    // 3. HIỂN THỊ FORM CHỈNH SỬA DANH MỤC
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") int categoryId, Model model) {
        try {
            Category category = categoryDAO.getCategoryById(categoryId);

            if (category != null) {
                model.addAttribute("category", category);
                // ✅ ĐÃ SỬA: Hướng về View chuẩn Spring
                return "admin/edit_category";
            } else {
                String error = URLEncoder.encode("Danh mục không tồn tại.", StandardCharsets.UTF_8);
                return "redirect:/admin/category/manage?error=" + error;
            }
        } catch (Exception e) {
            LOGGER.severe("Error fetching category for edit: " + e.getMessage());
            String error = URLEncoder.encode("Lỗi hệ thống khi tải thông tin danh mục.", StandardCharsets.UTF_8);
            return "redirect:/admin/category/manage?error=" + error;
        }
    }

    // 4. XỬ LÝ CẬP NHẬT THÔNG TIN DANH MỤC
    @PostMapping("/update")
    public String updateCategory(@RequestParam("id") int id,
                                 @RequestParam("title") String title,
                                 @RequestParam("description") String description) {
        if (title == null || title.trim().isEmpty()) {
            String error = URLEncoder.encode("Tên danh mục không được để trống.", StandardCharsets.UTF_8);
            return "redirect:/admin/category/manage?error=" + error;
        }

        try {
            Category category = new Category();
            category.setId(id);
            category.setTitle(title);
            category.setDescription(description);

            boolean isUpdated = categoryDAO.updateCategory(category);
            if (isUpdated) {
                String msg = URLEncoder.encode("Cập nhật danh mục thành công!", StandardCharsets.UTF_8);
                return "redirect:/admin/category/manage?msg=" + msg;
            } else {
                String error = URLEncoder.encode("Cập nhật danh mục thất bại.", StandardCharsets.UTF_8);
                return "redirect:/admin/category/manage?error=" + error;
            }
        } catch (Exception e) {
            LOGGER.severe("Error updating category: " + e.getMessage());
            String error = URLEncoder.encode("Lỗi hệ thống khi cập nhật danh mục.", StandardCharsets.UTF_8);
            return "redirect:/admin/category/manage?error=" + error;
        }
    }

    // 5. XỬ LÝ XÓA DANH MỤC (Bảo mật qua PostMapping)
    @PostMapping("/delete")
    public String deleteCategory(@RequestParam("id") int categoryId) {
        try {
            boolean isDeleted = categoryDAO.deleteCategory(categoryId);

            if (isDeleted) {
                String msg = URLEncoder.encode("Xóa danh mục thành công!", StandardCharsets.UTF_8);
                return "redirect:/admin/category/manage?msg=" + msg;
            } else {
                String error = URLEncoder.encode("Không thể xóa danh mục này (có thể chứa sản phẩm liên kết).", StandardCharsets.UTF_8);
                return "redirect:/admin/category/manage?error=" + error;
            }
        } catch (Exception e) {
            LOGGER.severe("Error deleting category: " + e.getMessage());
            String error = URLEncoder.encode("Lỗi hệ thống khi xóa danh mục.", StandardCharsets.UTF_8);
            return "redirect:/admin/category/manage?error=" + error;
        }
    }
}