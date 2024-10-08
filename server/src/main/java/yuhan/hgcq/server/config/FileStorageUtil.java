package yuhan.hgcq.server.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FileStorageUtil {

    private static final String TEMP_DIR = File.separator
            + "app" + File.separator
            + "temp" + File.separator;

    public static String saveFile(MultipartFile file) throws IOException {
        try {
            File dir = new File(TEMP_DIR);

            if (!dir.exists()) {
                dir.mkdirs();
            }

            String name = file.getOriginalFilename();
            Path path = Paths.get(TEMP_DIR + name);
            file.transferTo(path);

            return "/app/temp/" + name;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public static void deleteFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.deleteIfExists(path);
    }
}
