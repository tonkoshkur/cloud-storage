package ua.tonkoshkur.cloudstorage.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ua.tonkoshkur.cloudstorage.user.UserDetailsServiceImpl;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String SIGN_IN_PAGE = "/auth/signin";
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .userDetailsService(userDetailsServiceImpl)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/auth/**").anonymous()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage(SIGN_IN_PAGE)
                        .loginProcessingUrl(SIGN_IN_PAGE)
                        .defaultSuccessUrl("/", true))
                .logout(logout -> logout
                        .logoutUrl("/auth/signout")
                        .logoutSuccessUrl(SIGN_IN_PAGE))
                .build();
    }
}
