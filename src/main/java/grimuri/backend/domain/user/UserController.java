package grimuri.backend.domain.user;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import grimuri.backend.domain.user.dto.UserRequestDto;
import grimuri.backend.domain.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final FirebaseAuth firebaseAuth;
    private final UserService userService;

    @PostMapping("/signup")
    public UserResponseDto.UserInfo signup(@RequestHeader("Authorization") String authorization,
                                  @RequestBody UserRequestDto.Register registration) {
        FirebaseToken firebaseToken = userService.getFirebaseToken(firebaseAuth, authorization);
        User registeredUser
                = userService.register(firebaseToken.getUid(), firebaseToken.getEmail(), registration.getNickname());

        return UserResponseDto.UserInfo.of(registeredUser);
    }

    @GetMapping("/whoami")
    public UserResponseDto.UserInfo getUserInfo(@RequestHeader("Authorization") String authorization) {
        User loginUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return UserResponseDto.UserInfo.of(loginUser);
    }
}
