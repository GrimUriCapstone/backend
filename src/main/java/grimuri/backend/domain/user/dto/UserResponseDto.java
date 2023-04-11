package grimuri.backend.domain.user.dto;

import grimuri.backend.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class UserResponseDto {

    @Getter
    @AllArgsConstructor
    @Builder
    @ToString
    public static class UserInfo {

        private String username;
        private String email;
        private String nickname;

        public static UserInfo of(User user) {
            return UserInfo.builder()
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .build();
        }
    }

}
