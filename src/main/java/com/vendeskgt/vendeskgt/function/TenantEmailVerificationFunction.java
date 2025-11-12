package com.vendeskgt.vendeskgt.function;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.vendeskgt.vendeskgt.service.TenantEmailverificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class TenantEmailVerificationFunction {


    private final TenantEmailverificationService tenantEmailverificationService;

    @Autowired
    public TenantEmailVerificationFunction(TenantEmailverificationService tenantEmailverificationService) {
        this.tenantEmailverificationService = tenantEmailverificationService;
    }

    @Bean
    public Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> verifyEmail(){
        return tenantEmailverificationService::verification;
    }
}
