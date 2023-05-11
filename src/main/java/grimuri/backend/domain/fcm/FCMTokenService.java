package grimuri.backend.domain.fcm;

import grimuri.backend.domain.user.User;
import grimuri.backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FCMTokenService {

    private final FCMTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;

    /**
     * 사용자의 특정 FCM Token 정보를 삭제한다.
     * @param email 삭제하려는 FCM Token의 사용자의 email (PK)
     * @param fcmTokenStr FCM Token 문자열
     */
    public void deleteFCMToken(String email, String fcmTokenStr) {
        FCMToken fcmToken = fcmTokenRepository.findByToken(fcmTokenStr).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "FCM Token Not Found");
        });

        if (fcmToken.getUser().getEmail().equals(email)) {
            fcmTokenRepository.delete(fcmToken);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User의 FCM Token이 아닙니다.");
        }
    }

    /**
     * FCM Token - 사용자 정보를 DB에 저장한다. 기존에 존재하는 Token-사용자 정보라면 마지막 로그인 시간을 갱신한다.
     * Token은 존재하지만 사용자 정보가 다르다면 해당 정보를 삭제하고 새로운 사용자 정보로 FCM Token을 재생성한다.
     * 아예 존재하지 않는 FCM Token - 사용자 정보라면 새로 생성하여 저장한다.
     * @param email FCM Token 정보를 저장할 사용자의 email (PK)
     * @param fcmTokenStr FCM Token 문자열
     */
    public void registerFCMToken(String email, String fcmTokenStr) {
        // email로 사용자 엔티티 조회
        User user = userRepository.findById(email).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user email not found");
        });

        // 해당 토큰이 DB에 존재하는지 확인
        fcmTokenRepository.findByToken(fcmTokenStr).ifPresentOrElse(
            // 존재하면, 사용자의 것인지 확인
            fcmToken -> {
                if (fcmToken.getUser().getEmail().equals(email)) {
                    // 사용자의 것이라면 마지막 로그인 시간 갱신
                    fcmToken.setLastLogin(LocalDateTime.now());
                } else {
                    // 사용자의 것이 아니라면 기존 토큰정보 삭제 후 재생성
                    fcmTokenRepository.delete(fcmToken);
                    createNewTokenAndSave(fcmTokenStr, user);
                }
            },
            // 없으면 새로 등록
            () -> {
                createNewTokenAndSave(fcmTokenStr, user);
            }
        );
    }

    /**
     * 토큰 문자열과 사용자 엔티티로 FCMToken 엔티티를 새로 생성하여 저장한다.
     * @param fcmTokenStr FCM Token 문자열
     * @param user 사용자 엔티티
     */
    private void createNewTokenAndSave(String fcmTokenStr, User user) {
        FCMToken newToken = FCMToken.builder()
                .token(fcmTokenStr)
                .user(user)
                .lastLogin(LocalDateTime.now())
                .failCount(0L)
                .build();

        fcmTokenRepository.save(newToken);
    }

    /**
     * 사용자의 이메일 주소를 통해 FCM Token의 목록을 조회하여 반환한다.
     * @param email FCM Token 목록을 조회할 사용자의 이메일
     * @return FCM 토큰(String)의 목록
     */
    public List<String> getTokenListByUser(String email) {
        List<FCMToken> tokenList = fcmTokenRepository.findAllByUser_Email(email);

        return tokenList.stream()
                .map(FCMToken::getToken)
                .collect(Collectors.toList());
    }
}
