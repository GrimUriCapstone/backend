package grimuri.backend.domain.user;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findById(username).orElseThrow(() -> {
            throw new UsernameNotFoundException("username not found");
        });

        return user;
    }

    public User register(String username, String email, String nickname) {
        User user = User.builder()
                .username(username)
                .email(email)
                .nickname(nickname)
                .role(Role.USER)
                .build();
        return userRepository.save(user);
    }

    public FirebaseToken getFirebaseToken(FirebaseAuth firebaseAuth, String authorization) {
        String token = RequestUtil.getAuthorizationToken(authorization);
        try {
            return firebaseAuth.verifyIdToken(token);
        } catch (FirebaseAuthException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
