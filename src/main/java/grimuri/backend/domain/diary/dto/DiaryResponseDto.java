package grimuri.backend.domain.diary.dto;

import grimuri.backend.domain.image.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class DiaryResponseDto {

    @Getter
    @AllArgsConstructor
    @Builder
    @ToString
    public static class CandidateImageUrl {

        Long imageId;
        String imageUrl;

        public static CandidateImageUrl of(Image image) {
            return CandidateImageUrl.builder()
                    .imageId(image.getId())
                    .imageUrl(image.getSourceUrl())
                    .build();
        }
    }

}
