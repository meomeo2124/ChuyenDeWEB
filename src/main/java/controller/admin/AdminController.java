package controller.admin;

import dao.CategoryDAO;
import dao.DBConnectionPool;
import dao.ProductDAO;
import dao.UserDAO;
import models.Category;
import models.Product;
import models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.Connection;
import java.util.List;
import java.util.logging.Logger;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger LOGGER = Logger.getLogger(AdminController.class.getName());

    // 1. Hiển thị trang Admin Dashboard chính
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
            UserDAO userDAO = new UserDAO(connection);
            ProductDAO productDAO = new ProductDAO(connection);
            CategoryDAO categoryDAO = new CategoryDAO(connection);

            List<User> userList = userDAO.getAllUsers();
            List<Product> productList = productDAO.getAllProducts();
            List<Category> categoryList = categoryDAO.getAllCategories();

            model.addAttribute("totalUsers", userList != null ? userList.size() : 0);
            model.addAttribute("totalProducts", productList != null ? productList.size() : 0);
            model.addAttribute("totalCategories", categoryList != null ? categoryList.size() : 0);

            double totalRevenue = productDAO.getTotalRevenue();
            model.addAttribute("totalRevenue", totalRevenue);
            model.addAttribute("products", productList);

            // SỬA: Ép Spring forward thẳng ra thư mục /webapp/admin/
            return "forward:/admin/dashboard.jsp";
        } catch (Exception e) {
            model.addAttribute("error", "Không thể tải dữ liệu tổng quan hệ thống.");
            return "forward:/admin/dashboard.jsp";
        }
    }

    // 2. Endpoint hiển thị trang Thống kê chi tiết
    @GetMapping("/statistics")
    public String showStatistics() {
        return "admin/admin_statistics"; // Mở file /WEB-INF/views/admin/admin_statistics.jsp
    }
}