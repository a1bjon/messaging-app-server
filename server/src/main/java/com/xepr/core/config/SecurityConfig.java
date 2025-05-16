package com.xepr.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        String username = null;
        String pass = null;

        try (BufferedReader br = new BufferedReader(new FileReader(new File("src/main/resources/cred.txt").getAbsolutePath()))) {
            String line;
            byte cnt = 0;

            while ((line = br.readLine()) != null) {
                cnt++;

                switch (cnt) {
                    case 1:
                        username = line;
                        break;

                    case 2:
                        pass = line;
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read API credential file");
        }

        UserDetails user = User.withUsername(username).password(new BCryptPasswordEncoder().encode(pass)).roles("USER").build();
        return new InMemoryUserDetailsManager(user);
    }
}
