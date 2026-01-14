package eventure.event_service.service.imageStorage;

import eventure.event_service.dto.ImageMetadata;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class ImageProcessor {
    public ImageMetadata prepareForUpload(String base64String) {

        String mimeType = extractMimeType(base64String);
        String extension = getExtension(mimeType);
        String base64Data = extractBase64Data(base64String);
        long length = calculateBinaryLength(base64Data);

        InputStream rawStream = new ByteArrayInputStream(base64Data.getBytes(StandardCharsets.US_ASCII));
        InputStream decodedStream = Base64.getDecoder().wrap(rawStream);

        return new ImageMetadata(decodedStream, length, mimeType, extension);
    }

    private String extractBase64Data(String base64String) {
        int commaIndex = base64String.indexOf(",");
        if (commaIndex == -1) {
            return base64String;
        }
        return base64String.substring(commaIndex + 1);
    }

    private long calculateBinaryLength(String base64Data) {
        long n = base64Data.length();
        long padding = 0;
        if (base64Data.endsWith("==")) padding = 2;
        else if (base64Data.endsWith("=")) padding = 1;
        return (n * 3 / 4) - padding;
    }


    private String extractMimeType(String base64String) {
        if (base64String == null || !base64String.startsWith("data:")) {
            throw new IllegalArgumentException("Invalid Base64 format: missing 'data:' prefix");
        }
        int semicolonIndex = base64String.indexOf(";");
        if (semicolonIndex == -1) {
            throw new IllegalArgumentException("Invalid Base64 format: missing semicolon");
        }
        return base64String.substring(5, semicolonIndex);
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
