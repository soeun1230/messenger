package messenger.messenger.common.config;

import lombok.RequiredArgsConstructor;
import messenger.messenger.auth.oauth.application.service.CustomOAuth2UserService;
import messenger.messenger.auth.oauth.application.service.CustomOidcUserService;
import messenger.messenger.auth.oauth.application.service.CustomUserDetailsService;
import messenger.messenger.auth.token.domain.TokenProviderImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomOidcUserService customOidcUserService;
    private final CorsFilter corsFilter;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers(
                "/static/**", "/static/js/**", "/static/images/**",
                "/static/css/**", "/static/scss/**",
                "/h2-console/**", "/favicon.ico", "/error"
        );
    }

    @Bean
    SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http,
                                                  TokenProviderImpl tokenProviderImpl) throws Exception {

        http
                .httpBasic().disable()
                .csrf().disable()

                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)

                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                .and()
                .authorizeRequests((requests) -> requests
                .antMatchers("/resources/**", "/api/v1/register", "/api/v1/login", "/")
                .permitAll()
                .anyRequest().authenticated())

                .formLogin().loginProcessingUrl("/api/v1/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/")
                .permitAll()
                .and()
//                .formLogin().disable()
                .oauth2Login(oauth2 -> oauth2.userInfoEndpoint(
                        userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(customOAuth2UserService)
                                .oidcUserService(customOidcUserService)
                ))
                .userDetailsService(customUserDetailsService)
                .exceptionHandling().authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/api/v1/login"))

                .and()
                .apply(new JwtSecurityConfig(tokenProviderImpl));

        return http.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }



}

