package messenger.messenger.auth.token.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import messenger.messenger.auth.token.domain.TokenProviderImpl;
import messenger.messenger.auth.user.domain.Authority;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenProviderService {

    private final TokenProviderImpl tokenProviderImpl;

    @Transactional
    public String createAccessToken(Authentication authentication) {

        String token = null;

        if (authentication != null) {
            token = tokenProviderImpl.createAccessToken(authentication);
        }

        return token;
    }

    @Transactional
    public String createRefreshToken(Authentication authentication) {

        String token = null;

        if (authentication != null) {
            token = tokenProviderImpl.createRefreshToken(authentication);
        }

        return token;
    }

    @Transactional
    public String createAccessTokenFormLogin(String email, List<Authority> authorities) {

        String token = null;

        if (email != null) {
            token = tokenProviderImpl.createAccessToken(email, authorities);
        }
        return token;
    }

    @Transactional
    public String createRefreshTokenFormLogin(String email, List<Authority> authorities) {

        String token = null;

        if (email != null) {
            token = tokenProviderImpl.createRefreshToken(email, authorities);
        }
        return token;
    }



    public boolean validateToken(String token) {
        return tokenProviderImpl.validateToken(token);
    }


}
