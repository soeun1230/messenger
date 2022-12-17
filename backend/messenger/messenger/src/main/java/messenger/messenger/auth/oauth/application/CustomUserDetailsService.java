package messenger.messenger.auth.oauth.application;

import messenger.messenger.auth.oauth.application.certification.SelfCertification;
import messenger.messenger.auth.user.application.UserService;
import messenger.messenger.auth.user.domain.Users;
import messenger.messenger.auth.user.infra.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService extends AbstractOAuth2UserService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailsService(UserRepository userRepository, UserService userService, SelfCertification certification, PasswordEncoder passwordEncoder) {
        super(userRepository, userService, certification);
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = getUserRepository().findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("존재하지 않는 회원입니다.");
        }

        return createUserDetails(user);
    }

    private UserDetails createUserDetails(Users loginUser) {
        return User.builder()
                .username(loginUser.getUsername())
                .password(passwordEncoder.encode(loginUser.getPassword()))
                .roles("ROLE_USER")
                .build();
    }

}

