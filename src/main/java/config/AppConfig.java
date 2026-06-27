package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebMvc // Bật cấu hình Web MVC
@ComponentScan(basePackages = {"controller"}) // Thay đổi 'controller' thành package chứa các controller của bạn
public class AppConfig implements WebMvcConfigurer {

    // Cấu hình ViewResolver để trả về các trang JSP
    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/"); // Hoặc "/WEB-INF/views/" nếu bạn chuyển JSP vào đó (khuyến nghị)
        resolver.setSuffix(".jsp");
        return resolver;
    }

    // CẤU HÌNH QUAN TRỌNG: Cho phép truy cập tài nguyên tĩnh
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ánh xạ URL "/css/**" đến thư mục "/css/" trong webapp
        registry.addResourceHandler("/css/**").addResourceLocations("/css/");

        // Ánh xạ URL "/js/**" đến thư mục "/js/" trong webapp
        registry.addResourceHandler("/js/**").addResourceLocations("/js/");

        // Ánh xạ URL "/image/**" đến thư mục "/image/" trong webapp
        registry.addResourceHandler("/image/**").addResourceLocations("/image/");

        // Ánh xạ URL "/assets/**" đến thư mục "/assets/" trong webapp
        registry.addResourceHandler("/assets/**").addResourceLocations("/assets/");
    }
}