package eventure.event_service.service.imageStorage;

import java.util.concurrent.CompletableFuture;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    CompletableFuture<String> uploadImage(MultipartFile photo);
}
