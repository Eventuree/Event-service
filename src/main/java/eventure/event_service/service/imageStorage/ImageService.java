package eventure.event_service.service.imageStorage;

import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

public interface ImageService {
    CompletableFuture<String> uploadBase64Image(String base64String);
}
