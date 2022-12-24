package messenger.messenger.business.friend.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import messenger.messenger.auth.user.domain.Users;
import messenger.messenger.business.friend.presentation.dto.MyFriendSearchReqDto;
import messenger.messenger.business.friend.presentation.dto.MyFriendsReqDto;
import messenger.messenger.business.friend.domain.Friend;
import messenger.messenger.business.friend.infra.FriendQueryRepositoryImpl;
import messenger.messenger.business.friend.infra.FriendRepository;
import messenger.messenger.business.friend.infra.query.condition.MyFriendsCondition;
import messenger.messenger.business.friend.infra.query.condition.MyFriendsSearchCondition;
import messenger.messenger.business.friend.infra.query.dto.MyFriendsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FriendService {

    private final FriendQueryRepositoryImpl friendQueryRepository;
    private final FriendRepository friendRepository;

    /**
     *
     * 사용자의 친구 목록의 요청을 받아, 친구 리스트를 페이징화하여 제공
     *
     * @param reqDto
     * @param userId
     * @return
     */
    public Page<MyFriendsDto> getMyFriendsDtoPageComplex(MyFriendsReqDto reqDto, Long userId) {

        reqDto.updatePageable();
        log.info("userId = {}", userId);

        Pageable pageable = PageRequest.of(reqDto.getPage(), reqDto.getSize());
        MyFriendsCondition condition = new MyFriendsCondition(userId);

        return friendQueryRepository.getMyFriendsDtoPageComplex(condition, pageable);
    }

    /**
     * 친구 저장
     */
    public Long save(Users reqUser, Users resUser) {

        if (reqUser.getId() != resUser.getId()) {
            return friendRepository.save(new Friend(reqUser, resUser)).getId();
        } else {
            throw new IllegalArgumentException("본인을 친구추가 할 수 없습니다.");
        }

    }

    /**
     * 친구 Id로 친구 객체 찾기
     *
     */
    public Friend findOne(Long id) {
        Optional<Friend> findFriend = friendRepository.findById(id);
        return findFriend.orElseThrow(() -> {throw new IllegalArgumentException("존재하지 않는 회원입니다.");});
    }


    /**
     * 등록한 친구 목록의 유저 찾기
     */
    public Users findResUserFromFriendId(Long friendId) {

        Optional<Friend> findFriend = friendRepository.findById(friendId);
        return findFriend.orElseThrow(() -> {throw new IllegalArgumentException("존재하지 않는 회원입니다.");}).getResUser();
    }

    /**
     * 친구 삭제
     */
    public void deleteFriend(Long friendId) {
        Friend friend = findOne(friendId);
        friendRepository.deleteById(friend.getId());
    }

    /**
     *
     * 유저 아이디와 검색한 유저의 이름으로 부터 친구 목록 찾기
     *
     * @param reqDto (찾으려는 친구 유저 이름, 페이지, 사이즈)
     * @param userid (유저 Id)
     * @return
     */
    public Page<MyFriendsDto> getMyFriendsSearchDtoPageComplex(MyFriendSearchReqDto reqDto, Long userid) {

        reqDto.updatePageable();
        MyFriendsSearchCondition condition = new MyFriendsSearchCondition(userid, reqDto.getName());
        PageRequest pageable = PageRequest.of(reqDto.getPage(), reqDto.getSize());

        return friendQueryRepository.getMyFriendsSearchDtoPageComplex(condition, pageable);
    }
}
