package eventure.event_service.service.imageStorage;

import eventure.event_service.dto.ImageMetadata;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class ImageProcessor {
    public ImageMetadata prepareMultipart(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null");
        }

        String mimeType = file.getContentType();
        if (mimeType == null || !mimeType.startsWith("image/")) {
            throw new IllegalArgumentException("File is not an image");
        }

        String extension = getExtension(mimeType);

        return new ImageMetadata(
                file.getInputStream(),
                file.getSize(),
                mimeType,
                extension
        );
    }

    private String getExtension(String mimeType) {
        return switch (mimeType) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            default -> throw new IllegalArgumentException("Unsupported image type: " + mimeType);
        };
    }
}
