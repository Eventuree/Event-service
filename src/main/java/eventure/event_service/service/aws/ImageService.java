package eventure.event_service.service.aws;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.Base64;
import java.util.UUID;

@Service
public class ImageService {
    private final S3Client s3Client;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Value("${aws.region")
    private String region;

    public ImageService (S3Client s3Client){
        this.s3Client = s3Client;
    }

    public String uploadBase64Image(String base64String) {

        String contentType = "image/png";
        String extension = "png";

        if (base64String.startsWith("data:")) {
            int mimeEnd = base64String.indexOf(";");
            contentType = base64String.substring(5, mimeEnd);
            extension = contentType.split("/")[1];
        }

        String imageData = base64String.contains(",") ?
                base64String.split(",")[1] : base64String;
        byte[] decodedBytes = Base64.getDecoder().decode(imageData);

        String fileName = UUID.randomUUID() + "." + extension;

        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .contentType(contentType)
                        .build(),
                RequestBody.fromBytes(decodedBytes));

        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName);
    }
}
