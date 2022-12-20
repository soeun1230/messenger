package messenger.messenger.auth.token.domain;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProviderImpl implements InitializingBean, TokenProvider{

    public static final String TOKEN_TYPE = "Bearer ";
    private static final String AUTHORITIES_KEY = "auth";
    private Key key;

    private final String secretKey;
    private final long accessTokenExpirationTime;
    private final long refreshTokenExpirationTime;
    private final long reissueRefreshTime;

    public TokenProviderImpl(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-expiration-time}") long accessTokenExpirationTime,
            @Value("${jwt.refresh-expiration-time}") long refreshTokenExpirationTime,
            @Value("${jwt.reissue-refresh-time}") long reissueRefreshTime) {
        this.secretKey = secretKey;
        this.accessTokenExpirationTime = accessTokenExpirationTime;
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;
        this.reissueRefreshTime = reissueRefreshTime;
    }



    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(Authentication authentication, long tokenTime) {

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + tokenTime);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(now)
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(expiryDate)
                .compact();
    }

    public String createRefreshToken(Authentication authentication) {
        return createToken(authentication, refreshTokenExpirationTime);
    }

    public String createAccessToken(Authentication authentication) {
        return createToken(authentication, accessTokenExpirationTime);
    }


    public String getUserEmailFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }


    public long getRemainingTimeFromToken(String token) {
        Date expiration = getClaims(token).getExpiration();
        return expiration.getTime() - (new Date()).getTime();
    }

    public boolean isMoreThanReissueTime(String token) {
        return getRemainingTimeFromToken(token) >= reissueRefreshTime;
    }


    public Claims getClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build().parseClaimsJws(token)
                .getBody();
    }


    public Authentication getAuthentication(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    public String removeType(String token) {
        return token.substring(TOKEN_TYPE.length());
    }
}