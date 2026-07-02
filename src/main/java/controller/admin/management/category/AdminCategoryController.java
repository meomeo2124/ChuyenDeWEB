package controller.admin.management.category;

import dao.CategoryDAO;
import dao.DBConnectionPool;
import models.Category;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.List;
import java.util.logging.Logger;

@Controller
@RequestMapping("/admin/category")
public class AdminCategoryController {

    private static final Logger LOGGER = Logger.getLogger(AdminCategoryController.class.getName());

    // 1. Hiển thị danh sách danh mục (Thay thế ManageCategoryServlet)
    @GetMapping("/manage")
    public String manageCategories(Model model,
                                   @RequestParam(value = "msg", required = false) String msg,
                                   @RequestParam(value = "error", required = false) String error) {
        try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
            CategoryDAO categoryDAO = new CategoryDAO(connection);
            List<Category> categoryList = categoryDAO.getAllCategories();

            model.addAttribute("categoryList", categoryList);
            if (msg != null) model.addAttribute("msg", msg);
            if (error != null) model.addAttribute("error", error);

            // Forward thẳng ra thư mục /webapp/admin/ theo cấu trúc dự án của bạn
            return "forward:/admin/admin_categories.jsp";
        } catch (Exception e) {
            LOGGER.severe("Error getting all categories: " + e.getMessage());
            model.addAttribute("error", "Lỗi kết nối cơ sở dữ liệu khi tải danh sách danh mục.");
            return "forward:/admin/admin_categories.jsp";
        }
    }

    // 2. Xử lý thêm danh mục mới (Thay thế InsertCategoryServlet)
    @PostMapping("/insert")
    public String insertCategory(@RequestParam("title") String title,
                                 @RequestParam("description") String description) {
        if (title == null || title.trim().isEmpty()) {
            String error = URLEncoder.encode("Tên danh mục không được để trống.", StandardCharsets.UTF_8);
            return "redirect:/admin/category/manage?error=" + error;
        }

        try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
            CategoryDAO categoryDAO = new CategoryDAO(connection);

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

    // 3. Hiển thị Form chỉnh sửa danh mục (Thay thế EditCategoryServlet - doGet)
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") int categoryId, Model model) {
        try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
            CategoryDAO categoryDAO = new CategoryDAO(connection);
            Category category = categoryDAO.getCategoryById(categoryId);

            if (category != null) {
                model.addAttribute("category", category);
                return "forward:/admin/edit_category.jsp";
            } else {
                String error = URLEncoder.encode("Danh mục không tồn tại.", StandardCharsets.UTF_8);
                return "redirect:/admin/category/manage?error=" + error;
            }
        } catch (Exception e) {
            LOGGER.severe("Error fetching category for edit: " + e.getMessage());
            String error = URLEncoder.encode("Lỗi kết nối khi tải thông tin danh mục.", StandardCharsets.UTF_8);
            return "redirect:/admin/category/manage?error=" + error;
        }
    }

    // 4. Xử lý cập nhật thông tin danh mục (Thay thế EditCategoryServlet - doPost)
    @PostMapping("/update")
    public String updateCategory(@RequestParam("id") int id,
                                 @RequestParam("title") String title,
                                 @RequestParam("description") String description) {
        if (title == null || title.trim().isEmpty()) {
            String error = URLEncoder.encode("Tên danh mục không được để trống.", StandardCharsets.UTF_8);
            return "redirect:/admin/category/manage?error=" + error;
        }

        try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
            CategoryDAO categoryDAO = new CategoryDAO(connection);

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

    // 5. Xử lý xóa danh mục (Thay thế DeleteCategoryServlet - chuyển về POST để an toàn)
    @PostMapping("/delete")
    public String deleteCategory(@RequestParam("id") int categoryId) {
        try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
            CategoryDAO categoryDAO = new CategoryDAO(connection);
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
            String error = URLEncoder.encode("Lỗi kết nối cơ sở dữ liệu khi xóa.", StandardCharsets.UTF_8);
            return "redirect:/admin/category/manage?error=" + error;
        }
    }
}