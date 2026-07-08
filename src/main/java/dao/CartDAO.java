package dao;

import models.Cart;
import models.CartItem;
import exception.ResourceNotFoundException;
import exception.DatabaseException;
import exception.ValidationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.NoResultException;
import java.util.List;

@Repository
public class CartDAO {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * ✅ Lấy cart theo user ID
     */
    @Transactional(readOnly = true)
    public Cart getCartByUserId(int userId) {
        if (userId <= 0) {
            throw new ValidationException("userId", userId, "User ID must be greater than 0");
        }

        try {
            return entityManager.createQuery("SELECT c FROM Cart c WHERE c.userId = :userId", Cart.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new ResourceNotFoundException("Cart", "userId", userId);
        } catch (Exception e) {
            throw new DatabaseException("SELECT", "Cart", "Failed to get cart by user ID", e);
        }
    }

    /**
     * ✅ Tạo mới cart
     */
    @Transactional
    public void createCart(Cart cart) {
        if (cart == null) {
            throw new ValidationException("cart", null, "Cart object cannot be null");
        }
        if (cart.getUserId() <= 0) {
            throw new ValidationException("userId", cart.getUserId(), "User ID must be greater than 0");
        }

        try {
            entityManager.persist(cart);
        } catch (Exception e) {
            throw new DatabaseException("INSERT", "Cart", "Failed to create cart", e);
        }
    }

    /**
     * ✅ Cập nhật cart - dùng merge thay vì delete+insert
     */
    @Transactional
    public void updateCart(Cart cart) {
        if (cart == null || cart.getCartId() <= 0) {
            throw new ValidationException("cart", cart, "Valid cart is required");
        }

        try {
            // 1. Xóa items không còn trong cart
            List<CartItem> dbItems = getCartItems(cart.getCartId());
            for (CartItem dbItem : dbItems) {
                if (cart.getItems() == null || !cart.getItems().containsKey(dbItem.getProductId())) {
                    CartItem managedItem = entityManager.merge(dbItem);
                    entityManager.remove(managedItem);
                }
            }

            // 2. Merge hoặc insert items mới
            if (cart.getItems() != null && !cart.getItems().isEmpty()) {
                for (CartItem item : cart.getItems().values()) {
                    item.setCartId(cart.getCartId());
                    entityManager.merge(item);
                }
            }
        } catch (Exception e) {
            throw new DatabaseException("UPDATE", "Cart", "Failed to update cart", e);
        }
    }

    /**
     * ✅ Lấy danh sách items trong cart
     */
    @Transactional(readOnly = true)
    public List<CartItem> getCartItems(int cartId) {
        if (cartId <= 0) {
            throw new ValidationException("cartId", cartId, "Cart ID must be greater than 0");
        }

        try {
            return entityManager.createQuery("SELECT ci FROM CartItem ci WHERE ci.cartId = :cartId", CartItem.class)
                    .setParameter("cartId", cartId)
                    .getResultList();
        } catch (Exception e) {
            throw new DatabaseException("SELECT", "CartItem", "Failed to get cart items", e);
        }
    }

    /**
     * ✅ Xóa toàn bộ items trong cart
     */
    @Transactional
    public void clearCart(int cartId) {
        if (cartId <= 0) {
            throw new ValidationException("cartId", cartId, "Cart ID must be greater than 0");
        }

        try {
            entityManager.createQuery("DELETE FROM CartItem ci WHERE ci.cartId = :cartId")
                    .setParameter("cartId", cartId)
                    .executeUpdate();
        } catch (Exception e) {
            throw new DatabaseException("DELETE", "CartItem", "Failed to clear cart", e);
        }
    }

    /**
     * ✅ Xóa một item khỏi cart
     */
    @Transactional
    public boolean removeCartItem(int cartId, int productId) {
        if (cartId <= 0) {
            throw new ValidationException("cartId", cartId, "Cart ID must be greater than 0");
        }
        if (productId <= 0) {
            throw new ValidationException("productId", productId, "Product ID must be greater than 0");
        }

        try {
            int rowsDeleted = entityManager.createQuery("DELETE FROM CartItem ci WHERE ci.cartId = :cartId AND ci.productId = :productId")
                    .setParameter("cartId", cartId)
                    .setParameter("productId", productId)
                    .executeUpdate();
            return rowsDeleted > 0;
        } catch (Exception e) {
            throw new DatabaseException("DELETE", "CartItem", "Failed to remove cart item", e);
        }
    }
}