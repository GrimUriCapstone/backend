package grimuri.backend.domain.diary.dto;

import grimuri.backend.domain.diary.Diary;
import grimuri.backend.domain.image.Image;
import grimuri.backend.global.util.SchemaDescriptionUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DiaryResponseDto {

    @Getter
    @AllArgsConstructor
    @Builder
    @ToString
    @Schema(description = "사용자가 생성한 일기 항목의 정보")
    public static class Create {

        @Schema(description = SchemaDescriptionUtils.Diary.diaryId)
        private Long diaryId;

        @Schema(description = SchemaDescriptionUtils.Diary.title)
        private String title;

        @Schema(description = SchemaDescriptionUtils.Diary.originalContent)
        private String originalContent;

        public static Create of(Diary diary) {
            return Create.builder()
                    .diaryId(diary.getId())
                    .title(diary.getTitle())
                    .originalContent(diary.getOriginalContent())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    @ToString
    @Schema(description = "일기 정보")
    public static class DiaryResponse {

        @Schema(description = SchemaDescriptionUtils.Diary.diaryId)
        private Long diaryId;

        @Schema(description = SchemaDescriptionUtils.Diary.title)
        private String title;

        @Schema(description = SchemaDescriptionUtils.Diary.originalContent)
        private String originalContent;

        @Schema(description = SchemaDescriptionUtils.Diary.tags)
        private List<Tag> tags;

        @Schema(description = SchemaDescriptionUtils.Diary.imageSelected)
        private Boolean imageSelected;

        @Schema(description = SchemaDescriptionUtils.Diary.candidateImageUrls)
        private List<ImageUrl> candidateImageUrls;

        @Schema(description = SchemaDescriptionUtils.Diary.mainImageUrl)
        private ImageUrl mainImageUrl;

        public static DiaryResponse imageSelectedOf(Diary diary) {
            return DiaryResponse.builder()
                    .diaryId(diary.getId())
                    .title(diary.getTitle())
                    .originalContent(diary.getOriginalContent())
                    .tags(Tag.listOf(diary))
                    .imageSelected(true)
                    .candidateImageUrls(new ArrayList<>())
                    .mainImageUrl(ImageUrl.of(diary.getImageList().get(0)))
                    .build();
        }

        public static DiaryResponse imageUnSelectedOf(Diary diary) {
            return DiaryResponse.builder()
                    .diaryId(diary.getId())
                    .title(diary.getTitle())
                    .originalContent(diary.getOriginalContent())
                    .tags(new ArrayList<>())
                    .imageSelected(false)
                    .candidateImageUrls(diary.getImageList()
                            .stream().map(ImageUrl::of)
                            .collect(Collectors.toList()))
                    .mainImageUrl(null)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    @ToString
    @Schema(description = "일기를 요약한 결과인 Tag이다. 영어 태그와 한국어 태그로 이루어져 있다.")
    public static class Tag {

        @Schema(description = SchemaDescriptionUtils.Tag.engTag)
        private String engTag;

        @Schema(description = SchemaDescriptionUtils.Tag.korTag)
        private String korTag;

        public static Tag of(String pairStr) {
            String[] pair = pairStr.split(":");

            return Tag.builder()
                    .engTag(pair[0])
                    .korTag(pair[1])
                    .build();
        }

        public static List<Tag> listOf(Diary diary) {
            return Arrays.stream(diary.getShortContent().split(","))
                    .map(Tag::of)
                    .collect(Collectors.toList());
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    @ToString
    @Schema(description = "이미지의 정보이다. 이미지의 id와 URL로 이루어져있다.")
    public static class ImageUrl {

        @Schema(description = SchemaDescriptionUtils.ImageUrl.imageId)
        private Long imageId;

        @Schema(description = SchemaDescriptionUtils.ImageUrl.imageUrl)
        private String imageUrl;

        public static ImageUrl of(Image image) {
            return ImageUrl.builder()
                    .imageId(image.getId())
                    .imageUrl(image.getSourceUrl())
                    .build();
        }
    }


}
