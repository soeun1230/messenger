package messenger.messenger.auth.token.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.TimeToLive;

import javax.persistence.Id;
import java.util.concurrent.TimeUnit;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Token {

    @Id
    private String id;

    @TimeToLive(unit = TimeUnit.MILLISECONDS)
    private long expiration;

}
