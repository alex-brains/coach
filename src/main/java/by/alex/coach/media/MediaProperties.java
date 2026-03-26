package by.alex.coach.media;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Читает секцию app.media из application.yaml
 */
@Component
@ConfigurationProperties(prefix = "app.media")
public class MediaProperties {

    private String uploadDir = "uploads";
    private List<String> allowedTypes = List.of("image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml");

    public String getUploadDir() { return uploadDir; }
    public void setUploadDir(String uploadDir) { this.uploadDir = uploadDir; }

    public List<String> getAllowedTypes() { return allowedTypes; }
    public void setAllowedTypes(List<String> allowedTypes) { this.allowedTypes = allowedTypes; }
}
