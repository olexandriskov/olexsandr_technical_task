package technikal.task.fishmarket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Slf4j
@Service
public class FileService {

    @Value("${fish.images.path}")
    private String fishImagesPath;

    public String saveFile(MultipartFile image) throws IOException {
        Path uploadPath = Paths.get(fishImagesPath);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String imageFileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();

        try (InputStream inputStream = image.getInputStream()) {
            Files.copy(inputStream, uploadPath.resolve(imageFileName), StandardCopyOption.REPLACE_EXISTING);
        }

        return imageFileName;
    }

    public void deleteFile(String fileName) throws IOException {
        Path path = Paths.get(fishImagesPath).resolve(fileName);
        Files.deleteIfExists(path);
    }

    public void deleteFiles(List<String> fileNames) {
        for (String fileName : fileNames) {
            try {
                deleteFile(fileName);
            } catch (Exception ex) {
                log.error("Помилка видалення файлу з диска: {}", fileName, ex);
            }
        }
    }
}
