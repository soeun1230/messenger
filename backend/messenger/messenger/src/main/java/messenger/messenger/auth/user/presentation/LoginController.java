package messenger.messenger.auth.user.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import messenger.messenger.auth.oauth.domain.PrincipalUser;
import messenger.messenger.auth.token.application.TokenProviderService;
import messenger.messenger.auth.token.domain.TokenProviderImpl;
import messenger.messenger.auth.user.application.FormLoginUserDto;
import messenger.messenger.auth.user.application.FormRegisterUserDto;
import messenger.messenger.auth.user.application.UserService;
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

@RestController
@Slf4j
@CrossOrigin("http://localhost:3000")
@RequiredArgsConstructor
public class LoginController {

    private final TokenProviderImpl tokenProviderImpl;
    private final TokenProviderService tokenProviderService;

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;


    /**
     *
     * 로그인 요청 콜백
     * 상태코드
     * NOT_FOUND: 404 (principalUser가 없을 경우, 혹은 인증이 안된 경우, UNAUTHORIZED 404로 통일)
     * OK: 200 (로그인 완료, token 발급)
     *
     */
    @GetMapping("/")
    public ResponseEntity login(@AuthenticationPrincipal PrincipalUser principalUser,
                                Authentication authentication) {

        if (principalUser == null) return new ResponseEntity(HttpStatus.NOT_FOUND);

        String token = tokenProviderService.createToken(authentication);
        log.info("token = {}", token);

        if (!tokenProviderImpl.validateToken(token)) return new ResponseEntity(HttpStatus.NOT_FOUND);

        return new ResponseEntity(token, HttpStatus.OK);
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
    public String logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication ) {

        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, authentication);

        return "redirect:/login";

    }

//    @PostMapping("/api/v1/login")
//    public ResponseEntity formLogin(@Valid @RequestBody FormLoginUserDto formLoginUserDto,
//                                    BindingResult bindingResult) {
//
//        if (bindingResult.hasErrors()) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//
//        return new ResponseEntity(HttpStatus.OK);
//    }

}
