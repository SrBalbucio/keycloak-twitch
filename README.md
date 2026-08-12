# Keycloak Twitch Identity Provider (SPI)

Extensão de **Social Login** do Keycloak para o **Twitch**, permitindo que usuários façam login/cadastro no Keycloak usando sua conta Twitch via OAuth 2.0 (Authorization Code Grant).

Compatível com **Keycloak 26.7.1** (Java 17).

## Endpoints do Twitch utilizados

| Endpoint | URL |
|----------|-----|
| Authorization | `https://id.twitch.tv/oauth2/authorize` |
| Token | `https://id.twitch.tv/oauth2/token` |
| User Info (Helix) | `https://api.twitch.tv/helix/users` |
| Scope padrão | `user:read:email` |

> **Importante:** A API Helix do Twitch exige os headers `Authorization: Bearer <token>` **e** `Client-Id: <client_id>` em todas as requisições. O provider envia ambos automaticamente.

## Mapeamento de atributos

O perfil retornado por `GET https://api.twitch.tv/helix/users` (objeto dentro de `data[]`) é mapeado para o `BrokeredIdentityContext`:

| Campo Twitch (Helix) | Destino no Keycloak |
|-----------------------|---------------------|
| `id` | ID federado / `brokerUserId` |
| `login` | `username` |
| `email` | `email` (requer scope `user:read:email`) |
| `display_name` | atributo `twitch.display_name` |
| `profile_image_url` | atributos `twitch.profile_image_url` e `picture` |
| `id` | atributo `twitch.id` |
| `login` | atributo `twitch.login` |

O campo `email` só é retornado pelo Twitch quando o token possui o scope `user:read:email` (incluído no `DEFAULT_SCOPE`).

## Build

```bash
mvn clean package
```

O artefato final será gerado em `target/keycloak-twitch.jar` (fat jar com jsoup embutido).

## Deploy

1. Copie `target/keycloak-twitch.jar` para o diretório `providers/` do Keycloak:
   ```bash
   cp target/keycloak-twitch.jar $KEYCLOAK_HOME/providers/
   ```
2. Reinicie o Keycloak.

## Configuração no Keycloak

### 1. Criar a aplicação no Twitch

1. Acesse o [Twitch Developer Console](https://dev.twitch.tv/console).
2. Registre uma aplicação em **Applications → Register Your Application**.
3. Defina o **OAuth Redirect URL** como:
   ```
   https://<SEU_KEYCLOAK>/realms/<SEU_REALM>/broker/twitch/endpoint
   ```
4. Anote o **Client ID** e gere um **Client Secret**.

### 2. Configurar o provider no Keycloak

1. No admin console, vá em **Identity Providers → Add provider → Twitch**.
2. Preencha:
   - **Client Id**: Client ID da aplicação Twitch.
   - **Client Secret**: Client Secret da aplicação Twitch.
   - **Default Scopes**: `user:read:email` (já é o padrão; adicione outros scopes conforme necessário).
   - **User Info URL** *(opcional)*: endpoint do Helix (padrão `https://api.twitch.tv/helix/users`).
3. Salve.

### 3. (Opcional) Attribute Mapper

O provider registra o mapper **Twitch Attribute Mapper**, que permite mapear qualquer atributo do contexto broker (ex: `twitch.display_name`, `twitch.profile_image_url`) para um atributo de usuário do Keycloak, configurável via UI sem recompilar.

## Testes

```bash
mvn test
```

Os testes validam a desserialização do payload do Helix (wrapper `data[]`), cenários com array vazio, ausência de email (sem scope) e campos desconhecidos.

## Licença

Apache License, Version 2.0.

## Autor

- [SrBalbucio](https://github.com/SrBalbucio)
