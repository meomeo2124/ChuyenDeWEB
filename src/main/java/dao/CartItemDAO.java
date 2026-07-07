package dao;

import models.Cart;
import models.CartItem;
import models.Product;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.NoResultException;

@Repository
@Transactional
public class CartItemDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public boolean addCartItem(int cartId, int productId, int quantity) {
        if (cartId <= 0) return false;
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than 0");

        Product product = entityManager.find(Product.class, productId);
        if (product == null || quantity > product.getStock()) throw new IllegalArgumentException("Not enough stock");

        CartItem item = new CartItem();
        item.setCartId(cartId);
        item.setProductId(productId);
        item.setQuantity(quantity);

        entityManager.persist(item);
        return true;
    }

    public boolean addCartItem(Cart cart, Product product, int quantity) {
        if (cart.getCartId() <= 0) return false;
        if (quantity <= 0 || quantity > product.getStock()) {
            throw new IllegalArgumentException("Invalid quantity or not enough stock");
        }

        CartItem item = new CartItem(product, quantity);
        item.setCartId(cart.getCartId());
        entityManager.persist(item);

        cart.getItems().put(product.getId(), item);
        return true;
    }

    public void setQuantity(Cart cart, Product product, int quantity) {
        if (cart.getCartId() <= 0) return;
        if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative");
        if (quantity > product.getStock()) throw new IllegalArgumentException("Not enough stock");

        try {
            CartItem item = entityManager.createQuery("SELECT ci FROM CartItem ci WHERE ci.cartId = :cartId AND ci.productId = :productId", CartItem.class)
                    .setParameter("cartId", cart.getCartId())
                    .setParameter("productId", product.getId())
                    .getSingleResult();

            item.setQuantity(quantity);
            entityManager.merge(item);

            if (cart.getItems().containsKey(product.getId())) {
                cart.getItems().get(product.getId()).setQuantity(quantity);
            }
        } catch (NoResultException e) {
            // Không tìm thấy phần tử để cập nhật
        }
    }

    public int getQuantity(Cart cart, Product product) {
        try {
            CartItem item = entityManager.createQuery("SELECT ci FROM CartItem ci WHERE ci.cartId = :cartId AND ci.productId = :productId", CartItem.class)
                    .setParameter("cartId", cart.getCartId())
                    .setParameter("productId", product.getId())
                    .getSingleResult();
            return item.getQuantity();
        } catch (NoResultException e) {
            return 0;
        }
    }
}