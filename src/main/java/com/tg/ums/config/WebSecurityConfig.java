package com.tg.ums.config;

import com.tg.ums.service.auth.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF，便于前端测试
                .csrf(AbstractHttpConfigurer::disable)
                // 配置CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 配置会话管理
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                        .maximumSessions(1)
                )
                // 配置权限拦截规则
                .authorizeHttpRequests(authorize -> authorize
                        // 允许所有用户访问静态资源
                        .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/fonts/**", "/images/**").permitAll()
                        // 允许所有用户访问的接口
                        .requestMatchers("/api/auth/login", "/api/auth/logout", "/api/auth/current-user", "/api/auth/test-password", "/api/auth/generate-password", "/api/health").permitAll()
                        // 允许所有用户访问课程、专业、教师、批次、学期等基础数据接口
                        .requestMatchers("/api/courses/**", "/api/majors/**", "/api/teachers/**", "/api/batches/**", "/api/semesters/**").permitAll()
                        // 允许所有用户访问章节和知识点相关接口
                        .requestMatchers("/api/chapters/**", "/api/knowledge-points/**").permitAll()
                        // 允许所有用户访问题目相关接口
                        .requestMatchers("/api/questions/**", "/api/exam-papers/**").permitAll()
                        // 允许所有用户访问训练方案相关接口
                        .requestMatchers("/api/training-programs/**").permitAll()
                        // 允许所有用户访问教学计划相关接口
                        .requestMatchers("/api/teaching-plans/**").permitAll()
                        // 允许所有用户访问Swagger相关资源
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                // 允许所有认证用户访问用户管理相关接口
                .requestMatchers("/api/users/**").permitAll()
                // 其他所有请求都需要认证
                .anyRequest().authenticated()
                )
                // 配置用户信息服务
                .userDetailsService(customUserDetailsService);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
