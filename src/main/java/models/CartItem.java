package models;

import jakarta.persistence.*;

@Entity
@Table(name = "`dbo.cartitem`")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // Lưu ý quan trọng bên dưới

    @Column(name = "CartId")
    private int cartId;

    @Column(name = "ProductId")
    private int productId;

    @Column(name = "Quantity")
    private int quantity;

    @Transient // Không map vào DB
    private Product product;

    public CartItem() {}

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        if (product != null) {
            this.productId = product.getId();
        }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCartId() { return cartId; }
    public void setCartId(int cartId) { this.cartId = cartId; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) {
        this.product = product;
        if(product != null) this.productId = product.getId();
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getTotalPrice() {
        return (product != null) ? (product.getPrice() * quantity) : 0;
    }

    public boolean isValidQuantity(int q) {
        return q > 0;
    }
}