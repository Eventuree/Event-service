package eventure.event_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String photoUrl;
    private String gender;
    private Short age;
    private String bio;
    private String location;
    private Set<Long> favoriteCategoryIds;
    @JsonProperty("social_media_links")
    private List<String> socialMediaLinks;
}
