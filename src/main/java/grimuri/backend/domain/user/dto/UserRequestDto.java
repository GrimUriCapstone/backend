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
}
