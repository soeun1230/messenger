package messenger.messenger.auth.token.domain;

import messenger.messenger.auth.user.domain.Authority;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface TokenProvider {

    String TOKEN_TYPE = "Bearer ";

    String createAccessToken(Authentication authentication);

    String createRefreshToken(Authentication authentication);

    String getUserEmailFromToken(String token);

    long getRemainingTimeFromToken(String token);

    boolean isMoreThanReissueTime(String token);

    boolean validateToken(String authToken);

    String removeType(String token);



}
