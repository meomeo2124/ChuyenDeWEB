package dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import models.CartItem;
import models.Order;
import models.OrderItem;
import exception.DatabaseException;
import exception.ResourceNotFoundException;
import exception.ValidationException;

@Repository
public class OrderDAO {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Chức năng do bạn của bạn code: Kiểm tra xem user đã mua SP này chưa bằng JPA
     */
    @Transactional(readOnly = true)
    public boolean hasUserPurchasedProduct(int userId, int productId) {
        String jpql = "SELECT COUNT(o) FROM Order o JOIN o.items od " +
                "WHERE o.userId = :userId AND od.product.id = :productId AND o.status = 'PAID'";
        try {
            Long count = entityManager.createQuery(jpql, Long.class)
                    .setParameter("userId", userId)
                    .setParameter("productId", productId)
                    .getSingleResult();
            return count != null && count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public int createOrder(int userId, double totalPrice, String paymentMethod) {
        if (userId <= 0) throw new ValidationException("userId", userId, "User ID must be greater than 0");
        if (totalPrice <= 0) throw new ValidationException("totalPrice", totalPrice, "Total price must be greater than 0");

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

    @Transactional
    public void saveOrderDetails(int orderId, Map<Integer, CartItem> cartItems) {
        if (orderId <= 0) throw new ValidationException("orderId", orderId, "Order ID must be greater than 0");
        if (cartItems == null || cartItems.isEmpty()) throw new ValidationException("cartItems", cartItems, "Cart items cannot be empty");

        try {
            for (CartItem item : cartItems.values()) {
                if (item.getProduct() == null) throw new ValidationException("product", null, "Product cannot be null");

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

    @Transactional(readOnly = true)
    public List<Order> getOrdersBySubsets(String startDate, String endDate) {
        try {
            return entityManager.createQuery(
                            "SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate ORDER BY o.orderDate DESC", Order.class)
                    .setParameter("startDate", Timestamp.valueOf(startDate))
                    .setParameter("endDate", Timestamp.valueOf(endDate))
                    .getResultList();
        } catch (Exception e) {
            throw new DatabaseException("SELECT", "Order", "Failed to get orders by date range", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrdersWithUser() {
        try {
            return entityManager.createQuery("SELECT o FROM Order o ORDER BY o.orderDate DESC", Order.class).getResultList();
        } catch (Exception e) {
            throw new DatabaseException("SELECT", "Order", "Failed to get all orders", e);
        }
    }

    @Transactional
    public void updateOrderStatusAndPayment(int orderId, String status) {
        try {
            Order order = entityManager.find(Order.class, orderId);
            if (order == null) throw new ResourceNotFoundException("Order", "id", orderId);

            order.setStatus(status);
            entityManager.merge(order);

            entityManager.createNativeQuery("UPDATE payment SET status = ?1 WHERE order_id = ?2")
                    .setParameter(1, status)
                    .setParameter(2, orderId)
                    .executeUpdate();
        } catch (Exception e) {
            throw new DatabaseException("UPDATE", "Order", "Failed to update order status", e);
        }
    }

    @Transactional(readOnly = true)
    public Order getOrderById(int orderId) {
        Order order = entityManager.find(Order.class, orderId);
        if (order == null) throw new ResourceNotFoundException("Order", "id", orderId);
        // Force initialize lazy collections if needed for mapping
        order.getItems().size();
        return order;
    }
}