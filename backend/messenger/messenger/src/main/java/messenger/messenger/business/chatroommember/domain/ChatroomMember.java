package messenger.messenger.business.chatroommember.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import messenger.messenger.business.common.BaseEntity;
<<<<<<< HEAD
<<<<<<< HEAD
import messenger.messenger.business.user.domain.Users;
=======
import messenger.messenger.auth.user.domain.Users;
>>>>>>> 6989d4f78e3b521ac6460cc073e4094fd9ff1c50
=======
import messenger.messenger.auth.user.domain.Users;
>>>>>>> origin/backend

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ChatroomMember extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "chatroom_member_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Users member;

}
