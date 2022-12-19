package messenger.messenger.auth.token.presentation.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import messenger.messenger.auth.oauth.domain.PrincipalUser;
import messenger.messenger.auth.token.application.TokenProviderService;
import messenger.messenger.auth.token.domain.TokenProviderImpl;
import messenger.messenger.auth.user.application.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@Slf4j
@RequiredArgsConstructor
public class TokenController {

    private final UserService userService;

    private final TokenProviderImpl tokenProviderImpl;
    private final TokenProviderService tokenProviderService;


    @CrossOrigin("http://localhost:3000")
    @GetMapping("/")
    public ResponseEntity login(@AuthenticationPrincipal PrincipalUser principalUser,
                                Authentication authentication,
                                HttpServletRequest request,
                                HttpServletResponse response) {

        String token = tokenProviderService.createToken(authentication);

        boolean tokenValidator = tokenProviderImpl.validateToken(token);

        if (tokenValidator) {
            Authentication authentication1 = tokenProviderImpl.getAuthentication(token);

            log.info("getAuthorities = {}", authentication1.getAuthorities());
            log.info("getCredentials = {}", authentication1.getCredentials());
            log.info("getPrincipal = {}", authentication1.getPrincipal());
            log.info("getName = {}", authentication1.getName());
            log.info("getDetails = {}", authentication1.getDetails());

        }

        return new ResponseEntity(token, HttpStatus.OK);
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication ) {

        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, authentication);

        return "redirect:/login";

    }


    @GetMapping("/token")
    public ResponseEntity getToken(@RequestBody NameDto nameDto) {

        log.info("nameDto = {}", nameDto.getName());

        return new ResponseEntity(HttpStatus.OK);

    }

    @Data
    static class NameDto {
        private String name;
    }



}
