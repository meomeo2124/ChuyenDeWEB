package models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "`users`")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "username")
	private String username;

	@Column(name = "password")
	private String password;

	@Column(name = "email")
	private String email;

	@Column(name = "address")
	private String address;

	@Column(name = "phone_number")
	private String phone;

	@Column(name = "images")
	private String Img = "image/avatars/default-avatar.png";

	@Column(name = "is_admin")
	private boolean isAdmin;

	@Column(name = "google_id")
	private String googleId;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Cart cart;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Order> orders = new ArrayList<>();

	@Transient
	private ArrayList<Product> FavoriteProducts;

	public User() {}

	public void setName(String name) { this.username = name; }
	public String getGoogleId() { return googleId; }
	public void setGoogleId(String googleId) { this.googleId = googleId; }
	public void setImg(String path) { this.Img = path; }
	public String getImg() { return this.Img; }
	public boolean getIsAdmin() { return isAdmin; }
	public void setIsAdmin(boolean isAdmin) { this.isAdmin = isAdmin; }
	public boolean isAdmin() { return this.isAdmin; }
	public boolean isAdmin(User user) { return user.getEmail().equals("admin@example.com"); }
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getAddress() { return address; }
	public void setAddress(String address) { this.address = address; }
	public String getPhone() { return phone; }
	public void setPhone(String phone) { this.phone = phone; }
	public void setFavoriteProducts(ArrayList<Product> arrayList) { this.FavoriteProducts = arrayList; }

	public Cart getCart() { return cart; }
	public void setCart(Cart cart) { this.cart = cart; }
	public List<Order> getOrders() { return orders; }
	public void setOrders(List<Order> orders) { this.orders = orders; }

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password + ", email=" + email
				+ ", address=" + address + ", phone=" + phone + ", Img=" + Img + ", isAdmin=" + isAdmin + "]";
	}
}