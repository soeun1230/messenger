package messenger.messenger.home.presentation;

import lombok.RequiredArgsConstructor;
import messenger.messenger.auth.token.domain.TokenProvider;
import messenger.messenger.auth.token.presentation.dto.TokenDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

//
//    @PostMapping("/authenticate")
//    public ResponseEntity<TokenDto> authorize(@Validated @RequestBody)

}
