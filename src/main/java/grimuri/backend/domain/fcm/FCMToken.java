package grimuri.backend.domain.fcm;

import grimuri.backend.domain.BaseTimeEntity;
import grimuri.backend.domain.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FCMToken extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fcmTokenId;

    @Column(nullable = false)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk", nullable = false)
    private User user;

    @Column
    private LocalDateTime lastLogin;

    public void setLastLogin(LocalDateTime localDateTime) {
        this.lastLogin = localDateTime;
    }
}
