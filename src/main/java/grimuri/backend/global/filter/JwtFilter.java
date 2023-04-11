package grimuri.backend.global.filter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import grimuri.backend.global.util.RequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    private final UserDetailsService userDetailsService;
    private final FirebaseAuth firebaseAuth;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // Request의 Authorization Header에 담긴 Firebase ID Token을 검증한다.

        if (RequestUtil.isAuthorizationToken(request)) {
            FirebaseToken firebaseToken;
            try {
                String authToken = RequestUtil.getAuthorizationToken(request);
                firebaseToken = firebaseAuth.verifyIdToken(authToken);

                // firebaseToken으로부터 User 정보를 가져와 SecurityContext에 저장한다.
                UserDetails userDetails = userDetailsService.loadUserByUsername(firebaseToken.getUid());
                UsernamePasswordAuthenticationToken authentication
                        = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (FirebaseAuthException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"code\":\"INVALID_TOKEN\", \"message\":\"" + e.getMessage() + "\"}");

                return;
            } catch (NoSuchElementException e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setContentType("application/json");
                response.getWriter().write("{\"code\":\"USER_NOT_FOUND\"}");

                return;
            }
        } else {
            log.debug("Authorization 헤더 없음.");
        }

        chain.doFilter(request, response);
    }
}
