package grimuri.backend.domain.diary.dto;

import grimuri.backend.domain.diary.Diary;
import lombok.*;


public class DiaryMessageDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Generate {
        private Long diaryId;
        private String originalContent;
        private String userEmail;

        private Integer themeId;

        private Integer styleId;

        public static DiaryMessageDto.Generate of(Diary diary, Integer themeId, Integer styleId) {
            return Generate.builder()
                    .diaryId(diary.getId())
                    .originalContent(diary.getOriginalContent())
                    .userEmail(diary.getUser().getEmail())
                    .themeId(themeId)
                    .styleId(styleId)
                    .build();
        }
    }
}
