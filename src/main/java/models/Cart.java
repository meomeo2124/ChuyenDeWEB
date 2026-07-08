package models;

import jakarta.persistence.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Entity
@Table(name = "`cart`")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CartId")
    private int cartId;

    @Column(name = "UserId")
    private int userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId", insertable = false, updatable = false)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @MapKey(name = "productId")
    private Map<Integer, CartItem> items = new ConcurrentHashMap<>();

    public Cart() {
    }

    public Cart(int cartId, int userId) {
        this.cartId = cartId;
        this.userId = userId;
    }

    public synchronized void addItem(Product product, int quantity) {
        if (product == null) throw new IllegalArgumentException("Product cannot be null.");
        if (quantity <= 0 || quantity > product.getStock()) throw new IllegalArgumentException("Invalid quantity.");

        CartItem item = items.get(product.getId());
        if (item == null) {
            item = new CartItem(product, quantity);
            item.setCartId(this.cartId);
            item.setCart(this);
            items.put(product.getId(), item);
        } else {
            item.setQuantity(item.getQuantity() + quantity);
        }
    }

    public synchronized void updateQuantity(int productId, int quantity) {
        CartItem item = items.get(productId);
        if (item != null) {
            Product product = item.getProduct();
            if (product != null && quantity > 0 && quantity <= product.getStock()) {
                item.setQuantity(quantity);
            }
        }
    }

    public synchronized void removeItem(int productId) { items.remove(productId); }
    public double getTotalPrice() { return items.values().stream().mapToDouble(CartItem::getTotalPrice).sum(); }

    public int getCartId() { return cartId; }
    public int getUserId() { return userId; }
    public void setCartId(int cartId) { this.cartId = cartId; }
    public void setUserId(int userId) { this.userId = userId; }
    public Map<Integer, CartItem> getItems() { return items; }
    public void setItems(Map<Integer, CartItem> items) { this.items = items; }
    public void clearCart() { items.clear(); }
    public boolean containsProduct(int productId) { return items.containsKey(productId); }
    public int getId() { return cartId; }
    public Product lookup(int productId) { CartItem item = items.get(productId); return item != null ? item.getProduct() : null; }
    public int getQuantity(int productId) { CartItem item = items.get(productId); return item != null ? item.getQuantity() : 0; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}