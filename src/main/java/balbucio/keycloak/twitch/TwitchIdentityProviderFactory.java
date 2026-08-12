package balbucio.keycloak.twitch;

import com.google.auto.service.AutoService;
import org.keycloak.broker.provider.AbstractIdentityProviderFactory;
import org.keycloak.broker.social.SocialIdentityProviderFactory;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.ArrayList;
import java.util.List;

@AutoService(SocialIdentityProviderFactory.class)
public class TwitchIdentityProviderFactory extends AbstractIdentityProviderFactory<TwitchIdentityProvider>
        implements SocialIdentityProviderFactory<TwitchIdentityProvider> {

    public static final String PROVIDER_ID = "twitch";

    private static final List<ProviderConfigProperty> CONFIG_PROPERTIES = new ArrayList<>();

    static {
        ProviderConfigProperty userInfoUrl = new ProviderConfigProperty();
        userInfoUrl.setName(TwitchIdentityProviderConfig.USER_INFO_URL_KEY);
        userInfoUrl.setLabel("User Info URL");
        userInfoUrl.setType(ProviderConfigProperty.STRING_TYPE);
        userInfoUrl.setDefaultValue(TwitchIdentityProviderConfig.DEFAULT_USER_INFO_URL);
        userInfoUrl.setHelpText("Endpoint do Twitch Helix usado para carregar o perfil do usuario.");
        CONFIG_PROPERTIES.add(userInfoUrl);
    }

    @Override
    public String getName() {
        return "Twitch";
    }

    @Override
    public TwitchIdentityProvider create(KeycloakSession session, IdentityProviderModel model) {
        TwitchIdentityProviderConfig config = new TwitchIdentityProviderConfig(model);
        config.setAuthorizationUrl(TwitchIdentityProviderConfig.DEFAULT_AUTH_URL);
        config.setTokenUrl(TwitchIdentityProviderConfig.DEFAULT_TOKEN_URL);
        if (isBlank(config.getDefaultScope())) {
            config.setDefaultScope(TwitchIdentityProviderConfig.DEFAULT_SCOPE);
        }
        if (isBlank(config.getUserInfoUrl())) {
            config.setUserInfoUrl(TwitchIdentityProviderConfig.DEFAULT_USER_INFO_URL);
        }
        return new TwitchIdentityProvider(session, config);
    }

    @Override
    public TwitchIdentityProviderConfig createConfig() {
        return new TwitchIdentityProviderConfig();
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return CONFIG_PROPERTIES;
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
