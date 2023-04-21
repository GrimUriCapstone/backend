package grimuri.backend.domain.image;

import com.google.firebase.messaging.*;
import grimuri.backend.domain.diary.Diary;
import grimuri.backend.domain.diary.DiaryRepository;
import grimuri.backend.domain.fcm.FCMTokenService;
import grimuri.backend.domain.image.dto.ImageRequestDto;
import grimuri.backend.domain.user.User;
import grimuri.backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    private final FCMTokenService fcmTokenService;

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

        // TODO: 사용자의 클라이언트에 notify
        // TODO: 여기부터 Async로 처리할 수 있는지 리서치
        List<String> tokenList = fcmTokenService.getTokenListByUser(writerUser.getEmail());

        // TODO: 예외 처리
        try {
            notifyImageComplete(tokenList);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }

    // TODO: Notification 설정 - Title, Body, Image
    // TODO: Platform 별 Configuration
    // TODO: Custom Data Payload 설정
    private void notifyImageComplete(List<String> tokenList) throws FirebaseMessagingException {
        Notification notification = Notification.builder()
                .setTitle("Title")
                .setBody("body")
                .setImage("imageUrl")
                .build();

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(notification)
                .putData("score", "850")
                .putData("time", "2:45")
                .addAllTokens(tokenList)
                .build();

//        FirebaseMessaging.getInstance().sendMulticastAsync(message);
        BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);

        // fail 난 경우 확인
        if (response.getFailureCount() > 0) {
            List<SendResponse> responses = response.getResponses();
            for (int i = 0; i < responses.size(); i++) {
                if (responses.get(i).isSuccessful()) continue;

                log.debug("Failed Token: {}", tokenList.get(i));
            }
        }
    }
}
