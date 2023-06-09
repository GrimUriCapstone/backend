package grimuri.backend.domain.diary.dto;

import grimuri.backend.domain.diary.Diary;
import grimuri.backend.domain.image.Image;
import grimuri.backend.global.util.SchemaDescriptionUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DiaryResponseDto {

    @Getter
    @AllArgsConstructor
    @Builder
    @ToString
    @Schema(description = "최근 작성 일기")
    public static class Recent {

        @Schema(description = SchemaDescriptionUtils.Diary.diaryId)
        private Long diaryId;

        @Schema(description = SchemaDescriptionUtils.Diary.title)
        private String title;

        @Schema(description = SchemaDescriptionUtils.Diary.writerEmail)
        private String writerEmail;

        @Schema(description = SchemaDescriptionUtils.Diary.writerNickname)
        private String writerNickname;

        @Schema(description = SchemaDescriptionUtils.Diary.originalContent)
        private String originalContent;

        @Schema(description = SchemaDescriptionUtils.Diary.tags)
        private List<Tag> tags;

        @Schema(description = SchemaDescriptionUtils.Diary.mainImageUrl)
        private ImageUrl mainImageUrl;

        @Schema(description = SchemaDescriptionUtils.UserInfo.profileImage)
        private String profileImage;

        @Schema(description = SchemaDescriptionUtils.Diary.createdAt)
        private LocalDateTime createdAt;

        @Schema(description = SchemaDescriptionUtils.Diary.modifiedAt)
        private LocalDateTime modifiedAt;

        public static Recent of(Diary diary) {
            return Recent.builder()
                    .diaryId(diary.getId())
                    .title(diary.getTitle())
                    .writerEmail(diary.getUser().getEmail())
                    .writerNickname(diary.getUser().getNickname())
                    .originalContent(diary.getOriginalContent())
                    .tags(Tag.listOf(diary))
                    .mainImageUrl(ImageUrl.of(diary.getImageList().get(0)))
                    .profileImage(diary.getUser().getProfileImage())
                    .createdAt(diary.getCreatedAt())
                    .modifiedAt(diary.getModifiedAt())
                    .build();
        }
    }

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

        @Schema(description = SchemaDescriptionUtils.Diary.open)
        private Boolean open;

        public static Create of(Diary diary) {
            return Create.builder()
                    .diaryId(diary.getId())
                    .title(diary.getTitle())
                    .originalContent(diary.getOriginalContent())
                    .open(diary.getOpen())
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

        @Schema(description = SchemaDescriptionUtils.Diary.open)
        private Boolean open;

        @Schema(description = SchemaDescriptionUtils.UserInfo.email)
        private String email;

        @Schema(description = SchemaDescriptionUtils.UserInfo.username)
        private String username;

        @Schema(description = SchemaDescriptionUtils.UserInfo.profileImage)
        private String profileImage;

        @Schema(description = SchemaDescriptionUtils.Diary.createdAt)
        private LocalDateTime createdAt;

        @Schema(description = SchemaDescriptionUtils.Diary.modifiedAt)
        private LocalDateTime modifiedAt;


        public static DiaryResponse imageSelectedOf(Diary diary) {
            return DiaryResponse.builder()
                    .diaryId(diary.getId())
                    .title(diary.getTitle())
                    .originalContent(diary.getOriginalContent())
                    .tags(Tag.listOf(diary))
                    .imageSelected(true)
                    .candidateImageUrls(new ArrayList<>())
                    .mainImageUrl(ImageUrl.of(diary.getImageList().get(0)))
                    .open(diary.getOpen())
                    .email(diary.getUser().getEmail())
                    .username(diary.getUser().getUsername())
                    .profileImage(diary.getUser().getProfileImage())
                    .createdAt(diary.getCreatedAt())
                    .modifiedAt(diary.getModifiedAt())
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
                    .open(diary.getOpen())
                    .email(diary.getUser().getEmail())
                    .username(diary.getUser().getUsername())
                    .profileImage(diary.getUser().getProfileImage())
                    .createdAt(diary.getCreatedAt())
                    .modifiedAt(diary.getModifiedAt())
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
            if (diary.getShortContent() == null) {
                return Collections.emptyList();
            }

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

        @Schema(description = SchemaDescriptionUtils.Image.imageId)
        private Long imageId;

        @Schema(description = SchemaDescriptionUtils.Image.imageUrl)
        private String imageUrl;

        public static ImageUrl of(Image image) {
            return ImageUrl.builder()
                    .imageId(image.getId())
                    .imageUrl(image.getSourceUrl())
                    .build();
        }
    }


}
