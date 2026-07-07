package controller.web;

import dao.CartDAO;
import dao.CartItemDAO;
import dao.ProductDAO;
import jakarta.servlet.http.HttpSession;
import models.Cart;
import models.Product;
import models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CartController {

    private final CartDAO cartDAO;
    private final CartItemDAO cartItemDAO;
    private final ProductDAO productDAO;


    @Autowired
    public CartController(CartDAO cartDAO, CartItemDAO cartItemDAO, ProductDAO productDAO) {
        this.cartDAO = cartDAO;
        this.cartItemDAO = cartItemDAO;
        this.productDAO = productDAO;
    }


    // 1. HIỂN THỊ GIỎ HÀNG
    @GetMapping("/secure/cart")
    public String showCartPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            System.out.println("Fetching the cart for userId: " + user.getId());
            // 🌟 3. Gọi trực tiếp từ Spring Bean thay vì mở Connection thủ công
            Cart cart = getOrCreateCart(user.getId());

            if (cart != null) {
                if (cart.getItems().isEmpty()) {
                    System.out.println("Cart is empty for userId: " + user.getId());
                    model.addAttribute("errorMessage", "Your cart is empty.");
                }
                session.setAttribute("cart", cart);
            } else {
                model.addAttribute("errorMessage", "Failed to load cart.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error processing request.");
        }

        return "secure/cart";
    }


    // 2. THÊM VÀO GIỎ HÀNG
    @PostMapping("/addToCart")
    public String handleAddToCartForm(
            @RequestParam("productId") int productId,
            @RequestParam(value = "inputQuantity", required = false, defaultValue = "1") int quantity,
            HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return redirectToLoginWithMsg();
        }

        try {
            if (quantity <= 0) {
                throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
            }

            // 🌟 4. Loại bỏ hoàn toàn khối mở Connection và các từ khóa new DAO() rác
            Product product = productDAO.getProductById(productId);
            if (product == null) throw new IllegalArgumentException("Sản phẩm không tồn tại");
            if (quantity > product.getStock()) throw new IllegalArgumentException("Không đủ hàng trong kho.");

            Cart cart = getOrCreateCart(user.getId());

            if (cart.getItems().containsKey(productId)) {
                int currentQuantity = cartItemDAO.getQuantity(cart, product);
                int newQuantity = currentQuantity + quantity;
                if (newQuantity > product.getStock()) throw new IllegalArgumentException("Tổng số lượng vượt quá tồn kho.");
                cartItemDAO.setQuantity(cart, product, newQuantity);
            } else {
                cartItemDAO.addCartItem(cart, product, quantity);
            }

            cart = cartDAO.getCartByUserId(user.getId());
            session.setAttribute("cart", cart);

            String successMsg = URLEncoder.encode("Thêm vào giỏ hàng thành công", StandardCharsets.UTF_8.toString());
            return "redirect:/secure/cart?success=" + successMsg;

        } catch (Exception e) {
            e.printStackTrace();
            return redirectToProductDetailWithError(productId, e.getMessage());
        }
    }

    // 3. XÓA ITEM KHỎI GIỎ HÀNG
    @PostMapping(value = "/secure/cart", params = "action=removeItem")
    @ResponseBody
    public Map<String, Object> ajaxRemoveItem(@RequestParam("id") int productId, HttpSession session) {
        Map<String, Object> responseJson = new HashMap<>();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            responseJson.put("success", false);
            responseJson.put("message", "Please log in.");
            return responseJson;
        }

        try {
            Cart cart = getOrCreateCart(user.getId());
            cartDAO.removeCartItem(cart.getCartId(), productId);

            cart = cartDAO.getCartByUserId(user.getId());
            double subtotal = cart.getTotalPrice();
            double shipping = (subtotal > 0) ? 15000.00 : 0.00;
            double total = subtotal + shipping;

            session.setAttribute("cart", cart);

            responseJson.put("success", true);
            responseJson.put("message", "Product removed successfully.");
            responseJson.put("subtotal", subtotal);
            responseJson.put("shipping", shipping);
            responseJson.put("total", total);
        } catch (Exception e) {
            e.printStackTrace();
            responseJson.put("success", false);
            responseJson.put("message", "Database error: " + e.getMessage());
        }
        return responseJson;
    }

    // 4. CẬP NHẬT SỐ LƯỢNG
    @PostMapping(value = "/secure/cart", params = "action=updateQuantity")
    @ResponseBody
    public Map<String, Object> ajaxUpdateQuantity(
            @RequestParam("productId") int productId,
            @RequestParam("quantity") int quantity,
            HttpSession session) {

        Map<String, Object> responseJson = new HashMap<>();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            responseJson.put("success", false);
            responseJson.put("message", "Please log in.");
            return responseJson;
        }

        if (quantity < 0) {
            responseJson.put("success", false);
            responseJson.put("message", "Quantity cannot be negative.");
            return responseJson;
        }

        try {
            Cart cart = getOrCreateCart(user.getId());
            Product product = productDAO.getProductById(productId);

            if (product == null || quantity > product.getStock()) {
                responseJson.put("success", false);
                responseJson.put("message", "Not enough stock.");
                return responseJson;
            }

            cartItemDAO.setQuantity(cart, product, quantity);

            cart = cartDAO.getCartByUserId(user.getId());
            double subtotal = cart.getTotalPrice();
            double shipping = (subtotal > 0) ? 15000.00 : 0.00;
            double total = subtotal + shipping;

            session.setAttribute("cart", cart);

            responseJson.put("success", true);
            responseJson.put("message", "Quantity updated successfully.");
            responseJson.put("subtotal", subtotal);
            responseJson.put("shipping", shipping);
            responseJson.put("total", total);
        } catch (Exception e) {
            e.printStackTrace();
            responseJson.put("success", false);
            responseJson.put("message", "Error: " + e.getMessage());
        }
        return responseJson;
    }
    private Cart getOrCreateCart(int userId) throws Exception {
        Cart cart = cartDAO.getCartByUserId(userId);
        if (cart == null) {
            System.out.println("Cart not found for userId: " + userId + ". Creating new cart.");
            cart = new Cart(0, userId);
            cartDAO.createCart(cart);
            cart = cartDAO.getCartByUserId(userId);
            if (cart == null || cart.getCartId() <= 0) {
                throw new Exception("Failed to create cart for userId: " + userId);
            }
        }
        return cart;
    }

    private String redirectToProductDetailWithError(int productId, String rawMessage) {
        try {
            String encodedError = URLEncoder.encode(rawMessage, StandardCharsets.UTF_8.toString());
            return "redirect:/product?id=" + productId + "&error=" + encodedError;
        } catch (Exception e) {
            return "redirect:/product?id=" + productId + "&error=system_error";
        }
    }

    private String redirectToLoginWithMsg() {
        try {
            String errorMsg = URLEncoder.encode("Vui lòng đăng nhập", StandardCharsets.UTF_8.toString());
            return "redirect:/login?error=" + errorMsg;
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    @GetMapping(value = "/loadCart", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String ajaxLoadCartHtml(HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            return "<p class='text-center'>Giỏ hàng trống hoặc phiên làm việc đã hết hạn.</p>";
        }

        StringBuilder htmlBuilder = new StringBuilder();

        try {
            java.util.List<models.CartItem> list = cartDAO.getCartItems(cart.getCartId());

            for (models.CartItem ci : list) {
                // Định dạng hiển thị dấu phân tách hàng nghìn cho tiền Việt
                String formattedPrice = String.format("%,.0f", ci.getProduct().getPrice());

                htmlBuilder.append("<div class=\"row cart-item mb-3\">\r\n")
                        .append("    <div class=\"col-md-3\">\r\n")
                        .append("        <img src=\"").append(session.getServletContext().getContextPath()).append("/image/product/").append(ci.getProduct().getPhoto()).append("\"\r\n")
                        .append("             alt=\"").append(ci.getProduct().getName()).append("\" class=\"img-fluid rounded\">\r\n")
                        .append("    </div>\r\n")
                        .append("    <div class=\"col-md-5\">\r\n")
                        .append("        <h5 class=\"card-title\">").append(ci.getProduct().getName()).append("</h5>\r\n")
                        .append("        <p class=\"text-muted\">Category: ").append(ci.getProduct().getName()).append("</p>\r\n")
                        .append("    </div>\r\n")
                        .append("    <div class=\"col-md-2\">\r\n")
                        .append("        <div class=\"input-group\">\r\n")
                        .append("            <button class=\"btn btn-outline-secondary btn-sm\" type=\"button\" onclick=\"changeQuantity(-1, this)\">-</button>\r\n")
                        .append("            <input style=\"max-width: 100px\" type=\"number\"\r\n")
                        .append("                   class=\"form-control form-control-sm text-center quantity-input\"\r\n")
                        .append("                   onchange=\"updateQuantity()\"\r\n")
                        .append("                   value=\"").append(ci.getQuantity()).append("\" min=\"1\"/>\r\n")
                        .append("            <button class=\"btn btn-outline-secondary btn-sm\" type=\"button\" onclick=\"changeQuantity(1, this)\">+</button> \r\n")
                        .append("        </div>\r\n")
                        .append("    </div>\r\n")
                        .append("    <div class=\"col-md-2 text-end\">\r\n")
                        .append("        <p class=\"fw-bold\">").append(formattedPrice).append(" VNĐ</p>\r\n")
                        .append("        <button class=\"btn btn-sm btn-outline-danger\" onclick=\"removeItem(").append(ci.getProduct().getId()).append(")\"> \r\n")
                        .append("            <i class=\"bi bi-trash\"></i>\r\n")
                        .append("        </button>\r\n")
                        .append("    </div>\r\n")
                        .append("</div>\r\n")
                        .append("<hr>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "<p class='text-danger text-center'>Lỗi tải dữ liệu giỏ hàng.</p>";
        }

        return htmlBuilder.toString();
    }
}