package messenger.messenger.auth.oauth.application.service;

import lombok.extern.slf4j.Slf4j;
import messenger.messenger.auth.oauth.application.converter.ProviderUserConverter;
import messenger.messenger.auth.oauth.application.converter.ProviderUserRequest;
import messenger.messenger.auth.oauth.domain.PrincipalUser;
import messenger.messenger.auth.oauth.domain.social.ProviderUser;
import messenger.messenger.auth.user.application.UserService;
import messenger.messenger.auth.user.domain.Users;
import messenger.messenger.auth.user.infra.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomUserDetailsService extends AbstractOAuth2UserService implements UserDetailsService {


    public CustomUserDetailsService(UserRepository userRepository, UserService userService, ProviderUserConverter<ProviderUserRequest, ProviderUser> providerUserConverter) {
        super(userRepository, userService, providerUserConverter);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = getUserRepository().findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("존재하지 않는 회원입니다.");
        }

        log.info("userDetails = {}", user);

        ProviderUserRequest providerUserRequest = new ProviderUserRequest(user);
        ProviderUser providerUser = providerUser(providerUserRequest);

        log.info("providerUser.getEmail = {}", providerUser.getEmail());
        log.info("providerUser.getPassword = {}", providerUser.getPassword());
        log.info("providerUser.getAuthorities = {}", providerUser.getAuthorities());

        return new PrincipalUser(providerUser);

    }



}

