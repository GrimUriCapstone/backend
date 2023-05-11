package grimuri.backend.domain.user.dto;

import grimuri.backend.global.util.SchemaDescriptionUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

public class UserRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class Register {

        @Schema(description = SchemaDescriptionUtils.UserRegister.nickname)
        private String nickname;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    @Schema(description = "로그인/로그아웃 시 FCM 토큰 정보 전송")
    public static class FCMTokenRequest {

        @Schema(description = SchemaDescriptionUtils.FCMToken.fcm_token)
        private String fcm_token;
    }

}
