package grimuri.backend.domain.diary.dto;

import grimuri.backend.domain.diary.Diary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class DiaryMessageDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Generate {
        private Long diaryId;
        private String originalContent;
        private String userEmail;

        public static DiaryMessageDto.Generate of(Diary diary) {
            return Generate.builder()
                    .diaryId(diary.getId())
                    .originalContent(diary.getOriginalContent())
                    .userEmail(diary.getUser().getEmail())
                    .build();
        }
    }
}
