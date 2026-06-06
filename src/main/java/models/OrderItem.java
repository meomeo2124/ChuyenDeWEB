package models;

public class OrderItem {
    private int id;
    private int orderId;
    private Product product;
    private String productName;
    private int quantity;
    private double price;

    public OrderItem() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getProductId() { return product != null ? product.getId() : 0; }
    public void setProductId(int productId) {
        if (this.product == null) this.product = new Product();
        this.product.setId(productId);
    }
}