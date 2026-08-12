package balbucio.keycloak.twitch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitchUserProfile {

    private String id;

    private String login;

    @JsonProperty("display_name")
    private String displayName;

    private String email;

    @JsonProperty("profile_image_url")
    private String profileImageUrl;
}
