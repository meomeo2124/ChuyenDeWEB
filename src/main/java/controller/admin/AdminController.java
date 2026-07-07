package controller.admin;

import dao.CategoryDAO;
import dao.ProductDAO;
import dao.UserDAO;
import dao.OrderDAO; // BỔ SUNG ORDER DAO
import models.Category;
import models.Product;
import models.User;
import models.Order;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger LOGGER = Logger.getLogger(AdminController.class.getName());
    private final UserDAO userDAO;
    private final ProductDAO productDAO;
    private final CategoryDAO categoryDAO;
    private final OrderDAO orderDAO; // Khai báo thêm

    @Autowired
    public AdminController(UserDAO userDAO, ProductDAO productDAO, CategoryDAO categoryDAO, OrderDAO orderDAO) {
        this.userDAO = userDAO;
        this.productDAO = productDAO;
        this.categoryDAO = categoryDAO;
        this.orderDAO = orderDAO;
    }

    // 1. Hiển thị trang Admin Dashboard chính (Có tích hợp Bộ Lọc Thời Gian thực tế)
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model,
                                @RequestParam(value = "startDate", required = false) String startDateStr,
                                @RequestParam(value = "endDate", required = false) String endDateStr) {
        // 🔒 Bảo mật hệ thống
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getIsAdmin()) {
            return "redirect:/login?error=Ban khong co quyen truy cap vao vung Admin!";
        }

        try {
            // Lấy dữ liệu tổng số lượng cơ bản
            List<User> userList = userDAO.getAllUsers();
            List<Product> productList = productDAO.getAllProducts();
            model.addAttribute("totalUsers", userList != null ? userList.size() : 0);
            model.addAttribute("totalProducts", productList != null ? productList.size() : 0);
            model.addAttribute("products", productList);

            // Xử lý bộ lọc thời gian (Nếu để trống thì mặc định lấy từ đầu tháng đến hiện tại)
            if (startDateStr == null || startDateStr.isEmpty()) {
                startDateStr = new SimpleDateFormat("yyyy-MM-01'T'00:00").format(new Date());
            }
            if (endDateStr == null || endDateStr.isEmpty()) {
                endDateStr = new SimpleDateFormat("yyyy-MM-dd'T'23:59").format(new Date());
            }
            model.addAttribute("startDate", startDateStr);
            model.addAttribute("endDate", endDateStr);

            // Định dạng chuỗi datetime-local từ HTML sang dạng chuẩn dữ liệu SQL của bạn
            String sqlStart = startDateStr.replace("T", " ") + ":00";
            String sqlEnd = endDateStr.replace("T", " ") + ":59";

            // Lấy danh sách đơn hàng thực tế theo khoảng thời gian đã lọc
            List<Order> filteredOrders = orderDAO.getOrdersBySubsets(sqlStart, sqlEnd);

            int paidCount = 0;
            int pendingCount = 0;
            double totalRevenue = 0.0;

            if (filteredOrders != null) {
                for (Order o : filteredOrders) {
                    if ("PAID".equals(o.getStatus())) {
                        paidCount++;
                        totalRevenue += o.getTotalPrice(); // Chỉ cộng doanh thu từ đơn ĐÃ THANH TOÁN
                    } else if ("PENDING".equals(o.getStatus())) {
                        pendingCount++;
                    }
                }
            }

            // Gửi dữ liệu tính toán thực tế ra Dashboard
            model.addAttribute("paidOrders", paidCount);
            model.addAttribute("pendingOrders", pendingCount);
            model.addAttribute("totalRevenue", totalRevenue);

            return "admin/dashboard";
        } catch (Exception e) {
            LOGGER.severe("Error loading dashboard data: " + e.getMessage());
            model.addAttribute("error", "Không thể tải dữ liệu tổng quan hệ thống.");
            return "admin/dashboard";
        }
    }

    // 2. Endpoint hiển thị trang Thống kê chi tiết thực tế
    @GetMapping("/statistics")
    public String showStatistics(HttpSession session, Model model,
                                 @RequestParam(value = "startDate", required = false) String startDateStr,
                                 @RequestParam(value = "endDate", required = false) String endDateStr) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getIsAdmin()) {
            return "redirect:/login?error=Ban khong co quyen truy cap vao vung Admin!";
        }

        try {
            // Mặc định khoảng thời gian nếu không chọn
            if (startDateStr == null || startDateStr.isEmpty()) {
                startDateStr = new SimpleDateFormat("yyyy-MM-01'T'00:00").format(new Date());
            }
            if (endDateStr == null || endDateStr.isEmpty()) {
                endDateStr = new SimpleDateFormat("yyyy-MM-dd'T'23:59").format(new Date());
            }
            model.addAttribute("startDate", startDateStr);
            model.addAttribute("endDate", endDateStr);

            String sqlStart = startDateStr.replace("T", " ") + ":00";
            String sqlEnd = endDateStr.replace("T", " ") + ":59";

            List<Order> filteredOrders = orderDAO.getOrdersBySubsets(sqlStart, sqlEnd);
            double currentRevenue = 0.0;
            if (filteredOrders != null) {
                for (Order o : filteredOrders) {
                    if ("PAID".equals(o.getStatus())) {
                        currentRevenue += o.getTotalPrice();
                    }
                }
            }
            model.addAttribute("totalRevenue", currentRevenue);

            // Lấy thêm tổng quát để render báo cáo bên cạnh
            model.addAttribute("totalProducts", productDAO.getAllProducts().size());
            model.addAttribute("totalUsers", userDAO.getAllUsers().size());
            model.addAttribute("totalCategories", categoryDAO.getAllCategories().size());

            return "admin/admin_statistics";
        } catch (Exception e) {
            LOGGER.severe("Error loading statistics: " + e.getMessage());
            return "admin/admin_statistics";
        }
    }
}