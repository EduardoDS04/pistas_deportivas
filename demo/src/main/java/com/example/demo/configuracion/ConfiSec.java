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
                // Rutas públicas
                .requestMatchers(
                    "/register", "/webjars/**", "/img/**", "/login", "/logout", "/acerca", "/denegado")
                    .permitAll()


                // Acceso exclusivo para ADMIN
                .requestMatchers("/**")
                    .hasAuthority("ADMIN")

                // Acceso compartido para ADMIN y OPERARIO
                .requestMatchers("/admin/reservas/**")
                    .hasAnyAuthority("ADMIN", "OPERARIO")

                // Acceso autenticado para usuarios a sus datos personales y reservas
                .requestMatchers("/mis-datos/**", "/mis-datos/mis-reservas/**")
                    .authenticated())

            // Manejo de excepciones de acceso denegado
            .exceptionHandling((exception) -> exception
                .accessDeniedPage("/denegado"))

            // Configuración de login
            .formLogin((formLogin) -> formLogin
                .loginPage("/login")
                .permitAll())
            

            // Configuración de logout
            .logout((logout) -> logout
                .invalidateHttpSession(true)
                .logoutSuccessUrl("/")
                .permitAll())

            // Protección CSRF desactivada (puedes activarla según tus necesidades)
            .csrf((protection) -> protection.disable())

            .build();
    }
}
