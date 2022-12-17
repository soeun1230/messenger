package messenger.messenger.auth.token.domain;

import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("logoutAccessToken")
@NoArgsConstructor
public class LogoutAccessToken extends Token{

    public LogoutAccessToken(String id, long expiration) {
        super(id, expiration);
    }

    public static LogoutAccessToken of (String accessToken, long expiration) {
        return new LogoutAccessToken(accessToken, expiration);
    }
}
