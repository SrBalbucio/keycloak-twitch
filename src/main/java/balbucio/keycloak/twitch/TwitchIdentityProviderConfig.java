package balbucio.keycloak.twitch;

import org.keycloak.broker.oidc.OAuth2IdentityProviderConfig;
import org.keycloak.models.IdentityProviderModel;

public class TwitchIdentityProviderConfig extends OAuth2IdentityProviderConfig {

    public static final String DEFAULT_AUTH_URL = "https://id.twitch.tv/oauth2/authorize";
    public static final String DEFAULT_TOKEN_URL = "https://id.twitch.tv/oauth2/token";
    public static final String DEFAULT_USER_INFO_URL = "https://api.twitch.tv/helix/users";
    public static final String DEFAULT_SCOPE = "user:read:email";
    public static final String USER_INFO_URL_KEY = "userInfoUrl";

    public TwitchIdentityProviderConfig() {
        setAuthorizationUrl(DEFAULT_AUTH_URL);
        setTokenUrl(DEFAULT_TOKEN_URL);
        setUserInfoUrl(DEFAULT_USER_INFO_URL);
        setDefaultScope(DEFAULT_SCOPE);
        setStoreToken(true);
    }

    public TwitchIdentityProviderConfig(IdentityProviderModel model) {
        super(model);
    }

    public String getUserInfoUrl() {
        return getConfig().getOrDefault(USER_INFO_URL_KEY, DEFAULT_USER_INFO_URL);
    }

    public void setUserInfoUrl(String userInfoUrl) {
        getConfig().put(USER_INFO_URL_KEY, userInfoUrl);
    }
}
