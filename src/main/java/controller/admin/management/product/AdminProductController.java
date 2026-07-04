package controller.admin.management.product;

import dao.CategoryDAO;
import dao.DBConnectionPool;
import dao.ProductDAO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import models.Category;
import models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Controller
@RequestMapping("/admin/product")
public class AdminProductController {

    private static final Logger LOGGER = Logger.getLogger(AdminProductController.class.getName());

    @Autowired
    private ServletContext servletContext;

    // 1. Hiển thị danh sách sản phẩm (Thay thế ManageProductServlet)
    @GetMapping("/manage")
    public String manageProducts(Model model,
                                 @RequestParam(value = "success", required = false) String success,
                                 @RequestParam(value = "error", required = false) String error) {
        try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
            ProductDAO productDAO = new ProductDAO(connection);
            List<Product> productList = productDAO.getAllProducts();

            model.addAttribute("productList", productList);
            if (success != null) model.addAttribute("success", success);
            if (error != null) model.addAttribute("error", error);

            return "forward:/admin/manage_product.jsp";
        } catch (Exception e) {
            LOGGER.severe("Error getting products: " + e.getMessage());
            model.addAttribute("error", "Lỗi kết nối cơ sở dữ liệu khi tải danh sách sản phẩm.");
            return "forward:/admin/manage_product.jsp";
        }
    }

    // 2. Hiển thị Form thêm sản phẩm (Thay thế InsertProductServlet - doGet)
    @GetMapping("/insert")
    public String showInsertForm(Model model, @RequestParam(value = "error", required = false) String error) {
        try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
            ProductDAO productDAO = new ProductDAO(connection);
            // Đồng bộ lấy categoryList từ hàm có sẵn trong DAO của bạn
            model.addAttribute("categoryList", productDAO.getAllCategories());
            if (error != null) model.addAttribute("error", error);
            return "forward:/admin/insert_product.jsp";
        } catch (Exception e) {
            LOGGER.severe("Error loading categories for insert: " + e.getMessage());
            return "redirect:/admin/product/manage?error=" + URLEncoder.encode("Không thể tải danh mục sản phẩm.", StandardCharsets.UTF_8);
        }
    }

    // 3. Xử lý thêm sản phẩm kèm Upload file ảnh (Thay thế InsertProductServlet - doPost)
    @PostMapping("/insert")
    public String insertProduct(@RequestParam("name") String name,
                                @RequestParam("description") String description,
                                @RequestParam("price") double price,
                                @RequestParam(value = "stock", defaultValue = "0") int stock,
                                @RequestParam("photo") MultipartFile filePart) {
        try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
            ProductDAO productDAO = new ProductDAO(connection);

            String finalFileName = "no-sample.png";
            String imagePath = servletContext.getRealPath("") + File.separator + "image" + File.separator + "product";

            File imageDir = new File(imagePath);
            if (!imageDir.exists()) {
                imageDir.mkdirs();
            }

            // Xử lý upload file qua Spring MultipartFile
            if (filePart != null && !filePart.isEmpty()) {
                String originalFileName = filePart.getOriginalFilename();
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
                finalFileName = timestamp + "_" + originalFileName.replace(extension, "") + extension;

                File destFile = new File(imagePath + File.separator + finalFileName);
                filePart.transferTo(destFile);
            }

            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPhoto(finalFileName); // Hoặc setImg tùy thuộc tên thuộc tính model của bạn
            product.setPrice(price);
            product.setStock(stock);

            productDAO.addProduct(product);
            return "redirect:/admin/product/manage?success=" + URLEncoder.encode("Thêm sản phẩm mới thành công!", StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOGGER.severe("Error inserting product: " + e.getMessage());
            return "redirect:/admin/product/insert?error=" + URLEncoder.encode("Lỗi hệ thống: " + e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    // 4. Hiển thị Form Chỉnh sửa sản phẩm (Thay thế EditProductServlet - doGet)
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") int productId, Model model) {
        try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
            ProductDAO productDAO = new ProductDAO(connection);
            CategoryDAO categoryDAO = new CategoryDAO(connection);

            Product product = productDAO.getProductById(productId);
            if (product == null) {
                return "redirect:/admin/product/manage?error=" + URLEncoder.encode("Sản phẩm không tồn tại.", StandardCharsets.UTF_8);
            }

            List<Category> categories = categoryDAO.getAllCategories();
            model.addAttribute("product", product);
            model.addAttribute("categories", categories);

            return "forward:/admin/edit_product.jsp";
        } catch (Exception e) {
            LOGGER.severe("Error loading product for edit: " + e.getMessage());
            return "redirect:/admin/product/manage?error=" + URLEncoder.encode("Có lỗi xảy ra.", StandardCharsets.UTF_8);
        }
    }

    // 5. Xử lý Cập nhật thông tin sản phẩm (Đã sửa lỗi đón nhận MultipartFile)
    @PostMapping("/update")
    public String updateProduct(@RequestParam("id") int productId,
                                @RequestParam("name") String name,
                                @RequestParam("description") String description,
                                @RequestParam("price") double price,
                                @RequestParam("stock") int stock,
                                @RequestParam("category_id") int categoryId,
                                @RequestParam("photo") MultipartFile filePart) {
        try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
            ProductDAO productDAO = new ProductDAO(connection);
            CategoryDAO categoryDAO = new CategoryDAO(connection);

            Category category = categoryDAO.getCategoryById(categoryId);
            if (category == null) {
                return "redirect:/admin/product/manage?error=" + URLEncoder.encode("Danh mục không hợp lệ.", StandardCharsets.UTF_8);
            }

            Product existingProduct = productDAO.getProductById(productId);
            String finalFileName = (existingProduct != null) ? existingProduct.getPhoto() : "no-sample.png";

            if (filePart != null && !filePart.isEmpty()) {
                String originalFileName = filePart.getOriginalFilename();
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
                finalFileName = timestamp + "_" + originalFileName.replace(extension, "") + extension;

                String imagePath = servletContext.getRealPath("") + File.separator + "image" + File.separator + "product";
                File destFile = new File(imagePath + File.separator + finalFileName);
                filePart.transferTo(destFile);
            }

            Product product = new Product();
            product.setId(productId);
            product.setName(name);
            product.setDescription(description);
            product.setPhoto(finalFileName);
            product.setPrice(price);
            product.setStock(stock);
            product.setCategory(category);

            productDAO.updateProduct(product);
            return "redirect:/admin/product/manage?success=" + URLEncoder.encode("Cập nhật sản phẩm thành công!", StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOGGER.severe("Error updating product: " + e.getMessage());
            return "redirect:/admin/product/manage?error=" + URLEncoder.encode("Cập nhật thất bại.", StandardCharsets.UTF_8);
        }
    }
    // 6. Xử lý Xóa sản phẩm (Thay thế DeleteProductServlet - đổi sang POST để an toàn)
    @PostMapping("/delete")
    public String deleteProduct(@RequestParam("id") int productId) {
        try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
            ProductDAO productDAO = new ProductDAO(connection);

            if (productDAO.getProductById(productId) != null) {
                productDAO.deleteProduct(productId);
                return "redirect:/admin/product/manage?success=" + URLEncoder.encode("Xóa sản phẩm thành công!", StandardCharsets.UTF_8);
            } else {
                return "redirect:/admin/product/manage?error=" + URLEncoder.encode("Sản phẩm không tìm thấy.", StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            LOGGER.severe("Error deleting product: " + e.getMessage());
            return "redirect:/admin/product/manage?error=" + URLEncoder.encode("Lỗi khi xóa sản phẩm.", StandardCharsets.UTF_8);
        }
    }
}