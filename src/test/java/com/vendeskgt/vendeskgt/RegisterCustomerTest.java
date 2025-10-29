package com.vendeskgt.vendeskgt;

import com.vendeskgt.vendeskgt.domain.dto.TenantRegistrationResponse;
import com.vendeskgt.vendeskgt.domain.dto.TenantWithSubscriptionRegistrationRequest;
import com.vendeskgt.vendeskgt.firebase.FirebaseCreateUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class RegisterCustomerTest {

    @Autowired
    private ApplicationContext context;

    @MockitoBean
    private FirebaseCreateUserService firebaseCreateUserService;

    @Test
    public void  functionBeanExists(){
        assertTrue(context.containsBean("registerCustomer"), "El bean registerCustomer no existe");
        Function<TenantWithSubscriptionRegistrationRequest, TenantRegistrationResponse> fn = context.getBean("registerCustomer", Function.class);
        assertNotNull(fn, "El Bean registerCustomer es null");
    }
}
