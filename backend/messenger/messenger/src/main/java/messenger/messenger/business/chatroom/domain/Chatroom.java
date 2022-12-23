package messenger.messenger.business.chatroom.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import messenger.messenger.business.common.BaseEntity;
<<<<<<< HEAD
<<<<<<< HEAD
import messenger.messenger.business.user.domain.Users;

import javax.persistence.*;

import java.io.File;

=======
=======
>>>>>>> origin/backend
import messenger.messenger.auth.user.domain.Users;

import javax.persistence.*;

<<<<<<< HEAD
>>>>>>> 6989d4f78e3b521ac6460cc073e4094fd9ff1c50
=======
>>>>>>> origin/backend
import static javax.persistence.FetchType.LAZY;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Chatroom extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "chatroom_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    private String chatroomName;

    private int maxMember;

    private String chatFilePath;
}
