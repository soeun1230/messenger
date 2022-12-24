package messenger.messenger.business.school.presentation;

import com.google.gson.Gson;
import com.querydsl.jpa.impl.JPAQuery;
import messenger.messenger.auth.token.application.AuthService;
import messenger.messenger.auth.token.presentation.dto.TokenAuthDto;
import messenger.messenger.auth.user.application.AuthorityService;
import messenger.messenger.auth.user.application.UserService;
import messenger.messenger.auth.user.application.dto.FormRegisterUserDto;
import messenger.messenger.auth.user.domain.Authorities;
import messenger.messenger.auth.user.domain.Authority;
import messenger.messenger.auth.user.domain.Users;
import messenger.messenger.business.school.application.SchoolService;
import messenger.messenger.business.school.application.dto.SchoolSaveDto;
import messenger.messenger.business.school.application.dto.SchoolSearchReqDto;
import messenger.messenger.business.school.infra.repository.SchoolRepository;
import messenger.messenger.business.school.infra.repository.query.dto.SchoolSearchDto;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
class SchoolControllerTest {

    @Autowired UserService userService;
    @Autowired AuthService authService;
    @Autowired AuthorityService authorityService;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired SchoolService schoolService;
    @Mock SchoolController schoolController;

    static Users user;
    static List<Authority> authorities;
    static TokenAuthDto tokenAuthDto;
    static String accessToken;
    private MockMvc mockMvc;


    @BeforeEach
    public void dbInit() {
        userService.registerForm(FormRegisterUserDto.builder().email("k@naver.com").username("kose").password("1234").build(), passwordEncoder);
        user = userService.findByEmail("k@naver.com");
        authorities = authorityService.findAuthorityByUser(user);

        tokenAuthDto = authService.createFormTokenAuth("k@naver.com", authorities);
        accessToken = "Bearer " + tokenAuthDto.getAccessToken();

        mockMvc = MockMvcBuilders.standaloneSetup(schoolController).build();

        saveSchool();

    }

    @Test
    public void 학교_검색() throws Exception {

        //given
        SchoolSearchReqDto reqDto = requestDto();
        Page<SchoolSearchDto> resDto = schoolService.getSchoolSearchDto(reqDto);

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/schools/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(reqDto))
                        .header("Authorization", accessToken)
        );

        //then
        resultActions.andExpect(status().isOk());

    }
    

    private SchoolSearchReqDto requestDto() {
        return SchoolSearchReqDto.builder().page(0).size(10).schoolName("서울").build();
    }

    private void saveSchool() {

        String[] school = {"서울A대학교", "서울B대학교", "인천A대학교"};
        String[] address = {"서울시 A구", "서울시 B구", "인천시"};
        String[] regisNum = {"123", "124", "125"};

        for (int i=0; i< school.length; i++) {
            schoolService.save(new SchoolSaveDto(school[i], address[i], regisNum[i]));
        }
    }
}