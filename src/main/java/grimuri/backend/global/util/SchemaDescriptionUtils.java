package grimuri.backend.global.util;

public class SchemaDescriptionUtils {

    public static class FCMToken {
        public static final String fcm_token = "사용자가 로그인한 기기의 FCM 토큰";
    }

    public static class AfterSignup {
        public static final String username = "사용자의 이름";
        public static final String email = "사용자의 이메일";
        public static final String nickname = "사용자의 별명";
    }

    public static class ImageComplete {
        public static final String tags = "일기의 요약된 태그 목록";
        public static final String images = "이미지들의 스토리지 접근 URL";
    }

    public static class UserRegister {

        public static final String nickname = "사용자가 사용할 별명";
    }

    public static class UserInfo {

        public static final String username = "사용자의 이름";
        public static final String email = "사용자의 이메일";
        public static final String nickname = "사용자의 별명";
        public static final String profileImage = "사용자의 프로필 이미지";
    }

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
        public static final String createdAt = "일기 작성 시간";
        public static final String modifiedAt = "일기 수정 시간";
        public static final String open = "공개 여부";
    }

    public static class Tag {

        public static final String engTag = "영어 태그";
        public static final String korTag = "한국어 태그";
    }

    public static class Image {

        public static final String imageId = "이미지의 Id";
        public static final String imageUrl = "이미지의 스토리지 URL";
    }

}
