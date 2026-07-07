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

    public Cart getCartByUserId(int userId) {
        try {
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

    // ✅ FIX: Dùng merge() thay vì delete+insert
    public void updateCart(Cart cart) {
        // 1. Xóa items không còn trong cart.getItems()
        List<CartItem> dbItems = getCartItems(cart.getCartId());
        for (CartItem dbItem : dbItems) {
            if (!cart.getItems().containsKey(dbItem.getProductId())) {
                entityManager.remove(entityManager.merge(dbItem));
            }
        }

        // 2. Merge items còn lại
        if (cart.getItems() != null && !cart.getItems().isEmpty()) {
            for (CartItem item : cart.getItems().values()) {
                item.setCartId(cart.getCartId());
                entityManager.merge(item);
            }
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