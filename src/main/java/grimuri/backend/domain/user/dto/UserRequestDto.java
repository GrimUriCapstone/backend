package grimuri.backend.domain.user.dto;

import lombok.*;

public class UserRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class Register {
        private String nickname;
    }
}
