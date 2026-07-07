package dao;

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