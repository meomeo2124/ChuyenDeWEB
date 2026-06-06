package controller.web;

import dao.OrderDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Cart;
import models.Order;
import models.User;

import java.io.IOException;
import java.util.logging.Logger;

@WebServlet("/secure/payment")
public class Payment extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private OrderDAO orderDAO;
    private static final Logger LOGGER = Logger.getLogger(Payment.class.getName());

    @Override
    public void init() throws ServletException {
        orderDAO = new OrderDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        Cart cart = (Cart) session.getAttribute("cart");

        if (user == null) {
            LOGGER.warning("User not logged in, redirecting to login");
            response.sendRedirect(request.getContextPath() + "/login?error=Vui lòng đăng nhập để thanh toán");
            return;
        }

        if (cart == null || cart.getItems().isEmpty()) {
            LOGGER.warning("Cart is empty, redirecting to cart");
            response.sendRedirect(request.getContextPath() + "/secure/cart?error=Giỏ hàng trống");
            return;
        }

        LOGGER.info("doGet called: Forwarding to payment.jsp");
        try {
            request.getRequestDispatcher("/secure/payment.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.severe("Error forwarding to payment.jsp: " + e.getMessage());
            throw new ServletException("Lỗi khi chuyển tiếp đến trang thanh toán", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        Cart cart = (Cart) session.getAttribute("cart");

        if (user == null) {
            LOGGER.warning("User not logged in, redirecting to login");
            response.sendRedirect(request.getContextPath() + "/login?error=Vui lòng đăng nhập để thanh toán");
            return;
        }

        if (cart == null || cart.getItems().isEmpty()) {
            LOGGER.warning("Cart is empty, redirecting to cart");
            response.sendRedirect(request.getContextPath() + "/secure/cart?error=Giỏ hàng trống");
            return;
        }

        String action = request.getParameter("action");
        LOGGER.info("doPost called: action = " + action);

        if ("proceedToPayment".equals(action)) {
            LOGGER.info("Action = proceedToPayment: Forwarding to payment.jsp");
            try {
                request.getRequestDispatcher("/secure/payment.jsp").forward(request, response);
            } catch (Exception e) {
                LOGGER.severe("Error forwarding to payment.jsp: " + e.getMessage());
                throw new ServletException("Lỗi khi chuyển tiếp đến trang thanh toán", e);
            }
        } else if ("pay".equals(action)) {
            LOGGER.info("Action = pay: Processing payment");
            try {
                String paymentMethod = request.getParameter("paymentMethod");
                LOGGER.info("Payment method: " + paymentMethod);
                if (paymentMethod == null || paymentMethod.isEmpty()) {
                    LOGGER.warning("Payment method is empty");
                    response.sendRedirect(request.getContextPath() + "/secure/payment?error=Phương thức thanh toán không được để trống");
                    return;
                }

                double totalPrice = cart.getTotalPrice() + (cart.getTotalPrice() > 0 ? 10.00 : 0.00); // Thêm phí vận chuyển
                int orderId = orderDAO.createOrder(user.getId(), totalPrice, paymentMethod);
                if (orderId > 0) {
                    orderDAO.saveOrderDetails(orderId, cart.getItems());
                    cart.clearCart();
                    session.setAttribute("cart", cart);
                    LOGGER.info("Payment successful: Forwarding to invoice.jsp with orderId = " + orderId);

                    Order order = orderDAO.getOrderById(orderId);
                    request.setAttribute("order", order);
                    request.getRequestDispatcher("/secure/invoice.jsp").forward(request, response);
                } else {
                    LOGGER.warning("Payment failed: Redirecting to payment.jsp with error");
                    response.sendRedirect(request.getContextPath() + "/secure/payment?error=Thanh toán thất bại");
                }
            } catch (Exception e) {
                LOGGER.severe("Payment processing error: " + e.getMessage());
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/secure/payment?error=Thanh toán thất bại: " + e.getMessage());
            }
        } else {
            LOGGER.warning("Invalid action: Redirecting to cart.jsp");
            response.sendRedirect(request.getContextPath() + "/secure/cart?error=Yêu cầu không hợp lệ: action=" + action);
        }
    }
}