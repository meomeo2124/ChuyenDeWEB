package models;

import jakarta.persistence.*;

@Entity
@Table(name = "`order_details`")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "order_id")
    private int orderId;

    @Column(name = "product_id")
    private int productId;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "price")
    private double price;

    @Transient // Object product này dùng cho logic code Java, không lưu DB cột này
    private Product product;

    @Transient
    private String productName;

    public OrderItem() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) {
        this.product = product;
        if(product != null) {
            this.productId = product.getId();
        }
    }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
}