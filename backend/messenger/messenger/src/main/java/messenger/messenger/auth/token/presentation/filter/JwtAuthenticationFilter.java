package messenger.messenger.auth.token.presentation.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import messenger.messenger.auth.token.domain.TokenProviderImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final TokenProviderImpl tokenProviderImpl;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = resolveToken((HttpServletRequest) request);

        log.info("token = {}", token);

        if (token != null && tokenProviderImpl.validateToken(token)) {
            Authentication authentication = tokenProviderImpl.getAuthentication(token);

            log.info("authentication jwtAuthenticationFiler = {}", authentication);

            request.setAttribute("authentication", authentication);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        log.info("bearerToken = {}", bearerToken);

        return null;
    }

}
