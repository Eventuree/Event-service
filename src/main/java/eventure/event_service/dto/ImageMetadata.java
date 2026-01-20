package eventure.event_service.dto;

import java.io.InputStream;

public record ImageMetadata(
        InputStream inputStream, long length, String mimeType, String extension) {}
