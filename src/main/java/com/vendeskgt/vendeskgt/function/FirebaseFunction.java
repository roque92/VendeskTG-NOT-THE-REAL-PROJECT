package com.vendeskgt.vendeskgt.function;

import com.vendeskgt.vendeskgt.firebase.FirebaseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Configuration
public class FirebaseFunction {

    private final FirebaseUserService userService;

    @Autowired
    public FirebaseFunction(FirebaseUserService userService) {
        this.userService = userService;
    }

    @Bean
    public Supplier<List<Map<String, String>>> listDisabledUsers() {
        return () -> {
            try {
                return userService.listAndDeleteAllUsers();
            } catch (Exception e) {
                throw new RuntimeException("Error al obtener usuarios deshabilitados", e);
            }
        };
    }
}