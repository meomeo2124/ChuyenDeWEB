package models;

import jakarta.persistence.*;

@Entity
@Table(name = "`product`")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "product_name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "images")
    private String photo;

    @Column(name = "price")
    private double price;

    @Column(name = "stock")
    private int stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "discount")
    private double discount;

    // Các trường mới do bạn của bạn thêm vào (Flash Sale)
    @Column(name = "is_flash_sale")
    private int isFlashSale;

    @Column(name = "flash_sale_discount")
    private int flashSaleDiscount;

    public Product() {}

    public Product(int id, String name, String description, String photo,
                   double price, double discount, int stock, Category category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.photo = photo;
        this.price = price;
        this.discount = discount;
        this.stock = stock;
        this.category = category;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public int getIsFlashSale() { return isFlashSale; }
    public void setIsFlashSale(int isFlashSale) { this.isFlashSale = isFlashSale; }
    public int getFlashSaleDiscount() { return flashSaleDiscount; }
    public void setFlashSaleDiscount(int flashSaleDiscount) { this.flashSaleDiscount = flashSaleDiscount; }
}