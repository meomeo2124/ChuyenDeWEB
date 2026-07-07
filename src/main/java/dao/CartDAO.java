package dao;

import models.Cart;
import models.CartItem;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.NoResultException;
import java.util.List;

@Repository
@Transactional
public class CartDAO {

    @PersistenceContext
    private EntityManager entityManager;

    // Trong CartDAO.java
    public Cart getCartByUserId(int userId) {
        try {
            // Thêm alias 'c' và đảm bảo 'Cart' là tên Class Entity
            return entityManager.createQuery("SELECT c FROM Cart c WHERE c.userId = :userId", Cart.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void createCart(Cart cart) {
        entityManager.persist(cart);
    }

    public void updateCart(Cart cart) {
        // Xóa sạch CartItem cũ của Cart này
        entityManager.createQuery("DELETE FROM CartItem ci WHERE ci.cartId = :cartId")
                .setParameter("cartId", cart.getCartId())
                .executeUpdate();

        // Thêm loạt CartItem mới vào database
        for (CartItem item : cart.getItems().values()) {
            item.setCartId(cart.getCartId());
            entityManager.persist(item);
        }
    }

    public List<CartItem> getCartItems(int cartId) {
        return entityManager.createQuery("SELECT ci FROM CartItem ci WHERE ci.cartId = :cartId", CartItem.class)
                .setParameter("cartId", cartId)
                .getResultList();
    }

    public void clearCart(int cartId) {
        entityManager.createQuery("DELETE FROM CartItem ci WHERE ci.cartId = :cartId")
                .setParameter("cartId", cartId)
                .executeUpdate();
    }

    public boolean removeCartItem(int cartId, int productId) {
        int rowsDeleted = entityManager.createQuery("DELETE FROM CartItem ci WHERE ci.cartId = :cartId AND ci.productId = :productId")
                .setParameter("cartId", cartId)
                .setParameter("productId", productId)
                .executeUpdate();
        return rowsDeleted > 0;
    }
}