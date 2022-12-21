package messenger.messenger.auth.token.presentation.filter;

import lombok.extern.slf4j.Slf4j;
import messenger.messenger.auth.token.domain.TokenProviderImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@Component
public class JwtFilter extends GenericFilterBean {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String[] whitelist = {"/login", "/logout",
            "/favicon.ico", "/static/**", "/api/v1/register",
            "/api/v1/login", "/"
    };

    private TokenProviderImpl tokenProviderImpl;
    public JwtFilter(TokenProviderImpl tokenProviderImpl) {
        this.tokenProviderImpl = tokenProviderImpl;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String jwt = resolveToken(httpServletRequest);
        String requestURI = httpServletRequest.getRequestURI();

        log.info("requestURI = {}", requestURI);
        if (isLoginCheckPath(requestURI)) {


            log.info("getUserPrincipal = {}", httpServletRequest.getUserPrincipal());
            log.info("getRequestURL = {}", httpServletRequest.getRequestURL());
            log.info("getRequestURI = {}", httpServletRequest.getRequestURI());
            log.info("getAuthType = {}", httpServletRequest.getAuthType());
            log.info("getContextPath = {}", httpServletRequest.getContextPath());
            log.info("getCookies = {}", httpServletRequest.getCookies());
            log.info("getContentType = {}", httpServletRequest.getContentType());

            log.info("jwt = {}", jwt);


            if (StringUtils.hasText(jwt) && tokenProviderImpl.validateToken(jwt)) {
                Authentication authentication = tokenProviderImpl.getAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("authentication.getName() = {}", authentication.getName());
                log.info("requestURI = {}", requestURI);

            } else {
                log.info("유효한 JWT 토큰이 없습니다, uri = {}", requestURI);
                return ;
            }

        }

        filterChain.doFilter(servletRequest, servletResponse);
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
