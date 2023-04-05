package grimuri.backend.domain.diary.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;

public class DiaryRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class Create {

        @NotEmpty(message = "title을 입력해주세요.")
        private String title;

        @NotEmpty(message = "content를 입력해주세요.")
        private String content;

    }
}
