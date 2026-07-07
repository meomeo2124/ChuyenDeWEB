package controller.web;

import dao.ProductDAO;
import models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
    @Autowired
    public ProductController(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    @GetMapping("/product")
    public String showProductDetail(
            @RequestParam(value = "id", required = false) String productIdStr,
            Model model,
            RedirectAttributes redirectAttributes) {

        String errorMessage;

        if (productIdStr == null || productIdStr.trim().isEmpty()) {
            errorMessage = "Product ID is required";
            return redirectToHomepageWithError(errorMessage, redirectAttributes);
        }

        int id;
        try {
            id = Integer.parseInt(productIdStr.trim());
            if (id <= 0) {
                errorMessage = "Invalid product ID";
                return redirectToHomepageWithError(errorMessage, redirectAttributes);
            }
        } catch (NumberFormatException e) {
            errorMessage = "Invalid product ID format";
            return redirectToHomepageWithError(errorMessage, redirectAttributes);
        }

        try {
            Product product = productDAO.getProductById(id);

            if (product == null) {
                errorMessage = "Product not found";
                return redirectToHomepageWithError(errorMessage, redirectAttributes);
            }

            List<Product> productList = productDAO.getAllProducts();

            model.addAttribute("product", product);
            model.addAttribute("productList", productList);

            return "product-detail";

        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "An error occurred: " + e.getMessage();
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
                        .append("                <span class=\"text-primary fw-bold border-top pt-2 d-inline-block w-100\">\r\n")
                        .append("                    ").append(formattedPrice).append(" VNĐ\r\n")
                        .append("                </span>\r\n")
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

    private String escapeHtml(String input) {
        if (input == null) return null;
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}