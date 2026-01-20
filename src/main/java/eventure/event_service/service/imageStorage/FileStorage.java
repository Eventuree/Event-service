package eventure.event_service.service.imageStorage;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public interface FileStorage {
    CompletableFuture<String> upload(
            InputStream inputStream, long contentLength, String contentType, String fileName);
}
