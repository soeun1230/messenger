package messenger.messenger.business.user.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import messenger.messenger.business.user.domain.Users;
import messenger.messenger.business.user.infra.UsersRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsersService {

    private final UsersRepository usersRepository;

    private Long save(Users users) {
        Users saveUsers = usersRepository.save(users);
        return saveUsers.getId();
    }

    private Users findOne(Long id) throws IllegalAccessException {
        Optional<Users> optionalUser = usersRepository.findById(id);

        Users users = optionalUser.orElseThrow(() -> {
            throw new IllegalArgumentException("존재하지 않는 유저 입니다.");
        });

        return users;
    }

}
