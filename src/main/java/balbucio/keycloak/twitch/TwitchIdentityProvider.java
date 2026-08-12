package balbucio.keycloak.twitch;

import lombok.extern.jbosslog.JBossLog;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.keycloak.broker.oidc.AbstractOAuth2IdentityProvider;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.provider.IdentityBrokerException;
import org.keycloak.broker.social.SocialIdentityProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.util.JsonSerialization;

import java.io.IOException;
import java.util.List;

@JBossLog
public class TwitchIdentityProvider extends AbstractOAuth2IdentityProvider<TwitchIdentityProviderConfig>
        implements SocialIdentityProvider<TwitchIdentityProviderConfig> {

    public TwitchIdentityProvider(KeycloakSession session, TwitchIdentityProviderConfig config) {
        super(session, config);
    }

    @Override
    protected String getDefaultScopes() {
        return TwitchIdentityProviderConfig.DEFAULT_SCOPE;
    }

    @Override
    protected BrokeredIdentityContext doGetFederatedIdentity(String accessToken) {
        TwitchUserProfile profile = fetchUserProfile(accessToken);

        if (profile == null || isBlank(profile.getId())) {
            throw new IdentityBrokerException("Resposta do Twitch nao inclui o identificador do usuario.");
        }

        BrokeredIdentityContext context = new BrokeredIdentityContext(profile.getId(), getConfig());
        context.setBrokerUserId(profile.getId());
        context.setUsername(profile.getLogin());
        context.setModelUsername(context.getUsername());
        context.setEmail(profile.getEmail());
        context.setIdp(this);

        context.setUserAttribute("twitch.id", profile.getId());
        context.setUserAttribute("twitch.login", nullToEmpty(profile.getLogin()));
        context.setUserAttribute("twitch.display_name", nullToEmpty(profile.getDisplayName()));

        if (!isBlank(profile.getProfileImageUrl())) {
            context.setUserAttribute("twitch.profile_image_url", profile.getProfileImageUrl());
            context.setUserAttribute("picture", profile.getProfileImageUrl());
        }

        return context;
    }

    private TwitchUserProfile fetchUserProfile(String accessToken) {
        try {
            String body = fetchJson(getConfig().getUserInfoUrl(), accessToken);
            TwitchUsersResponse response = JsonSerialization.readValue(body, TwitchUsersResponse.class);
            List<TwitchUserProfile> data = response.getData();
            return (data == null || data.isEmpty()) ? null : data.get(0);
        } catch (IOException e) {
            throw new IdentityBrokerException("Nao foi possivel obter o perfil do usuario no Twitch.", e);
        }
    }

    protected String fetchJson(String url, String accessToken) throws IOException {
        Connection.Response response = Jsoup.connect(url)
                .ignoreContentType(true)
                .header("Authorization", "Bearer " + accessToken)
                .header("Client-Id", getConfig().getClientId())
                .header("Accept", "application/json")
                .execute();
        return response.body();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
