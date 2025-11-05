package com.example.ExpireDateReminderAppBackend.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 0H02009_カゥンセッリン
 */
public class FileUploadUtil {
    private FileUploadUtil() {
        // Prevent instantiation
    }

    public static String saveFile(MultipartFile file, String uploadDir) throws IOException {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    /**
     * 🔧 Deletes an old image file from the uploads directory.
     */
    public static void deleteOldFile(String oldImageUrl, String uploadDir) {
        if (oldImageUrl == null || oldImageUrl.isEmpty()) {
            return;
        }

        try {

            // Extract filename from URL like "/uploads/abc.jpg"
            Path oldImagePath = Paths.get(uploadDir, Paths.get(oldImageUrl).getFileName().toString());
            File oldFile = oldImagePath.toFile();

            if (oldFile.exists()) {
                boolean deleted = oldFile.delete();
                if (!deleted) {
                    System.err.println("⚠️ Failed to delete old image: " + oldFile.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error deleting old file: " + e.getMessage());
        }
    }
}
