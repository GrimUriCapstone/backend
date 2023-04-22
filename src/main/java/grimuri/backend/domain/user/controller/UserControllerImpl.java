package grimuri.backend.domain.user.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import grimuri.backend.domain.fcm.FCMTokenService;
import grimuri.backend.domain.user.User;
import grimuri.backend.domain.user.UserService;
import grimuri.backend.domain.user.dto.UserRequestDto;
import grimuri.backend.domain.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserControllerImpl implements UserController {

    private final FirebaseAuth firebaseAuth;
    private final UserService userService;
    private final FCMTokenService fcmTokenService;

    @PostMapping("/fcmtoken")
    @Override
    public ResponseEntity<?> postLoginFCMToken(@RequestBody UserRequestDto.FCMTokenRequest tokenRequest) {
        User loginUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        fcmTokenService.registerFCMToken(loginUser.getEmail(), tokenRequest.getFcm_token());

        return ResponseEntity.status(HttpStatus.OK).body("success");
    }

    /**
     * @param authorization Authorization Header
     * @param registration  사용자 등록 추가 정보
     * @return ResponseEntity - UserResponseDto.AfterSignup
     */
    @PostMapping("/signup")
    @Override
    public ResponseEntity<UserResponseDto.AfterSignup> signup(@RequestHeader("Authorization") String authorization,
                                                              @RequestBody UserRequestDto.Register registration) {
        FirebaseToken firebaseToken = userService.getFirebaseToken(firebaseAuth, authorization);
        User registeredUser
                = userService.register(firebaseToken, registration);

        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponseDto.AfterSignup.of(registeredUser));
    }

    /**
     *
     * @param authorization Authorization Header
     * @return ResponseEntity - UserResponseDto.UserInfo
     */
    @GetMapping("/whoami")
    @Override
    public ResponseEntity<UserResponseDto.UserInfo> getUserInfo(@RequestHeader("Authorization") String authorization) {
        User loginUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        FirebaseToken firebaseToken = userService.getFirebaseToken(firebaseAuth, authorization);

        return ResponseEntity.status(HttpStatus.OK).body(UserResponseDto.UserInfo.of(loginUser, firebaseToken.getPicture()));
    }
}
