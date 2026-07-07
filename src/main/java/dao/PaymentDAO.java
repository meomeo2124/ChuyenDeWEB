package dao;

import models.CartItem;
import models.Order;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;

@Repository
@Transactional
public class PaymentDAO {

    @Autowired
    private OrderDAO orderDAO;

    public int createOrder(int userId, double totalPrice, String paymentMethod) {
        return orderDAO.createOrder(userId, totalPrice, paymentMethod);
    }

    public void saveOrderDetails(int orderId, Map<Integer, CartItem> cartItems) {
        orderDAO.saveOrderDetails(orderId, cartItems);
    }

    public Order getOrderById(int orderId) {
        return orderDAO.getOrderById(orderId);
    }
}