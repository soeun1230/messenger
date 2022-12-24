package messenger.messenger.business.school.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import messenger.messenger.business.school.application.SchoolService;
import messenger.messenger.business.school.application.dto.SchoolSearchReqDto;
import messenger.messenger.business.school.infra.repository.query.dto.SchoolSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/schools")
@RequiredArgsConstructor
public class SchoolController {

    private final SchoolService schoolService;

    @GetMapping("/search")
    public ResponseEntity getSchoolsSearchRes(@RequestHeader("Authorization") String accessToken,
                                              @RequestBody SchoolSearchReqDto schoolSearchReqDto) {

        Page<SchoolSearchDto> schoolSearchDto = schoolService.getSchoolSearchDto(schoolSearchReqDto);

        return new ResponseEntity(schoolSearchDto, HttpStatus.OK);
    }

}
