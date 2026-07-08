package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import models.CartItem;
import models.Order;
import models.OrderItem;
import exception.ResourceNotFoundException;
import exception.DatabaseException;
import exception.ValidationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.sql.Timestamp;

@Repository
public class OrderDAO {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * ✅ Tạo order mới
     */
    @Transactional
    public int createOrder(int userId, double totalPrice, String paymentMethod) {
        if (userId <= 0) {
            throw new ValidationException("userId", userId, "User ID must be greater than 0");
        }
        if (totalPrice <= 0) {
            throw new ValidationException("totalPrice", totalPrice, "Total price must be greater than 0");
        }

    public boolean hasUserPurchasedProduct(int userId, int productId) {
        String sql = "SELECT COUNT(*) FROM `dbo.orders` o " +
                "JOIN `dbo.order_details` od ON o.id = od.order_id " +
                "WHERE o.user_id = ? AND od.product_id = ? AND o.status = 'PAID'";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Trả về true nếu người dùng đã từng mua thành công món này
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int createOrder(int userId, double totalPrice, String paymentMethod) throws SQLException {
        String insertOrderSQL = "INSERT INTO `dbo.orders` (user_id, total, status, created_at) VALUES (?, ?, ?, ?)";
        int orderId = 0;

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(insertOrderSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, userId);
                pstmt.setDouble(2, totalPrice);
                pstmt.setString(3, "PENDING");
                pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        orderId = rs.getInt(1);
                    } else {
                        throw new SQLException("Tạo hóa đơn thất bại, không lấy được ID.");
                    }
                }
                conn.commit();
                return orderId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        try {
            Order order = new Order();
            order.setUserId(userId);
            order.setTotalPrice(totalPrice);
            order.setStatus("PENDING");
            order.setOrderDate(new Date());
            order.setPaymentMethod(paymentMethod != null ? paymentMethod : "CASH");

            entityManager.persist(order);
            return order.getId();
        } catch (Exception e) {
            throw new DatabaseException("INSERT", "Order", "Failed to create order", e);
        }
    }

    /**
     * ✅ Lưu chi tiết order
     */
    @Transactional
    public void saveOrderDetails(int orderId, Map<Integer, CartItem> cartItems) {
        if (orderId <= 0) {
            throw new ValidationException("orderId", orderId, "Order ID must be greater than 0");
        }
        if (cartItems == null || cartItems.isEmpty()) {
            throw new ValidationException("cartItems", cartItems, "Cart items cannot be empty");
        }

        try {
            for (CartItem item : cartItems.values()) {
                if (item.getProduct() == null) {
                    throw new ValidationException("product", null, "Product cannot be null in cart item");
                }

                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(orderId);
                orderItem.setProduct(item.getProduct());
                orderItem.setProductName(item.getProduct().getName());
                orderItem.setQuantity(item.getQuantity());
                orderItem.setPrice(item.getProduct().getPrice());

                entityManager.persist(orderItem);
            }
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("INSERT", "OrderItem", "Failed to save order details", e);
        }
    }

    public List<Order> getOrdersBySubsets(String startDate, String endDate) throws SQLException {
        List<Order> orderList = new ArrayList<>();

        String sql = "SELECT o.id, o.user_id, o.total, o.status, o.created_at, u.username " +
                "FROM `dbo.orders` o " +
                "JOIN `dbo.users` u ON o.user_id = u.id " +
                "WHERE o.created_at BETWEEN ? AND ? " +
                "ORDER BY o.created_at DESC";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, startDate);
            ps.setString(2, endDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order();
                    order.setId(rs.getInt("id"));
                    order.setUserId(rs.getInt("user_id"));
                    order.setTotalPrice(rs.getDouble("total"));
                    order.setStatus(rs.getString("status"));
                    order.setOrderDate(rs.getTimestamp("created_at"));
                    order.setShippingAddress(rs.getString("username"));
                    orderList.add(order);
                }
            }
    /**
     * ✅ Lấy orders theo khoảng thời gian
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersBySubsets(String startDate, String endDate) {
        if (startDate == null || startDate.trim().isEmpty()) {
            throw new ValidationException("startDate", startDate, "Start date cannot be empty");
        }
        if (endDate == null || endDate.trim().isEmpty()) {
            throw new ValidationException("endDate", endDate, "End date cannot be empty");
        }

        try {
            return entityManager.createQuery(
                            "SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate ORDER BY o.orderDate DESC", Order.class)
                    .setParameter("startDate", Timestamp.valueOf(startDate))
                    .setParameter("endDate", Timestamp.valueOf(endDate))
                    .getResultList();
        } catch (IllegalArgumentException e) {
            throw new ValidationException("dateFormat", startDate + " or " + endDate, "Invalid date format. Use yyyy-MM-dd HH:mm:ss");
        } catch (Exception e) {
            throw new DatabaseException("SELECT", "Order", "Failed to get orders by date range", e);
        }
    }

    public List<Order> getAllOrdersWithUser() throws SQLException {
        List<Order> orderList = new ArrayList<>();
        String sql = "SELECT o.id, o.user_id, o.total, o.status, o.created_at, u.username FROM `dbo.orders` o JOIN `dbo.users` u ON o.user_id = u.id ORDER BY o.created_at DESC";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setUserId(rs.getInt("user_id"));
                order.setTotalPrice(rs.getDouble("total"));
                order.setStatus(rs.getString("status"));
                order.setOrderDate(rs.getTimestamp("created_at"));
                order.setShippingAddress(rs.getString("username"));
                orderList.add(order);
            }
    /**
     * ✅ Lấy tất cả orders
     */
    @Transactional(readOnly = true)
    public List<Order> getAllOrdersWithUser() {
        try {
            return entityManager.createQuery("SELECT o FROM Order o ORDER BY o.orderDate DESC", Order.class).getResultList();
        } catch (Exception e) {
            throw new DatabaseException("SELECT", "Order", "Failed to get all orders", e);
        }
    }

    /**
     * ✅ Cập nhật status order
     */
    @Transactional
    public void updateOrderStatusAndPayment(int orderId, String status) {
        if (orderId <= 0) {
            throw new ValidationException("orderId", orderId, "Order ID must be greater than 0");
        }
        if (status == null || status.trim().isEmpty()) {
            throw new ValidationException("status", status, "Status cannot be empty");
        }

        try {
            Order order = entityManager.find(Order.class, orderId);
            if (order == null) {
                throw new ResourceNotFoundException("Order", "id", orderId);
            }

            order.setStatus(status);
            entityManager.merge(order);

            // Update payment table
            int rowsUpdated = entityManager.createNativeQuery("UPDATE payment SET status = ?1 WHERE order_id = ?2")
                    .setParameter(1, status)
                    .setParameter(2, orderId)
                    .executeUpdate();

            if (rowsUpdated == 0) {
                throw new ResourceNotFoundException("Payment", "order_id", orderId);
            }
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("UPDATE", "Order", "Failed to update order status", e);
        }
    }

    public Order getOrderById(int orderId) throws SQLException {
        String selectOrderSQL = "SELECT id, user_id, total, status, created_at FROM `dbo.orders` WHERE id = ?";
        String selectItemsSQL = "SELECT od.product_id, p.product_name AS product_name, od.quantity, od.price " +
                "FROM `dbo.order_details` od JOIN `dbo.product` p ON od.product_id = p.id " +
                "WHERE od.order_id = ?";
        Order order = null;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmtOrder = conn.prepareStatement(selectOrderSQL);
             PreparedStatement pstmtItems = conn.prepareStatement(selectItemsSQL)) {

            pstmtOrder.setInt(1, orderId);
            try (ResultSet rsOrder = pstmtOrder.executeQuery()) {
                if (rsOrder.next()) {
                    order = new Order();
                    order.setId(rsOrder.getInt("id"));
                    order.setUserId(rsOrder.getInt("user_id"));
                    order.setTotalPrice(rsOrder.getDouble("total"));
                    order.setPaymentMethod("CASH");
                    order.setStatus(rsOrder.getString("status"));
                    order.setOrderDate(rsOrder.getTimestamp("created_at"));

                    pstmtItems.setInt(1, orderId);
                    try (ResultSet rsItems = pstmtItems.executeQuery()) {
                        List<OrderItem> items = new ArrayList<>();
                        while (rsItems.next()) {
                            OrderItem item = new OrderItem();
                            Product product = new Product();
                            product.setId(rsItems.getInt("product_id"));
                            product.setName(rsItems.getString("product_name"));
                            item.setProduct(product);
                            item.setProductName(rsItems.getString("product_name"));
                            item.setQuantity(rsItems.getInt("quantity"));
                            item.setPrice(rsItems.getDouble("price"));
                            items.add(item);
                        }
                        order.setItems(items);
                    }
                }
            }
            return order;
    /**
     * ✅ Lấy order theo ID
     */
    @Transactional(readOnly = true)
    public Order getOrderById(int orderId) {
        if (orderId <= 0) {
            throw new ValidationException("orderId", orderId, "Order ID must be greater than 0");
        }

        Order order = entityManager.find(Order.class, orderId);
        if (order == null) {
            throw new ResourceNotFoundException("Order", "id", orderId);
        }
        return order;
    }
}