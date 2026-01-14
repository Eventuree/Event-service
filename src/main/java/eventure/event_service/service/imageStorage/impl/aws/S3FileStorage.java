package eventure.event_service.service.imageStorage.impl.aws;

import eventure.event_service.service.imageStorage.FileStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class S3FileStorage implements FileStorage {

    private final S3AsyncClient s3AsyncClient;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Override
    public CompletableFuture<String> upload(InputStream inputStream, long contentLength, String contentType, String fileName) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(contentType)
                .build();

        return s3AsyncClient.putObject(putObjectRequest,
                        AsyncRequestBody.fromInputStream(inputStream, contentLength, Executors.newSingleThreadExecutor()))
                .thenApply(response -> String.format("https://%s.s3.amazonaws.com/%s", bucketName, fileName));
    }
}
