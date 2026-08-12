package balbucio.keycloak.twitch;

import org.junit.jupiter.api.Test;
import org.keycloak.util.JsonSerialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TwitchUserProfileTest {

    @Test
    void shouldDeserializeHelixUsersPayload() throws Exception {
        String payload = "{"
                + "\"data\":[{"
                + "\"id\":\"141981764\","
                + "\"login\":\"twitchdev\","
                + "\"display_name\":\"TwitchDev\","
                + "\"type\":\"\","
                + "\"broadcaster_type\":\"partner\","
                + "\"description\":\"Supporting third-party developers.\","
                + "\"profile_image_url\":\"https://static-cdn.jtvnw.net/jtv_user_pictures/profile_image.png\","
                + "\"offline_image_url\":\"https://static-cdn.jtvnw.net/jtv_user_pictures/offline_image.png\","
                + "\"view_count\":0,"
                + "\"email\":\"not-real@email.com\","
                + "\"created_at\":\"2016-12-14T20:32:28Z\""
                + "}"
                + "]}";

        TwitchUsersResponse response = JsonSerialization.readValue(payload, TwitchUsersResponse.class);

        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());

        TwitchUserProfile profile = response.getData().get(0);
        assertEquals("141981764", profile.getId());
        assertEquals("twitchdev", profile.getLogin());
        assertEquals("TwitchDev", profile.getDisplayName());
        assertEquals("not-real@email.com", profile.getEmail());
        assertEquals("https://static-cdn.jtvnw.net/jtv_user_pictures/profile_image.png", profile.getProfileImageUrl());
    }

    @Test
    void shouldHandleEmptyDataArray() throws Exception {
        String payload = "{\"data\":[]}";

        TwitchUsersResponse response = JsonSerialization.readValue(payload, TwitchUsersResponse.class);

        assertNotNull(response.getData());
        assertEquals(0, response.getData().size());
    }

    @Test
    void shouldHandleMissingEmailWhenScopeAbsent() throws Exception {
        String payload = "{\"data\":[{\"id\":\"1\",\"login\":\"user\",\"display_name\":\"User\"}]}";

        TwitchUsersResponse response = JsonSerialization.readValue(payload, TwitchUsersResponse.class);

        TwitchUserProfile profile = response.getData().get(0);
        assertEquals("1", profile.getId());
        assertNull(profile.getEmail());
        assertNull(profile.getProfileImageUrl());
    }

    @Test
    void shouldIgnoreUnknownFields() throws Exception {
        String payload = "{\"data\":[{\"id\":\"1\",\"login\":\"user\",\"unexpected_field\":\"value\",\"nested\":{\"a\":1}}]}";

        TwitchUsersResponse response = JsonSerialization.readValue(payload, TwitchUsersResponse.class);

        assertEquals("1", response.getData().get(0).getId());
    }
}
