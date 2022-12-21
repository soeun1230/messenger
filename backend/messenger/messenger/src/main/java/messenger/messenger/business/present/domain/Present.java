package messenger.messenger.business.present.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
<<<<<<< HEAD
import lombok.Value;
import messenger.messenger.business.chatroom.domain.Chatroom;
import messenger.messenger.business.common.BaseEntity;
import messenger.messenger.business.item.domain.Item;
import messenger.messenger.business.user.domain.Users;
=======
import messenger.messenger.business.chatroom.domain.Chatroom;
import messenger.messenger.business.common.BaseEntity;
import messenger.messenger.business.item.domain.Item;
import messenger.messenger.auth.user.domain.Users;
>>>>>>> 6989d4f78e3b521ac6460cc073e4094fd9ff1c50

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@NoArgsConstructor
@Getter
@AllArgsConstructor
public class Present extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "present_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sender_id")
    private Users sender;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "giver_id")
    private Users giver;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "chatroom_id")
    private Chatroom chatroom;

}
