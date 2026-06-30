package controller.web;

import dao.DBConnectionPool;
import dao.ProductDAO;
import models.Product;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.List;

@Controller
public class ProductController {

    // Hứng endpoint "/product" thay thế hoàn toàn cho ProductServlet cũ
    @GetMapping("/product")
    public String showProductDetail(
            @RequestParam(value = "id", required = false) String productIdStr,
            Model model,
            RedirectAttributes redirectAttributes) {

        String errorMessage;

        // 1. Kiểm tra ID sản phẩm có bị trống hay không
        if (productIdStr == null || productIdStr.trim().isEmpty()) {
            errorMessage = "Product ID is required";
            return redirectToHomepageWithError(errorMessage, redirectAttributes);
        }

        int id;
        try {
            // 2. Chuyển đổi ID sang số nguyên và kiểm tra số âm
            id = Integer.parseInt(productIdStr.trim());
            if (id <= 0) {
                errorMessage = "Invalid product ID";
                return redirectToHomepageWithError(errorMessage, redirectAttributes);
            }
        } catch (NumberFormatException e) {
            errorMessage = "Invalid product ID format";
            return redirectToHomepageWithError(errorMessage, redirectAttributes);
        }

        // 3. Kết nối Cơ sở dữ liệu và lấy thông tin
        try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
            ProductDAO productDAO = new ProductDAO(connection);

            Product product = productDAO.getProductById(id);

            // 4. Kiểm tra nếu sản phẩm không tồn tại trong DB
            if (product == null) {
                errorMessage = "Product not found";
                return redirectToHomepageWithError(errorMessage, redirectAttributes);
            }

            // 5. Nếu sản phẩm tồn tại, lấy danh sách sản phẩm liên quan đổ lên chân trang
            List<Product> productList = productDAO.getAllProducts();

            // Thiết lập các thuộc tính gửi xuống hiển thị tại product-detail.jsp
            model.addAttribute("product", product);
            model.addAttribute("productList", productList);

            return "product-detail"; // Trỏ đến WEB-INF/views/product-detail.jsp

        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "An error occurred: " + e.getMessage();
            return redirectToHomepageWithError(errorMessage, redirectAttributes);
        }
    }

    // Hàm Helper xử lý chuyển hướng về trang chủ kèm theo thông báo lỗi chuẩn mã hóa URL giống Servlet cũ
    private String redirectToHomepageWithError(String message, RedirectAttributes redirectAttributes) {
        try {
            String encodedError = URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
            // Sử dụng cơ chế redirect của Spring MVC sang HomeController đón đường dẫn "/" (đã trỏ sang /home)
            return "redirect:/?error=" + encodedError;
        } catch (Exception e) {
            return "redirect:/?error=system_error";
        }
    }
}