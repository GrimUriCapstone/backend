package grimuri.backend.global;

import grimuri.backend.domain.user.User;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;

@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RateLimiter rateLimiter;

    // TODO: 적용 대상 API 구분
    @Pointcut("within(grimuri.backend.domain.diary.controller..*)")
    public void onRequest() {}

    @Pointcut("execution(* grimuri.backend.domain.user.controller.UserControllerImpl.*(..))")
    public void onSignupAndLogin() {}

    @Around("onSignupAndLogin()")
    public Object signupAndLoginLimit(ProceedingJoinPoint pjp) throws Throwable {
        log.debug("\tAround: signupAndLoginLimit,\tMethod: {}", pjp.getSignature().getName());

        Bucket bucket = rateLimiter.resolveBucket(pjp.getSignature(), 10L, 1L, Duration.ofMinutes(1));
        if (bucket.tryConsume(1L)) {
            log.debug("\t>>> Remain bucket Count : {}", bucket.getAvailableTokens());

            return pjp.proceed(pjp.getArgs());
        } else {
            log.debug("\t>>> Exhausted Limit in Simple Bucket");

            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("too many requests");
        }
    }

    @Around("onRequest()")
    public Object onRequestLimit(ProceedingJoinPoint pjp) throws Throwable {
        log.debug("\tAround: onRequestLimit,\tMethod: {}", pjp.getSignature().getName());

        log.debug("\targs: {}", Arrays.stream(pjp.getArgs()));

        User loginUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = loginUser.getEmail();

        Bucket bucket = rateLimiter.resolveBucket(email, pjp.getSignature(), 100L, 1L, Duration.ofMinutes(1));
        if (bucket.tryConsume(1L)) {
            log.debug("\t>>> Remain bucket Count : {}", bucket.getAvailableTokens());

            return pjp.proceed(pjp.getArgs());
        } else {
            log.debug("\t>>> Exhausted Limit in Simple Bucket");

            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("too many requests");
        }
    }


}
