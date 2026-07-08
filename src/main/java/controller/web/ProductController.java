package controller.web;

import dao.ProductDAO;
import dao.OrderDAO; // Thêm import OrderDAO
import models.Product;
import models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Controller
public class ProductController {

    private final ProductDAO productDAO;
    private final OrderDAO orderDAO; // 🌟 BỔ SUNG: Khai báo OrderDAO

    @Autowired // 🌟 CẬP NHẬT: Inject cả ProductDAO và OrderDAO qua Constructor
    public ProductController(ProductDAO productDAO, OrderDAO orderDAO) {
        this.productDAO = productDAO;
        this.orderDAO = orderDAO;
    }

    @GetMapping("/product")
    public String getProductDetail(
            @RequestParam(value = "id", required = false) String productIdStr,
            Model model,
            jakarta.servlet.http.HttpSession session, // 🌟 BỔ SUNG: Thêm HttpSession để đọc User
            RedirectAttributes redirectAttributes) {

        String errorMessage;

        // 1. Kiểm tra ID trống
        if (productIdStr == null || productIdStr.trim().isEmpty()) {
            errorMessage = "Mã số sản phẩm không được để trống";
            return redirectToHomepageWithError(errorMessage, redirectAttributes);
        }

        int id;
        try {
            id = Integer.parseInt(productIdStr.trim());
            if (id <= 0) {
                errorMessage = "Mã số sản phẩm không hợp lệ";
                return redirectToHomepageWithError(errorMessage, redirectAttributes);
            }
        } catch (NumberFormatException e) {
            errorMessage = "Định dạng mã sản phẩm phải là ký số";
            return redirectToHomepageWithError(errorMessage, redirectAttributes);
        }

        try {
            // 2. Truy vấn chi tiết đồ uống
            Product product = productDAO.getProductById(id);
            if (product == null) {
                errorMessage = "Sản phẩm không tồn tại trong hệ thống";
                return redirectToHomepageWithError(errorMessage, redirectAttributes);
            }

            // 3. Nạp danh sách đồ uống gợi ý và danh sách đánh giá thực tế
            List<Product> productList = productDAO.getAllProducts();
            List<models.Review> reviews = productDAO.getReviewsByProductId(id);
            boolean canReview = false;
            User user = (User) session.getAttribute("user");
            if (user != null) {
                canReview = orderDAO.hasUserPurchasedProduct(user.getId(), id);
            }

            model.addAttribute("product", product);
            model.addAttribute("productList", productList);
            model.addAttribute("reviews", reviews);
            model.addAttribute("canReview", canReview); // 🌟 BỔ SUNG: Đẩy kết quả (true/false) ra file JSP

            return "product-detail";

        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "Lỗi hệ thống: " + e.getMessage();
            return redirectToHomepageWithError(errorMessage, redirectAttributes);
        }
    }

    private String redirectToHomepageWithError(String message, RedirectAttributes redirectAttributes) {
        try {
            String encodedError = URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
            return "redirect:/?error=" + encodedError;
        } catch (Exception e) {
            return "redirect:/?error=system_error";
        }
    }

    @PostMapping("/product/review")
    public String submitReview(@RequestParam("productId") int productId,
                               @RequestParam("rating") int rating,
                               @RequestParam("comment") String comment,
                               @RequestParam(value = "reviewImage", required = false) org.springframework.web.multipart.MultipartFile file,
                               jakarta.servlet.http.HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null || !orderDAO.hasUserPurchasedProduct(user.getId(), productId)) {
            return "redirect:/product?id=" + productId + "&error=unauthorized";
        }

        String username = user.getUsername();
        String imagePath = null;

        if (file != null && !file.isEmpty()) {
            try {
                String uploadsDir = session.getServletContext().getRealPath("/") + "uploads/";
                java.io.File dir = new java.io.File(uploadsDir);
                if (!dir.exists()) dir.mkdirs();

                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                file.transferTo(new java.io.File(uploadsDir + fileName));
                imagePath = fileName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        models.Review rev = new models.Review();
        rev.setProductId(productId);
        rev.setUsername(username);
        rev.setRating(rating);
        rev.setComment(comment);
        rev.setImagePath(imagePath);

        productDAO.insertReview(rev);
        return "redirect:/product?id=" + productId;
    }

    @GetMapping(value = "/load", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String ajaxLoadMoreProducts(
            @RequestParam("exists") int amount,
            jakarta.servlet.http.HttpServletRequest request) {

        StringBuilder htmlBuilder = new StringBuilder();
        String contextPath = request.getContextPath();

        Locale localeVN = new Locale("vi", "VN");
        NumberFormat vnFormat = NumberFormat.getInstance(localeVN);

        try {
            List<Product> products = productDAO.getNext4(amount);

            for (Product o : products) {
                String productUrl = contextPath + "/product?id=" + o.getId();
                String photoName = (o.getPhoto() != null && !o.getPhoto().trim().isEmpty()) ? o.getPhoto() : "no-sample.png";
                String formattedPrice = vnFormat.format(o.getPrice());

                htmlBuilder.append("<div class=\"product-count col mb-4\">\r\n")
                        .append("    <div class=\"card h-100 product-card\">\r\n")
                        .append("        <a href=\"").append(productUrl).append("\"> \r\n")
                        .append("            <div class=\"product-img-wrapper\">\r\n")
                        .append("                 <img src=\"").append(contextPath).append("/image/product/").append(photoName).append("\"\r\n")
                        .append("                      onerror=\"this.src='").append(contextPath).append("/image/product/no-sample.png';\" />\r\n")
                        .append("            </div>\r\n")
                        .append("        </a>\r\n")
                        .append("        <div class=\"card-body p-3 pt-1 text-center d-flex flex-column justify-content-between\">\r\n")
                        .append("            <div class=\"mb-2\">\r\n")
                        .append("                <div class=\"product-title\">\r\n")
                        .append("                    <a href=\"").append(productUrl).append("\" class=\"text-decoration-none text-dark\">\r\n")
                        .append("                        ").append(escapeHtml(o.getName())).append("\r\n")
                        .append("                    </a>\r\n")
                        .append("                </div>\r\n")
                        .append("                <div class=\"d-flex justify-content-center small text-warning gap-0.5\" style=\"font-size: 11px;\">\r\n")
                        .append("                    <i class=\"bi bi-star-fill\"></i><i class=\"bi bi-star-fill\"></i><i class=\"bi bi-star-fill\"></i><i class=\"bi bi-star-fill\"></i><i class=\"bi bi-star-fill\"></i>\r\n")
                        .append("                </div>\r\n")
                        .append("            </div>\r\n")
                        .append("            <div class=\"product-price mb-3\">\r\n")
                        .append("                ").append(formattedPrice).append(" đ\r\n")
                        .append("            </div>\r\n")
                        .append("            <a class=\"btn btn-view-options w-100\" href=\"").append(productUrl).append("\">Thêm mua ngay</a>\r\n")
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

    private String escapeHtml(String input) {
        if (input == null) return null;
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}