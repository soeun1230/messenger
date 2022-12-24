package messenger.messenger.business.friend.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import messenger.messenger.auth.user.application.UserService;
import messenger.messenger.auth.user.domain.Users;
import messenger.messenger.business.friend.application.FriendService;
import messenger.messenger.business.friend.presentation.dto.MyFriendSearchReqDto;
import messenger.messenger.business.friend.presentation.dto.MyFriendsReqDto;
import messenger.messenger.business.friend.presentation.dto.MyCrudFriendDto;
import messenger.messenger.business.friend.infra.query.dto.MyFriendsDto;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/friends")
public class FriendController {

    private final FriendService friendService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity getMyFriends(
            @RequestHeader("Authorization") String accessToken,
            @RequestBody MyFriendsReqDto reqDto) {

        Users user = userService.findUserForAuthentication(SecurityContextHolder.getContext().getAuthentication());
        Page<MyFriendsDto> myFriends = friendService.getMyFriendsDtoPageComplex(reqDto, user.getId());

        return new ResponseEntity(myFriends, HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity addFriend(
            @RequestHeader("Authorization") String accessToken,
            @RequestBody MyCrudFriendDto reqDto) {

        Users user = userService.findUserForAuthentication(SecurityContextHolder.getContext().getAuthentication());
        friendService.save(user, userService.findOne(reqDto.getFriendId()));

        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity deleteFriend(
            @RequestHeader("Authorization") String accessToken,
            @RequestBody MyCrudFriendDto reqDto) {

        friendService.deleteFriend(reqDto.getFriendId());

        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity searchFriend(
            @RequestHeader("Authorization") String accessToken,
            @RequestBody MyFriendSearchReqDto reqDto) {

        Users user = userService.findUserForAuthentication(SecurityContextHolder.getContext().getAuthentication());
        Page<MyFriendsDto> resDto = friendService.getMyFriendsSearchDtoPageComplex(reqDto, user.getId());

        return new ResponseEntity(resDto, HttpStatus.OK);
    }

}
