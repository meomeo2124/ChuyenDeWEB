package controller.web;

import dao.DBConnectionPool;
import dao.ProductDAO;
import models.Product;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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

    // =========================================================================
    // 3. TẢI THÊM SẢN PHẨM BẰNG AJAX (Thay thế hoàn toàn cho LoadMoreServlet cũ)
    // =========================================================================
    @GetMapping(value = "/load", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String ajaxLoadMoreProducts(
            @RequestParam("exists") int amount,
            jakarta.servlet.http.HttpServletRequest request) {

        StringBuilder htmlBuilder = new StringBuilder();
        String contextPath = request.getContextPath();

        try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
            ProductDAO productDAO = new ProductDAO(connection);

            // Gọi hàm getNext4(amount) nguyên bản của bạn từ ProductDAO
            List<Product> products = productDAO.getNext4(amount);

            for (Product o : products) {
                String productUrl = contextPath + "/product?id=" + o.getId();
                String photoName = (o.getPhoto() != null && !o.getPhoto().trim().isEmpty()) ? o.getPhoto() : "no-sample.png";

                htmlBuilder.append("<div class=\"product-count col mb-5\">\r\n")
                        .append("    <div class=\"card h-100\">\r\n")
                        .append("        <a href=\"").append(productUrl).append("\"> \r\n")
                        .append("            <img class=\"card-img-top bg-dark\"\r\n")
                        .append("                 src=\"").append(contextPath).append("/image/product/").append(photoName).append("\"\r\n")
                        .append("                 onerror=\"this.src='").append(contextPath).append("/image/product/no-sample.png'; this.onerror=null;\" />\r\n")
                        .append("        </a>\r\n")
                        .append("        \r\n")
                        .append("        <div class=\"card-body p-4\">\r\n")
                        .append("            <div class=\"text-center\">\r\n")
                        .append("                \r\n")
                        .append("                <h5 class=\"fw-bolder\">\r\n")
                        .append("                    <a href=\"").append(productUrl).append("\" class=\"text-decoration-none text-dark\">\r\n")
                        .append("                        ").append(escapeHtml(o.getName())).append("\r\n")
                        .append("                    </a>\r\n")
                        .append("                </h5>\r\n")
                        .append("                \r\n")
                        .append("                <div class=\"d-flex justify-content-center small text-warning mb-2\">\r\n")
                        .append("                    <div class=\"bi-star-fill\">*</div>\r\n")
                        .append("                    <div class=\"bi-star-fill\">*</div>\r\n")
                        .append("                    <div class=\"bi-star-fill\">*</div>\r\n")
                        .append("                    <div class=\"bi-star-fill\">*</div>\r\n")
                        .append("                    <div class=\"bi-star-fill\">*</div>\r\n")
                        .append("                </div>\r\n")
                        .append("                \r\n")
                        .append("                $ ").append(o.getPrice()).append("\r\n")
                        .append("            </div>\r\n")
                        .append("        </div>\r\n")
                        .append("        \r\n")
                        .append("        <div class=\"card-footer p-4 pt-0 border-top-0 bg-transparent\">\r\n")
                        .append("            <div class=\"text-center\">\r\n")
                        .append("                <a class=\"btn btn-outline-dark mt-auto\" href=\"").append(productUrl).append("\">View options</a>\r\n")
                        .append("            </div>\r\n")
                        .append("        </div>\r\n")
                        .append("    </div>\r\n")
                        .append("</div>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        return htmlBuilder.toString();
    }

    // Hàm tiện ích hỗ trợ lọc các ký tự HTML đặc biệt để tránh lỗi hiển thị dữ liệu
    private String escapeHtml(String input) {
        if (input == null) return null;
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

}