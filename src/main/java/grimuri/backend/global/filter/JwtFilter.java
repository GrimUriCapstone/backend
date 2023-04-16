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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

                log.debug("Request URI: {}", request.getRequestURI());
                if (request.getRequestURI().toString().equals("/api/v1/user/signup")) {
                    chain.doFilter(servletRequest, servletResponse);
                }

                if (request.getRequestURI().toString().equals("/api/v1/image/generate")) {
                    chain.doFilter(servletRequest, servletResponse);
                }

                // firebaseToken으로부터 User 정보를 가져와 SecurityContext에 저장한다.
                UserDetails userDetails = userDetailsService.loadUserByUsername(firebaseToken.getEmail());
                UsernamePasswordAuthenticationToken authentication
                        = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("Request URI={}, User={}", request.getRequestURI(), authentication.getPrincipal().toString());
                log.debug("\tRequest Remote Info={}:{}", request.getRemoteAddr(), request.getRemotePort());
            } catch (IllegalArgumentException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.getWriter().write("{\"code\":\"INVALID_TOKEN\", \"message\":\"" + e.getMessage() + "\"}");

                return;
            } catch (FirebaseAuthException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"code\":\"INVALID_TOKEN\", \"message\":\"" + e.getMessage() + "\"}");

                return;
            } catch (UsernameNotFoundException e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setContentType("application/json");
                response.getWriter().write("{\"code\":\"USER_NOT_FOUND\", \"message\":\"\"" + e.getMessage() + "\"}");

                return;
            }
        } else {
            if (!request.getRequestURI().startsWith("/swagger-ui") && !request.getRequestURI().startsWith("/api-docs")) {
                log.debug("Request URI={}, Authorization 헤더 없음.", request.getRequestURI());
                log.debug("\tRequest Remote Info={}:{}", request.getRemoteAddr(), request.getRemotePort());
            }
        }

        chain.doFilter(request, response);
    }
}
