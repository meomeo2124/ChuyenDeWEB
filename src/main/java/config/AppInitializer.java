package config;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return null;
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        // Chỉ cần nạp AppConfig là đủ, vì trong AppConfig đã có @ComponentScan("config")
        // nó sẽ tự động tìm thấy SecurityConfig
        return new Class[]{AppConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
                "",
                1024 * 1024 * 10,  // Tối đa 10MB/file
                1024 * 1024 * 50,  // Tối đa 50MB/request
                1024 * 1024 * 2    // Kích thước vùng đệm 2MB
        );
        registration.setMultipartConfig(multipartConfigElement);
    }
}