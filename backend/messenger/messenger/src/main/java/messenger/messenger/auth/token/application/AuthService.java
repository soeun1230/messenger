package messenger.messenger.auth.token.application;

import lombok.RequiredArgsConstructor;
import messenger.messenger.auth.token.application.dto.AuthResponseDto;
import messenger.messenger.auth.token.domain.*;
import messenger.messenger.auth.token.infra.repository.TokenRepository;
import messenger.messenger.common.exception.NotExistsRefreshTokenException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final TokenProviderImpl tokenProviderImpl;
    private final TokenRepository tokenRepository;

    public void logout(String accessToken, String refreshToken) {
        saveLogoutAccessToken(accessToken);
        saveLogoutRefreshToken(refreshToken);
    }

    public AuthResponseDto reissue (String refreshToken) {
        refreshToken = tokenProviderImpl.removeType(refreshToken);
        isInRedisOrThrow(refreshToken);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String newAccessToken = tokenProviderImpl.createAccessToken(authentication);
        if (tokenProviderImpl.isMoreThanReissueTime(refreshToken))
            return AuthResponseDto.of(newAccessToken, refreshToken);

        deleteOriginRefreshToken(refreshToken);
        String newRefreshToken = createNewRefreshToken(authentication);
        return AuthResponseDto.of(newAccessToken, newRefreshToken);
    }

    /**
     *
     * 새로운 refreshToken을 생성하는 메소드
     *
     */
    private String createNewRefreshToken(Authentication authentication) {
        String newRefreshToken = tokenProviderImpl.createRefreshToken(authentication);
        tokenRepository.saveRefreshToken(
                RefreshToken.of(newRefreshToken, getRemainingTimeFromToken(newRefreshToken)));

        return newRefreshToken;
    }


    /**
     *
     * 새로운 refreshToken이 생성되면, 이전에 사용한 원래 refreshToken을 제거
     *
     */
    private void deleteOriginRefreshToken(String refreshToken) {
        tokenRepository.deleteRefreshTokenById(refreshToken);
        tokenRepository.saveLogoutRefreshToken(
                LogoutRefreshToken.of(refreshToken, getRemainingTimeFromToken(refreshToken))
        );
    }

    private void isInRedisOrThrow(String refreshToken) {
        if (!tokenRepository.existRefreshTokenById(refreshToken)) {
            throw new NotExistsRefreshTokenException();
        }
    }

    /**
     *
     * 로그 아웃시, refreshToken을 redis에 저장하여, 만료 시간 전까지,
     * refreshToken의 유효성을 저장하기 위한 메소드
     *
     */
    private void saveLogoutRefreshToken(String refreshToken) {
        String removedTypeRefreshToken = getRemoveBearerType(refreshToken);
        LogoutRefreshToken logoutRefreshToken = LogoutRefreshToken.of(removedTypeRefreshToken, getRemainingTimeFromToken(removedTypeRefreshToken));
        tokenRepository.saveLogoutRefreshToken(logoutRefreshToken);
    }

    /**
     *
     * 로그 아웃시, accessToken을 redis에 저장하여, 만료 시간 전까지,
     * accessToken의 유효성을 저장하기 위한 메소드
     *
     */
    private void saveLogoutAccessToken(String accessToken) {
        String removedTypeAccessToken = getRemoveBearerType(accessToken);
        LogoutAccessToken logoutAccessToken = LogoutAccessToken.of(removedTypeAccessToken, getRemainingTimeFromToken(removedTypeAccessToken));
        tokenRepository.saveLogoutAccessToken(logoutAccessToken);
    }

    private String getRemoveBearerType(String token) {
        return token.substring(7);
    }


    /**
     *
     * 토큰 expiration 만료 시간 get method
     *
     */
    private long getRemainingTimeFromToken(String token) {
        return tokenProviderImpl.getRemainingTimeFromToken(token);
    }


}
