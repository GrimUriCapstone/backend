package grimuri.backend.domain.image.dto;

import grimuri.backend.global.util.SchemaDescriptionUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

public class ImageRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    @Schema(description = "일기 생성 요청 Body")
    public static class Complete {

        @Schema(description = SchemaDescriptionUtils.Diary.diaryId)
        private Long diaryId;

        @Schema(description = SchemaDescriptionUtils.ImageComplete.tags)
        private List<Tag> tags;

        @Schema(description = SchemaDescriptionUtils.UserInfo.email)
        private String userEmail;

        @Schema(description = SchemaDescriptionUtils.ImageComplete.images)
        private List<String> images;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    @Schema(description = "일기를 요약한 결과인 Tag이다. 영어 태그와 한국어 태그로 이루어져 있다.")
    public static class Tag {

        @Schema(description = SchemaDescriptionUtils.Tag.korTag)
        private String kor;

        @Schema(description = SchemaDescriptionUtils.Tag.engTag)
        private String eng;
    }
}
