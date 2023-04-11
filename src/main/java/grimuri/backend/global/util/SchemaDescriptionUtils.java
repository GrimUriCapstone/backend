package grimuri.backend.global.util;

public class SchemaDescriptionUtils {

    public static class Diary {
        public static final String diaryId = "일기 항목의 Id";
        public static final String title = "일기의 제목";
        public static final String originalContent = "사용자가 입력한 일기의 원문";
        public static final String tags = "사용자가 입력한 일기의 태그 (요약)";
        public static final String imageSelected = "일기의 대표 이미지가 선택되었는지 여부";
        public static final String candidateImageUrls = "일기의 후보 이미지 목록. 대표 이미지가 선택되지 않았을 경우에만 존재하며," +
                "대표 이미지가 선택된 일기의 경우 해당 항목은 빈 값이다.";
        public static final String mainImageUrl = "일기의 대표 이미지 정보. 대표 이미지가 선택되었을 경우에만 존재하며," +
                "대표 이미지가 선택되지 않은 경우 해당 항목은 빈 값이다.";

    }

    public static class Tag {

        public static final String engTag = "영어 태그";
        public static final String korTag = "한국어 태그";
    }

    public static class ImageUrl {

        public static final String imageId = "이미지의 Id";
        public static final String imageUrl = "이미지의 S3 URL";
    }

}
