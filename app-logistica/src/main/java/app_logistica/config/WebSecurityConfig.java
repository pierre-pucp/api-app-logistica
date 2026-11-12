package app_logistica.config;
import app_logistica.entity.Usuario;
import app_logistica.repository.UsuarioRepository;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;

import jakarta.servlet.http.HttpSession;

@Configuration
@EnableWebSecurity 
public class WebSecurityConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean 
    public UserDetailsManager userDetailsManager() {
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        
        users.setUsersByUsernameQuery(
            "SELECT email, pwd, activo FROM usuario WHERE email = ?"
        );
        
        users.setAuthoritiesByUsernameQuery(
            "SELECT u.email, r.nombre FROM usuario u " +
            "INNER JOIN rol r ON u.idrol = r.idrol WHERE u.email = ?"
        );
        
        return users;
    }

    @Bean 
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/catalogo/**", "/loginForm").permitAll()
            .requestMatchers("/productos/**").hasAnyAuthority("admin", "logistica")
            .requestMatchers("/proveedores/**").hasAuthority("admin")
            .anyRequest().authenticated()
        );

        http.formLogin(form -> form
            .loginPage("/loginForm")
            .loginProcessingUrl("/processLogin")
            .usernameParameter("email")
            .passwordParameter("pwd")
            .successHandler((request, response, authentication) -> {
                HttpSession session = request.getSession();
                
                String email = authentication.getName();
                Usuario usuario = usuarioRepository.findByEmail(email);
                session.setAttribute("usuario", usuario);

                DefaultSavedRequest savedRequest = 
                    (DefaultSavedRequest) session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");

                if (savedRequest != null) {
                    String targetUrl = savedRequest.getRedirectUrl();
                    response.sendRedirect(targetUrl);
                } else {
                    String redirectUrl = "/catalogo";
                    for (GrantedAuthority authority : authentication.getAuthorities()) {
                        String role = authority.getAuthority();
                        if ("admin".equals(role)) {
                            redirectUrl = "/proveedores";
                            break;
                        } else if ("logistica".equals(role)) {
                            redirectUrl = "/productos";
                            break;
                        }
                    }
                    response.sendRedirect(redirectUrl);
                }
            })
            .permitAll()
        );
        // .defaultSuccessUrl("/redirectByRol", true)

        http.logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/catalogo")
            .deleteCookies("JSESSIONID", "SESSION")
            .invalidateHttpSession(true)
            .clearAuthentication(true)
            .permitAll()
        );

        return http.build();
    }
}
