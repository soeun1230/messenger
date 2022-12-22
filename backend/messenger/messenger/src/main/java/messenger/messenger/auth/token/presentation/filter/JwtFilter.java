package messenger.messenger.auth.token.presentation.filter;

import lombok.extern.slf4j.Slf4j;
import messenger.messenger.auth.token.domain.TokenProviderImpl;
import org.hibernate.annotations.Filter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String[] whitelist = {"/login", "/logout",
            "/favicon.ico", "/static/**", "/api/v1/register",
            "/api/v1/login", "/"
    };

    private final TokenProviderImpl tokenProviderImpl;
    public JwtFilter(TokenProviderImpl tokenProviderImpl) {
        this.tokenProviderImpl = tokenProviderImpl;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String jwt = resolveToken(request);
        String requestURI = request.getRequestURI();

        if (isLoginCheckPath(requestURI)) {

            log.info("jwt = {}", jwt);


            if (StringUtils.hasText(jwt) && tokenProviderImpl.validateToken(jwt)) {
                Authentication authentication = tokenProviderImpl.getAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("authentication.getName() = {}", authentication.getName());

            } else {
                log.info("유효한 JWT 토큰이 없습니다, uri = {}", requestURI);
                return ;
            }

        }

        filterChain.doFilter(request, response);

    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        log.info("bearToken = {}", bearerToken);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    private boolean isLoginCheckPath(String requestURI) {
        return !PatternMatchUtils.simpleMatch(whitelist, requestURI);
    }

}
