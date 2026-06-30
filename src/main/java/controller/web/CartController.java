package controller.web;

import dao.CartDAO;
import dao.CartItemDAO;
import dao.DBConnectionPool;
import dao.ProductDAO;
import jakarta.servlet.http.HttpSession;
import models.Cart;
import models.Product;
import models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CartController {

    // =========================================================================
    // 1. HIỂN THỊ GIỎ HÀNG (Thay thế cho doGet của CartServlet cũ)
    // =========================================================================
    @GetMapping("/secure/cart")
    public String showCartPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
            System.out.println("Fetching the cart for userId: " + user.getId());
            CartDAO cartDAO = new CartDAO(connection);

            Cart cart = getOrCreateCart(user.getId(), cartDAO);

            if (cart != null) {
                if (cart.getItems().isEmpty()) {
                    System.out.println("Cart is empty for userId: " + user.getId());
                    model.addAttribute("errorMessage", "Your cart is empty.");
                }
                session.setAttribute("cart", cart);
            } else {
                model.addAttribute("errorMessage", "Failed to load cart.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error processing request.");
        }

        // Trỏ thẳng vào WEB-INF/views/secure/cart.jsp (Sau khi bạn đã di chuyển file jsp vào thư mục views)
        return "secure/cart";
    }

    // =========================================================================
    // 2. THÊM VÀO GIỎ HÀNG (Form Submit truyền thống từ trang chi tiết sản phẩm)
    // =========================================================================
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

            try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
                ProductDAO productDAO = new ProductDAO();
                Product product = productDAO.getProductById(productId);
                if (product == null) throw new IllegalArgumentException("Sản phẩm không tồn tại");
                if (quantity > product.getStock()) throw new IllegalArgumentException("Không đủ hàng trong kho.");

                CartDAO cartDAO = new CartDAO(connection);
                Cart cart = getOrCreateCart(user.getId(), cartDAO);
                CartItemDAO cartItemDAO = new CartItemDAO();

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
            }
        } catch (Exception e) {
            e.printStackTrace();
            return redirectToProductDetailWithError(productId, e.getMessage());
        }
    }

    // =========================================================================
    // 3. XÓA ITEM KHỎI GIỎ HÀNG (AJAX - Trả về JSON)
    // =========================================================================
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

        try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
            CartDAO cartDAO = new CartDAO(connection);
            Cart cart = getOrCreateCart(user.getId(), cartDAO);

            cartDAO.removeCartItem(cart.getCartId(), productId);

            // Tải lại giỏ hàng mới nhất để tính toán lại tổng tiền
            cart = cartDAO.getCartByUserId(user.getId());
            double subtotal = cart.getTotalPrice();
            double shipping = (subtotal > 0) ? 10.00 : 0.00;
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

    // =========================================================================
    // 4. CẬP NHẬT SỐ LƯỢNG (AJAX - Trả về JSON)
    // =========================================================================
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

        try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
            CartDAO cartDAO = new CartDAO(connection);
            Cart cart = getOrCreateCart(user.getId(), cartDAO);

            ProductDAO productDAO = new ProductDAO();
            Product product = productDAO.getProductById(productId);

            if (product == null || quantity > product.getStock()) {
                responseJson.put("success", false);
                responseJson.put("message", "Not enough stock.");
                return responseJson;
            }

            CartItemDAO cartItemDAO = new CartItemDAO();
            cartItemDAO.setQuantity(cart, product, quantity);

            cart = cartDAO.getCartByUserId(user.getId());
            double subtotal = cart.getTotalPrice();
            double shipping = (subtotal > 0) ? 10.00 : 0.00;
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

    // =========================================================================
    // HÀM BỔ TRỢ (HELPERS)
    // =========================================================================
    private Cart getOrCreateCart(int userId, CartDAO cartDAO) throws SQLException {
        Cart cart = cartDAO.getCartByUserId(userId);
        if (cart == null) {
            System.out.println("Cart not found for userId: " + userId + ". Creating new cart.");
            cart = new Cart(0, userId);
            cartDAO.createCart(cart);
            cart = cartDAO.getCartByUserId(userId);
            if (cart == null || cart.getCartId() <= 0) {
                throw new SQLException("Failed to create cart for userId: " + userId);
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
}