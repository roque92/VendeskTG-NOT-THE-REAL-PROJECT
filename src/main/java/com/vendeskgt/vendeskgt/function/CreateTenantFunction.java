package com.vendeskgt.vendeskgt.function;

import com.vendeskgt.vendeskgt.domain.dto.TenantRegistrationResponse;
import com.vendeskgt.vendeskgt.domain.dto.TenantWithSubscriptionRegistrationRequest;
import com.vendeskgt.vendeskgt.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class CreateTenantFunction {

    @Autowired
    private TenantService tenantService;


    @Bean
    public Function<TenantWithSubscriptionRegistrationRequest, TenantRegistrationResponse> registerCustomer(){
        return tenantService::register;
    }
}
