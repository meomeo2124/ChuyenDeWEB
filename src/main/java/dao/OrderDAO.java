package dao;

import models.CartItem;
import models.Order;
import models.OrderItem;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class OrderDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public int createOrder(int userId, double totalPrice, String paymentMethod) {
        Order order = new Order();
        order.setUserId(userId);
        order.setTotalPrice(totalPrice);
        order.setStatus("PENDING");
        order.setOrderDate(new Timestamp(System.currentTimeMillis()));
        order.setPaymentMethod(paymentMethod != null ? paymentMethod : "CASH");

        entityManager.persist(order);
        return order.getId(); // Khóa chính tự sinh sau khi persist()
    }

    public void saveOrderDetails(int orderId, Map<Integer, CartItem> cartItems) {
        for (CartItem item : cartItems.values()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(orderId);
            orderItem.setProduct(item.getProduct());
            orderItem.setProductName(item.getProduct().getName());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(item.getProduct().getPrice());

            entityManager.persist(orderItem);
        }
    }

    public List<Order> getOrdersBySubsets(String startDate, String endDate) {
        return entityManager.createQuery(
                        "SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate ORDER BY o.orderDate DESC", Order.class)
                .setParameter("startDate", Timestamp.valueOf(startDate))
                .setParameter("endDate", Timestamp.valueOf(endDate))
                .getResultList();
    }

    public List<Order> getAllOrdersWithUser() {
        return entityManager.createQuery("SELECT o FROM Order o ORDER BY o.orderDate DESC", Order.class).getResultList();
    }

    public void updateOrderStatusAndPayment(int orderId, String status) {
        Order order = entityManager.find(Order.class, orderId);
        if (order != null) {
            order.setStatus(status);
            entityManager.merge(order);
        }

        // Cập nhật trạng thái thanh toán đồng bộ
        entityManager.createQuery("UPDATE Payment p SET p.status = :status WHERE p.orderId = :orderId")
                .setParameter("status", status)
                .setParameter("orderId", orderId)
                .executeUpdate();
    }

    public Order getOrderById(int orderId) {
        return entityManager.find(Order.class, orderId);
    }
}