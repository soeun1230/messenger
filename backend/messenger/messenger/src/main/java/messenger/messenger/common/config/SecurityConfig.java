package messenger.messenger.common.config;

import lombok.RequiredArgsConstructor;
import messenger.messenger.auth.oauth.application.CustomOAuth2UserService;
import messenger.messenger.auth.oauth.application.CustomOidcUserService;
import messenger.messenger.auth.token.domain.TokenProviderImpl;
import messenger.messenger.auth.token.presentation.handler.JwtAccessDeniedHandler;
import messenger.messenger.auth.token.presentation.handler.JwtAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOidcUserService customOidcUserService;
    private final TokenProviderImpl tokenProviderImpl;
    private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers(
                "/static/**", "/static/js/**", "/static/images/**",
                "/static/css/**", "/static/scss/**",
                "/h2-console/**", "/favicon.ico", "/error"
        );
    }

//    @Bean
//    JwtAuthenticationFilter jwtAuthenticationFilter(TokenProviderImpl jwtProvider) {
//        return new JwtAuthenticationFilter(jwtProvider);
//    }

    @Bean
    SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http,
                                                  TokenProviderImpl tokenProviderImpl) throws Exception {

        http
                .httpBasic().disable()
                .csrf().disable()

                // add
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                //

                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)

                .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                .and()
                .authorizeRequests((requests) -> requests
                .antMatchers("/", "/resources/**")
                .permitAll()
                .anyRequest().authenticated())

                .oauth2Login(oauth2 -> oauth2.userInfoEndpoint(
                        userInfoEndpointConfig -> userInfoEndpointConfig
                        .userService(customOAuth2UserService)
                        .oidcUserService(customOidcUserService)))

                .apply(new JwtSecurityConfig(tokenProviderImpl));
//                .addFilterBefore(jwtAuthenticationFilter(tokenProvider),
//                        UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}

