package messenger.messenger.auth.domain.social;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

@Slf4j
public class NaverUser extends OAuth2ProviderUser {

    public NaverUser(Map<String, Object> attributes, OAuth2User oAuth2User, ClientRegistration clientRegistration) {
        super(attributes, oAuth2User, clientRegistration);
    }

    @Override
    public String getId() {
        return (String) getAttributes().get("id");
    }

    @Override
    public String getUsername() {
        return (String) getAttributes().get("email");
    }

    @Override
    public String getPicture() {
        return (String) getAttributes().get("profile-image");
    }

}


