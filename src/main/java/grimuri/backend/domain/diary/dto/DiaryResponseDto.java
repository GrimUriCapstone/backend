package grimuri.backend.domain.diary.dto;

import grimuri.backend.domain.diary.Diary;
import grimuri.backend.domain.image.Image;
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
    public static class Create {

        private Long diaryId;
        private String title;
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
    public static class DiaryResponse {

        private Long diaryId;
        private String title;
        private String originalContent;
        private List<Tag> tags;
        private Boolean imageSelected;
        private List<ImageUrl> candidateImageUrls;
        private ImageUrl mainImageUrl;

        public static DiaryResponse imageSelectedOf(Diary diary) {
            return DiaryResponse.builder()
                    .diaryId(diary.getId())
                    .title(diary.getTitle())
                    .originalContent(diary.getOriginalContent())
                    .tags(Tag.listOf(diary))
                    .imageSelected(true)
                    .candidateImageUrls(null)
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
    public static class Tag {

        private String engTag;
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
    public static class ImageUrl {

        private Long imageId;
        private String imageUrl;

        public static ImageUrl of(Image image) {
            return ImageUrl.builder()
                    .imageId(image.getId())
                    .imageUrl(image.getSourceUrl())
                    .build();
        }
    }


}
