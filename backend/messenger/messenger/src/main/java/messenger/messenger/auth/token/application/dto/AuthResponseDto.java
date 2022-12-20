package messenger.messenger.auth.token.application.dto;

import lombok.Getter;

@Getter
public class AuthResponseDto {

    private String tokenType = "Bearer ";

    private String accessToken;
    private String refreshToken;

    public AuthResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static AuthResponseDto of (String accessToken, String refreshToken) {
        return new AuthResponseDto(accessToken, refreshToken);
    }
}