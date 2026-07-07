package dao;

import config.AppConfig;
import models.Product;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

public class TestConnection {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        try {
            // Test 1: Lấy thử DataSource xem Spring đã quản lý kết nối thành công chưa
            DataSource dataSource = context.getBean(DataSource.class);
            try (Connection connection = dataSource.getConnection()) {
                if (connection != null) {
                    System.out.println(">>> Kết nối cơ sở dữ liệu qua Spring Bean thành công!");
                }
            }

            // Test 2: Lấy thử ProductDAO đã được Spring tiêm DataSource tự động
            ProductDAO dao = context.getBean(ProductDAO.class);
            List<Product> list = dao.getAllProducts();

            System.out.println(">>> Danh sách sản phẩm quét từ Spring IoC:");
            for (Product p : list) {
                System.out.println(p.toString());
            }

        } catch (Exception e) {
            System.err.println("Lỗi hệ thống: " + e.getMessage());
            e.printStackTrace();
        } finally {
            context.close();
        }
    }
}