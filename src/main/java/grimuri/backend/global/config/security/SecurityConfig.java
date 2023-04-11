package grimuri.backend.global.config.security;


import com.google.firebase.auth.FirebaseAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticateEntryPoint jwtAuthenticateEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final CorsConfigurationSource corsConfigurationSource;
    private final UserDetailsService userDetailsService;

    private final FirebaseAuth firebaseAuth;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic().disable()
                .csrf().disable()
                .cors().configurationSource(corsConfigurationSource)

                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticateEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers("/swagger-ui/index.html", "/swagger-ui.html", "/swagger/**", "/swagger-ui/**",
                        "/api-docs/**", "/favicon.ico")
                    .permitAll()
                .antMatchers("/api/v1/user/signup")
                    .permitAll()
                .anyRequest()
                    .hasAnyRole("ADMIN", "USER")

                .and()
                .apply(new JwtSecurityConfig(userDetailsService, firebaseAuth));

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().antMatchers("/error");
    }
}
