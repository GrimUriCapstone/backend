package grimuri.backend.domain.user.dto;

import grimuri.backend.domain.user.User;
import grimuri.backend.global.util.SchemaDescriptionUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class UserResponseDto {

    @Getter
    @AllArgsConstructor
    @Builder
    @ToString
    @Schema(description = "사용자 정보")
    public static class UserInfo {

        @Schema(description = SchemaDescriptionUtils.UserInfo.username)
        private String username;

        @Schema(description = SchemaDescriptionUtils.UserInfo.email)
        private String email;

        @Schema(description = SchemaDescriptionUtils.UserInfo.nickname)
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
