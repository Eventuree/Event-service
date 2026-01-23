package eventure.event_service.client;

import eventure.event_service.dto.UserProfileDto;
import eventure.event_service.dto.UserProfileSummaryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "profile-service", url = "${application.config.profile-url}")
public interface ProfileServiceClient {

    @GetMapping("/api/v1/profiles/{id}/summary")
    UserProfileSummaryDto getUserProfileSummary(@PathVariable("id") Long id);

    @GetMapping("/api/v1/profiles/{id}")
    UserProfileDto getUserProfile(@PathVariable("id") Long id);
}
