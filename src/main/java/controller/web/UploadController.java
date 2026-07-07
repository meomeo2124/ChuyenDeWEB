package controller.web;

import dao.UserDAO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Controller
public class UploadController {

	private final UserDAO userDAO;
	private final ServletContext servletContext;

	@Autowired
	public UploadController(UserDAO userDAO, ServletContext servletContext) {
		this.userDAO = userDAO;
		this.servletContext = servletContext;
	}


	@GetMapping("/upload")
	public String showUploadPage() {
		return "upload";
	}

	@PostMapping("/upload")
	public String handleUpload(@RequestParam("photo") MultipartFile filePart, HttpSession session) {
		try {
			User user = (User) session.getAttribute("user");
			if (user == null) {
				return "redirect:/login";
			}

			if (filePart != null && !filePart.isEmpty()) {
				String realPath = servletContext.getRealPath("/image/avatars");
				String filename = filePart.getOriginalFilename();

				// Tạo thư mục nếu chưa tồn tại
				if (!Files.exists(Path.of(realPath))) {
					Files.createDirectories(Path.of(realPath));
				}

				// Lưu file ảnh vào ổ đĩa
				String picPath = realPath + File.separator + filename;
				filePart.transferTo(new File(picPath));

				// Cập nhật DB thông qua Spring Bean và cập nhật Session
				String dbSavedPath = "image/avatars/" + filename;
				userDAO.changeImg(user.getId(), dbSavedPath);

				// Đồng bộ lại dữ liệu trong session hiện tại để giao diện đổi ảnh ngay lập tức
				user.setImg(dbSavedPath);
				session.setAttribute("user", user);
				session.setAttribute("img", dbSavedPath);
			}

			return "redirect:/home";

		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/home?error=upload_failed";
		}
	}
}