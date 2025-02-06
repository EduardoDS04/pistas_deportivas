package com.example.demo.configuracion;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class ConfiSec {

    @Autowired
    DataSource dataSource;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
            .dataSource(dataSource)
            .usersByUsernameQuery(
                "select username, password, enabled " +
                "from usuario where username = ?")
            .authoritiesByUsernameQuery(
                "select username, tipo as 'authority' " +
                "from usuario where username = ?");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filtro(HttpSecurity httpSec) throws Exception {
        return httpSec
            .authorizeHttpRequests((request) -> request
                .requestMatchers("/register", "/webjars/**", "/img/**", "/login", "/logout", "/acerca", "/denegado", "/error")
                    .permitAll() // Permite acceso a estas rutas para todos
                .requestMatchers("/admin/**").hasAuthority("ADMIN") // Solo acceso a ADMIN 
                .requestMatchers("/admin/reservas/**").hasAnyAuthority("ADMIN") // Acceso a ADMIN 
                .requestMatchers("/mis-datos/**").hasAuthority("USUARIO") 
                .requestMatchers("/**").authenticated() // Todas las rutas requieren autenticación
            )
            // Manejo de excepciones para denegar el acceso
            .exceptionHandling((exception) -> exception.accessDeniedPage("/denegado"))
            .formLogin((formLogin) -> formLogin.loginPage("/login").permitAll())
            .logout((logout) -> logout.invalidateHttpSession(true).logoutSuccessUrl("/").permitAll())
            // Protección CSRF desactivada
            .csrf((protection) -> protection.disable())
            .build();
    }
}
