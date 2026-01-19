package eventure.event_service.service.imageStorage.impl;

import eventure.event_service.dto.ImageMetadata;
import eventure.event_service.service.imageStorage.FileStorage;
import eventure.event_service.service.imageStorage.ImageProcessor;
import eventure.event_service.service.imageStorage.ImageService;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final FileStorage fileStorage;
    private final ImageProcessor imageProcessor;

    @Override
    public CompletableFuture<String> uploadImage(MultipartFile photo) {
        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        ImageMetadata metadata = imageProcessor.prepareMultipart(photo);

                        String fileName = UUID.randomUUID() + "." + metadata.extension();

                        return fileStorage
                                .upload(
                                        metadata.inputStream(),
                                        metadata.length(),
                                        metadata.mimeType(),
                                        fileName)
                                .join();

                    } catch (IOException e) {
                        log.error("Failed to process image: {}", e.getMessage());
                        return null;
                    } catch (IllegalArgumentException e) {
                        log.warn("Invalid image file: {}", e.getMessage());
                        return null;
                    } catch (Exception e) {
                        log.error("Unexpected error during image upload: {}", e.getMessage(), e);
                        return null;
                    }
                });
    }
}
