package messenger.messenger.auth.token.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import messenger.messenger.auth.token.domain.TokenProviderImpl;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenProviderService {

    private final TokenProviderImpl tokenProviderImpl;

    @Transactional
    public String createToken(Authentication authentication) {

        String token = null;

        if (authentication != null) {
            token = tokenProviderImpl.createAccessToken(authentication);
        }

        return token;

    }
}
