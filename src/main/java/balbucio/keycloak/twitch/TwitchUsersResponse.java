package balbucio.keycloak.twitch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitchUsersResponse {

    private List<TwitchUserProfile> data;
}
