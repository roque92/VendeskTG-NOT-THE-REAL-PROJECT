package com.vendeskgt.vendeskgt.service;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.vendeskgt.vendeskgt.domain.entity.TenantEntity;
import com.vendeskgt.vendeskgt.repository.TenantRepository;
import com.vendeskgt.vendeskgt.resend.ResendWelcomeEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class TenantEmailverificationService {

    private static final Logger log = LoggerFactory.getLogger(TenantEmailverificationService.class);

    private final TenantRepository repository;
    private final ResendWelcomeEmailService welcomeEmailService;
    private final FirebaseAuth firebaseAuth;

    public TenantEmailverificationService(TenantRepository repository,
                                          ResendWelcomeEmailService welcomeEmailService,
                                          FirebaseAuth firebaseAuth) {
        this.repository = repository;
        this.welcomeEmailService = welcomeEmailService;
        this.firebaseAuth = firebaseAuth;
    }

    public APIGatewayProxyResponseEvent verification(APIGatewayProxyRequestEvent requestEvent){
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        responseEvent.setHeaders(Map.of("Content-Type", "application/json"));

        try{
            Map<String, String> queryParams = requestEvent.getQueryStringParameters();
            if(queryParams == null || !queryParams.containsKey("email")){
                return response(400, "{\"error\":\"Email requerido\"}");
            }

            String email = queryParams.get("email");

            // ✅ Verificar email en Firebase
            UserRecord user = firebaseAuth.getUserByEmail(email);

            if(!user.isEmailVerified()){
                return response(400, "{\"error\":\"El email no ha sido verificado todavía\"}");
            }

            // ✅ Revisar si existe en la BD
            Optional<TenantEntity> tenantOpt = repository.findByContactEmail(email);
            if(tenantOpt.isEmpty()){
                return response(404, "{\"error\":\"Usuario no encontrado\"}");
            }

            TenantEntity entity = tenantOpt.get();

            // ✅ Si ya estaba activo, no duplicar proceso
            if(entity.isActive()){
                return response(200, "{\"message\":\"Tu cuenta ya está activa\"}");
            }

            // ✅ Activar
            entity.setActive(true);
            repository.save(entity);
            log.info("Usuario activado: {}", email);

            // ✅ Email de bienvenida
            try {
                String fullName = entity.getName() + " " + entity.getLastName();
                welcomeEmailService.sendWelcomeEmail(email, fullName);
            } catch (Exception ex) {
                log.error("Fallo enviando email de bienvenida", ex);
            }

            log.info("Cuenta activada correctamente: {}", email);
            return response(200, "{\"message\":\"Cuenta activada correctamente\"}");

        } catch (Exception e){
            log.error("Error en verificación", e);
            return response(500, "{\"error\":\"Error interno\"}");
        }
    }

    private APIGatewayProxyResponseEvent response(int status, String body){
        return new APIGatewayProxyResponseEvent().withStatusCode(status).withBody(body);
    }
}
