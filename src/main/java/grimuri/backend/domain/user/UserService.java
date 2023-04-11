package grimuri.backend.domain.user;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import grimuri.backend.domain.user.dto.UserRequestDto;
import grimuri.backend.domain.user.dto.UserResponseDto;
import grimuri.backend.global.util.RequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 메소드 이름은 loadUserByUsername이지만, ID를 email로 사용하므로 email을 통해 User 엔티티를 조회한다.
     * @param email the username identifying the user whose data is required.
     * @return email로 찾은 User 엔티티
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findById(email).orElseThrow(() -> {
            throw new UsernameNotFoundException("user email not found");
        });

        return user;
    }

    /**
     * FirebaseToken의 정보와 추가 정보를 통해 회원 정보를 생성한다.
     * @param firebaseToken Authorization 헤더로부터 생성한 FirebaseToken
     * @param registerDto 회원가입 요청 시 작성한 추가 정보
     * @return 등록된 회원 정보
     */
    public User register(FirebaseToken firebaseToken, UserRequestDto.Register registerDto) {
        User user = User.builder()
                .email(firebaseToken.getEmail())
                .username(firebaseToken.getName())
                .nickname(registerDto.getNickname())
                .role(Role.USER)
                .build();
        return userRepository.save(user);
    }

    /**
     * Authorization 헤더에 있는 토큰 값을 Firebase Auth를 통해 검증하고, FirebaseToken으로 만들어 반환한다.
     * @param firebaseAuth
     * @param authorization Authorization Header
     * @return FirebaseToken
     * @throws ResponseStatusException
     */
    public FirebaseToken getFirebaseToken(FirebaseAuth firebaseAuth, String authorization) {

        try {
            String token = RequestUtil.getAuthorizationToken(authorization);

            return firebaseAuth.verifyIdToken(token);
        } catch (FirebaseAuthException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
