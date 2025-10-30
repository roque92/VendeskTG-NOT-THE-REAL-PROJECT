package com.vendeskgt.vendeskgt.function;

import com.vendeskgt.vendeskgt.controllerAdvice.FirebaseCustomException;
import com.vendeskgt.vendeskgt.domain.dto.TenantRegistrationResponse;
import com.vendeskgt.vendeskgt.domain.dto.TenantWithSubscriptionRegistrationRequest;
import com.vendeskgt.vendeskgt.service.CreateTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Configuration
public class CreateTenantFunction {

    @Autowired
    private CreateTenantService createTenantService;

    @Bean
    public Function<TenantWithSubscriptionRegistrationRequest, Map<String, Object>> registerCustomer() {
        return request -> {
            try {
                // Llama al servicio y devuelve datos de éxito
                TenantRegistrationResponse resp = createTenantService.register(request);
                return Map.of(
                        "success", true,
                        "data", resp
                );
            } catch (FirebaseCustomException e) {
                // Maneja errores de Firebase
                return Map.of(
                        "error", Map.of(
                                "code", 400,
                                "message", e.getDescription(),
                                "errors", List.of(
                                        Map.of(
                                                "message", e.getDescription(),
                                                "domain", "global",
                                                "reason", e.getCode().name()
                                        )
                                )
                        )
                );
            } catch (Exception e) {
                // Otros errores inesperados
                return Map.of(
                        "error", Map.of(
                                "code", 500,
                                "message", e.getMessage(),
                                "errors", List.of(
                                        Map.of(
                                                "message", e.getMessage(),
                                                "domain", "global",
                                                "reason", "INTERNAL_ERROR"
                                        )
                                )
                        )
                );
            }
        };
    }
}
