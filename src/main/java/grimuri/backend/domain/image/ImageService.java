package grimuri.backend.domain.image;

import com.google.firebase.messaging.*;
import grimuri.backend.domain.diary.Diary;
import grimuri.backend.domain.diary.DiaryRepository;
import grimuri.backend.domain.fcm.FCMToken;
import grimuri.backend.domain.fcm.FCMTokenRepository;
import grimuri.backend.domain.fcm.FCMTokenService;
import grimuri.backend.domain.image.dto.ImageRequestDto;
import grimuri.backend.domain.user.User;
import grimuri.backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ImageService {

    private final DiaryRepository diaryRepository;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final FCMTokenRepository fcmTokenRepository;
    private final FCMTokenService fcmTokenService;


    @Async
    public void saveImageWithDiary(ImageRequestDto.Complete request) {
        Diary findDiary = diaryRepository.findById(request.getDiaryId()).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Diary가 존재하지 않습니다.");
        });

        User writerUser = userRepository.findById(request.getUserEmail()).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User가 존재하지 않습니다.");
        });

        String tagStr = request.getTags().stream()
                .map(tag -> tag.getEng() + ":" + tag.getKor())
                .collect(Collectors.joining(","));
        findDiary.saveTags(tagStr);
        findDiary.setImageCreated(true);

        request.getImages().forEach(url -> {
            Image eachImage = Image.builder()
                    .sourceUrl(url)
                    .diary(findDiary)
                    .build();

            imageRepository.save(eachImage);
        });

        List<FCMToken> tokenList = fcmTokenRepository.findAllByUser_Email(writerUser.getEmail());
        log.debug("\tToken List Size: {}", tokenList.size());

        // TODO: 예외 처리
        try {
            notifyImageComplete(tokenList, findDiary.getId(), findDiary.getTitle());
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }

    // TODO: Platform 별 Configuration
    /**
     * Token List에 있는 FCM Token들에 Notification을 생성하고 발송한다.
     * @param tokenList Notification을 발송하고자 하는 FCM Token의 List
     * @param diaryId 일기 생성이 완료되었다고 알릴 diaryId
     * @param diaryTitle 일기 생성이 완료되었다고 알릴 diary의 Title
     * @throws FirebaseMessagingException
     */
    public void notifyImageComplete(List<FCMToken> tokenList, Long diaryId, String diaryTitle) throws FirebaseMessagingException {
        Notification notification = Notification.builder()
                .setTitle("이미지 생성 완료!")
                .setBody("일기 \"" + diaryTitle + "\"의 이미지 생성이 완료되었습니다.")
                .build();

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(notification)
                .putData("diaryId", String.valueOf(diaryId))
                .putData("diaryTitle", diaryTitle)
                .addAllTokens(tokenList.stream().map(FCMToken::getToken).collect(Collectors.toList()))
                .build();

        BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);

        // success 시 failCount = 0, fail 시 failCount++
        List<SendResponse> responses = response.getResponses();
        for (int i = 0; i < responses.size(); i++) {
            SendResponse sendResponse = responses.get(i);
            FCMToken fcmToken = tokenList.get(i);

            if (sendResponse.isSuccessful()) {
                fcmToken.setFailCount(0L);
            } else {
                fcmToken.setFailCount(fcmToken.getFailCount() + 1);
                log.debug("\tFailed Token: {}", fcmToken.getToken());
            }
        }
    }
}
