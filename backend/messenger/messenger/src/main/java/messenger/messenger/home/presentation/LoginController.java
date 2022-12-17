package messenger.messenger.home.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import messenger.messenger.auth.token.domain.TokenProvider;
import messenger.messenger.auth.user.application.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;
    private final TokenProvider tokenProvider;

    @CrossOrigin("http://localhost:3000")
    @GetMapping("/")
    public ResponseEntity login(HttpServletRequest request,
                                HttpServletResponse response) {

        log.info("request = {}", request.getHeaderNames());
        log.info("request = {}", request.getUserPrincipal());
        log.info("request = {}", request.getRequestURL());

        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication ) {

        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, authentication);

        return "redirect:/login";

    }

}
