package grimuri.backend.domain.fcm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FCMTokenService {

    private final FCMTokenRepository fcmTokenRepository;

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
