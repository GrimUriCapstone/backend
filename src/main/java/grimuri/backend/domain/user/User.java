package grimuri.backend.domain.user;

import grimuri.backend.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Entity @Table(name = "user_table")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "user_seq")
    private Long seq;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;
}
