package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

@Repository
public class PaymentDAO {

    private final DataSource dataSource;

    @Autowired
    public PaymentDAO(DataSource dataSource) {
        this.dataSource = dataSource;
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
                        throw new SQLException("Creating order failed, no ID obtained.");
                    }
                }
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw new SQLException("Error creating order: " + e.getMessage(), e);
            }
        }
        return orderId;
    }

    public void saveOrderDetails(int orderId, Map<Integer, CartItem> cartItems) throws SQLException {
        String insertOrderDetailSQL = "INSERT INTO `dbo.order_details` (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(insertOrderDetailSQL)) {
                for (CartItem item : cartItems.values()) {
                    pstmt.setInt(1, orderId);
                    pstmt.setInt(2, item.getProduct().getId());
                    pstmt.setInt(3, item.getQuantity());
                    pstmt.setDouble(4, item.getProduct().getPrice());
                    pstmt.executeUpdate();
                }
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw new SQLException("Error saving order details: " + e.getMessage(), e);
            }
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
                    order.setPaymentMethod("CASH"); // Giá trị dự phòng
                    order.setStatus(rsOrder.getString("status"));
                    order.setOrderDate(rsOrder.getTimestamp("created_at"));

                    pstmtItems.setInt(1, orderId);
                    try (ResultSet rsItems = pstmtItems.executeQuery()) {
                        List<OrderItem> items = new ArrayList<>();
                        while (rsItems.next()) {
                            OrderItem item = new OrderItem();
                            item.setProductId(rsItems.getInt("product_id"));
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
        }
    }
}