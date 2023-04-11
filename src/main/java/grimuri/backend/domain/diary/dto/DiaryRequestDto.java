package grimuri.backend.domain.diary.dto;

import grimuri.backend.global.util.SchemaDescriptionUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;

public class DiaryRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    @Schema(description = "일기 생성 요청 Body")
    public static class CreateRequest {

        @NotBlank(message = "title을 입력해주세요.")
        @Schema(description = SchemaDescriptionUtils.Diary.title)
        private String title;

        @NotBlank(message = "content를 입력해주세요.")
        @Schema(description = SchemaDescriptionUtils.Diary.originalContent)
        private String content;

    }
}
