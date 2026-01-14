package eventure.event_service.service.imageStorage.impl;

import eventure.event_service.dto.ImageMetadata;
import eventure.event_service.service.imageStorage.FileStorage;
import eventure.event_service.service.imageStorage.ImageService;
import eventure.event_service.service.imageStorage.ImageProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final FileStorage fileStorage;
    private final ImageProcessor imageProcessor;


    @Override
    public CompletableFuture<String> uploadBase64Image(String base64String) {
        ImageMetadata metadata = imageProcessor.prepareForUpload(base64String);

        String fileName = UUID.randomUUID() + "." + metadata.extension();

        return fileStorage.upload(
                metadata.inputStream(),
                metadata.length(),
                metadata.mimeType(),
                fileName);
    }
}
