package com.biketaxi.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ImageUtil {
    
    // Supported image formats
    private static final Set<String> SUPPORTED_FORMATS = new HashSet<>(Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    ));
    
    // Maximum file size (10MB to match application.properties)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    // Maximum dimensions
    private static final int MAX_WIDTH = 1920;
    private static final int MAX_HEIGHT = 1080;
    
    // Buffer size for efficient reading
    private static final int BUFFER_SIZE = 8192;
    
    /**
     * Validates if the uploaded file is a valid image
     */
    public static boolean isValidImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        
        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            return false;
        }
        
        // Check content type
        String contentType = file.getContentType();
        if (contentType == null || !SUPPORTED_FORMATS.contains(contentType.toLowerCase())) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Converts MultipartFile to byte array with memory optimization
     */
    public static byte[] convertToByteArray(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        
        // Check file size before processing
        long fileSize = file.getSize();
        if (fileSize > MAX_FILE_SIZE) {
            throw new IOException("File size exceeds maximum allowed size of " + MAX_FILE_SIZE + " bytes");
        }
        
        // Use a more efficient approach with proper buffer size
        try (InputStream inputStream = file.getInputStream();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            long totalBytesRead = 0;
            
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                
                // Additional safety check
                if (totalBytesRead > MAX_FILE_SIZE) {
                    throw new IOException("File size exceeds maximum allowed size during processing");
                }
            }
            
            return baos.toByteArray();
        } catch (OutOfMemoryError e) {
            throw new IOException("Insufficient memory to process image. Try reducing image size.", e);
        }
    }
    
    /**
     * Gets the file extension from content type
     */
    public static String getFileExtension(String contentType) {
        if (contentType == null) {
            return "jpg";
        }
        
        switch (contentType.toLowerCase()) {
            case "image/jpeg":
            case "image/jpg":
                return "jpg";
            case "image/png":
                return "png";
            case "image/gif":
                return "gif";
            case "image/webp":
                return "webp";
            default:
                return "jpg";
        }
    }
    
    /**
     * Generates a unique filename for the image
     */
    public static String generateImageFilename(String originalFilename, String contentType) {
        String extension = getFileExtension(contentType);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomSuffix = String.valueOf((int) (Math.random() * 1000));
        
        if (originalFilename != null && !originalFilename.isEmpty()) {
            String nameWithoutExtension = originalFilename.replaceAll("\\.[^.]*$", "");
            return nameWithoutExtension + "_" + timestamp + "_" + randomSuffix + "." + extension;
        }
        
        return "image_" + timestamp + "_" + randomSuffix + "." + extension;
    }
    
    /**
     * Validates image dimensions (basic check)
     */
    public static boolean isValidDimensions(byte[] imageData) {
        // This is a basic validation - in a real application, you might want to use
        // a library like ImageIO to get actual dimensions
        if (imageData == null || imageData.length == 0) {
            return false;
        }
        
        // For now, just check if the file size is reasonable
        return imageData.length > 100 && imageData.length <= MAX_FILE_SIZE;
    }
    
    /**
     * Gets the content type from byte array (basic detection)
     */
    public static String getContentTypeFromBytes(byte[] imageData) {
        if (imageData == null || imageData.length < 4) {
            return "image/jpeg";
        }
        
        // Check file signatures
        if (imageData[0] == (byte) 0xFF && imageData[1] == (byte) 0xD8) {
            return "image/jpeg";
        }
        if (imageData[0] == (byte) 0x89 && imageData[1] == (byte) 0x50 && 
            imageData[2] == (byte) 0x4E && imageData[3] == (byte) 0x47) {
            return "image/png";
        }
        if (imageData[0] == (byte) 0x47 && imageData[1] == (byte) 0x49 && 
            imageData[2] == (byte) 0x46) {
            return "image/gif";
        }
        
        return "image/jpeg"; // Default
    }
} 