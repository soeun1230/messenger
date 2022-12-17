package messenger.messenger.common.config;

import messenger.messenger.auth.token.domain.TokenProviderImpl;
import messenger.messenger.auth.token.presentation.filter.JwtFilter;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final TokenProviderImpl tokenProviderImpl;
    public JwtSecurityConfig(TokenProviderImpl tokenProviderImpl) {
        this.tokenProviderImpl = tokenProviderImpl;
    }

    @Override
    public void configure(HttpSecurity http) {
        http.addFilterBefore(
                new JwtFilter(tokenProviderImpl),
                UsernamePasswordAuthenticationFilter.class
        );
    }
}