package messenger.messenger.auth.oauth.application;

import lombok.extern.slf4j.Slf4j;
import messenger.messenger.auth.oauth.application.certification.SelfCertification;
import messenger.messenger.auth.oauth.domain.PrincipalUser;
import messenger.messenger.auth.oauth.domain.social.ProviderUser;
import messenger.messenger.auth.user.application.UserService;
import messenger.messenger.auth.user.infra.UserRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;

@Slf4j
@Service
public class CustomOAuth2UserService extends AbstractOAuth2UserService implements
        OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    public CustomOAuth2UserService(UserRepository userRepository, UserService userService, SelfCertification certification) {
        super(userRepository, userService, certification);
    }
    
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        ProviderUser providerUser = super.providerUser(clientRegistration, oAuth2User);

        log.info("social getEmail = {}", providerUser.getEmail());
        log.info("social getOAuth2User = {}", providerUser.getOAuth2User());
        log.info("social getAuthorities = {}", providerUser.getAuthorities());
        log.info("social getPassword = {}", providerUser.getPassword());

        selfCertificate(providerUser);
        super.register(providerUser, userRequest);

        log.info("userRequest.getAccessToken() = {}", userRequest.getAccessToken());

        return new PrincipalUser(providerUser);

    }
}
