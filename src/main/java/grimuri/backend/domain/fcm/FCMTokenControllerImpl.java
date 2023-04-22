package grimuri.backend.domain.fcm;

import com.google.firebase.messaging.FirebaseMessagingException;
import grimuri.backend.domain.image.ImageService;
import grimuri.backend.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/fcm")
public class FCMTokenControllerImpl implements FCMTokenController {

    private final ImageService imageService;
    private final FCMTokenRepository fcmTokenRepository;

    @GetMapping("/notification-test")
    @Override
    public ResponseEntity<?> testNotification(@RequestParam Long diaryId, @RequestParam String diaryTitle) {
        User loginUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<FCMToken> tokenList = fcmTokenRepository.findAllByUser_Email(loginUser.getEmail());
        try {
            imageService.notifyImageComplete(tokenList, diaryId, diaryTitle);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body("success");
    }
}
