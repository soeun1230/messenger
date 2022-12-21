package messenger.messenger.auth.oauth.application;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import messenger.messenger.auth.oauth.application.certification.SelfCertification;
import messenger.messenger.auth.oauth.infra.common.util.OAuth2Utils;
import messenger.messenger.auth.oauth.domain.social.*;
import messenger.messenger.auth.user.application.UserService;
import messenger.messenger.auth.user.domain.Users;
import messenger.messenger.auth.user.infra.UserRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Getter
public abstract class AbstractOAuth2UserService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final SelfCertification certification;

    public AbstractOAuth2UserService(UserRepository userRepository,
                                     UserService userService,
                                     SelfCertification certification) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.certification = certification;
    }

    public void selfCertificate(ProviderUser providerUser) {
        certification.checkCertification(providerUser);
    }

    public void register(ProviderUser providerUser, OAuth2UserRequest oAuth2UserRequest) {
        Users user = userRepository.findByEmail(providerUser.getEmail());

        if (user == null) {
            ClientRegistration clientRegistration = oAuth2UserRequest.getClientRegistration();
            userService.register(clientRegistration.getRegistrationId(), providerUser);
        }

    }

    public ProviderUser providerUser(ClientRegistration clientRegistration, OAuth2User oAuth2User) {

        String registrationId = clientRegistration.getRegistrationId();

        log.info("registrationId = {}", registrationId);

        switch (registrationId) {

            case "google" :
                return new GoogleUser(OAuth2Utils.getMainAttributes(oAuth2User), oAuth2User, clientRegistration);

            case "naver" :
                return new NaverUser(OAuth2Utils.getSubAttributes(oAuth2User, "response"), oAuth2User, clientRegistration);

            case "kakao" :
                if (oAuth2User instanceof OidcUser) {
                    return new KakaoOidcUser(OAuth2Utils.getMainAttributes(oAuth2User), oAuth2User, clientRegistration);
                } else {
                    return new KakaoUser(OAuth2Utils.getOtherAttributes(oAuth2User, "kakao_account", "profile"),
                            oAuth2User, clientRegistration);
                }

            default:
                return null;
        }

    }


}
