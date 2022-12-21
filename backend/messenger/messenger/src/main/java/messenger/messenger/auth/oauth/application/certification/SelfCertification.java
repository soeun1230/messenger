package messenger.messenger.auth.oauth.application.certification;

import lombok.RequiredArgsConstructor;
import messenger.messenger.auth.oauth.domain.social.ProviderUser;
import messenger.messenger.auth.user.infra.UserRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SelfCertification {

    private final UserRepository userRepository;

    public void checkCertification(ProviderUser providerUser) {

    }

}
