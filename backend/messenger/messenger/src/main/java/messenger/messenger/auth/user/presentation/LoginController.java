package messenger.messenger.auth.user.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import messenger.messenger.auth.oauth.domain.PrincipalUser;
import messenger.messenger.auth.token.application.TokenProviderService;
import messenger.messenger.auth.user.application.AuthorityService;
import messenger.messenger.auth.user.application.dto.FormRegisterUserDto;
import messenger.messenger.auth.user.application.UserService;
import messenger.messenger.auth.user.domain.Authority;
import messenger.messenger.auth.user.domain.Users;
import messenger.messenger.auth.user.infra.AuthorityRepository;
import messenger.messenger.auth.user.presentation.dto.LoginDto;
import messenger.messenger.auth.user.presentation.dto.TokenResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@CrossOrigin("http://localhost:3000")
@RequiredArgsConstructor
public class LoginController {

    private final TokenProviderService tokenProviderService;

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    private final AuthorityService authorityService;


    /**
     *
     * 로그인 요청 콜백
     * 상태코드
     * NOT_FOUND: 400 (principalUser가 없을 경우, 혹은 인증이 안된 경우, UNAUTHORIZED 403이지만 404로 통일)
     * OK: 200 (로그인 완료, token, refreshToken 발급)
     *
     */
    @GetMapping("/")
    public ResponseEntity loginForProvideToken(@AuthenticationPrincipal PrincipalUser principalUser,
                                                    Authentication authentication) {

        log.info("principalUser = {}", principalUser);
        log.info("authentication = {}", authentication);

        if (principalUser == null) return new ResponseEntity(HttpStatus.NOT_FOUND);

        String accessToken = tokenProviderService.createAccessToken(authentication);
        String refreshToken = tokenProviderService.createRefreshToken(authentication);

        if (!tokenProviderService.validateToken(accessToken)) return new ResponseEntity(HttpStatus.NOT_FOUND);
        if (!tokenProviderService.validateToken(refreshToken)) return new ResponseEntity(HttpStatus.NOT_FOUND);

        return new ResponseEntity(new TokenResponseDto(accessToken, refreshToken), HttpStatus.OK);
    }



    /**
     *
     * 회원 가입 요청
     * 상태코드
     * BAD_REQUEST: 400 (요청한 파라미터의 타입 에러 혹은 바인딩 에러)
     * CONFLICT: 409 (요청한 회원가입 이메일 이미 존재)
     * OK: 200 (가입 완료)
     *
     * @param formRegisterUserDto
     * @param bindingResult
     * @return
     */
    @PostMapping("/api/v1/register")
    public ResponseEntity formRegister(@Valid @RequestBody FormRegisterUserDto formRegisterUserDto,
                                       BindingResult bindingResult) {

        if (bindingResult.hasErrors()) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (!userService.registerForm(formRegisterUserDto, passwordEncoder)) return new ResponseEntity<>(HttpStatus.CONFLICT);

        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("/api/v1/logout")
    public ResponseEntity logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication ) {

        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, authentication);

        return new ResponseEntity(HttpStatus.OK);

    }

    @GetMapping("/api/v1/test")
    public ResponseEntity tokenTest() {

        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/api/v1/login")
    public ResponseEntity login(@RequestBody LoginDto loginDto,
                                BindingResult bindingResult,
                                HttpServletRequest request,
                                HttpServletResponse response) {

        if (bindingResult.hasErrors()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Users users = userService.userLoginCheck(loginDto, passwordEncoder);

        log.info("users = {}", users);

        if (users == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        List<Authority> authorities = authorityService.findAuthorityByUser(users);

        log.info("authorities = {}", authorities.isEmpty());

        if (authorities.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        String accessToken = tokenProviderService.createAccessTokenFormLogin(users.getEmail(), authorities);
        String refreshToken = tokenProviderService.createRefreshTokenFormLogin(users.getEmail(), authorities);

        log.info("accessToken = {}", accessToken);
        log.info("refreshToken = {}", refreshToken);

        if (!tokenProviderService.validateToken(accessToken)) return new ResponseEntity(HttpStatus.NOT_FOUND);
        if (!tokenProviderService.validateToken(refreshToken)) return new ResponseEntity(HttpStatus.NOT_FOUND);

        log.info("accessToken Validate = {}", accessToken);
        log.info("refreshToken Validate = {}", refreshToken);


        return new ResponseEntity(new TokenResponseDto(accessToken, refreshToken), HttpStatus.OK);

    }

}
