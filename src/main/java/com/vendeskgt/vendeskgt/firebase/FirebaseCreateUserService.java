package com.vendeskgt.vendeskgt.firebase;

import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.vendeskgt.vendeskgt.domain.enums.RoleEnum;
import com.vendeskgt.vendeskgt.resend.ResendVerifyEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class FirebaseCreateUserService {

    private static final Logger log = LoggerFactory.getLogger(FirebaseCreateUserService.class);

    private final FirebaseAuth firebaseAuth;
    private final ResendVerifyEmailService emailService;

    @Autowired
    public FirebaseCreateUserService(FirebaseAuth firebaseAuth, ResendVerifyEmailService emailService) {
        this.firebaseAuth = firebaseAuth;
        this.emailService = emailService;
    }

    public void registerUser(String email, String password, UUID tenantId,RoleEnum role)
            throws FirebaseAuthException {

        UserRecord userRecord;
        email = email.toLowerCase();

        // 1️⃣ Crear usuario
        try {
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(password);

            userRecord = firebaseAuth.createUser(request);
            log.info("✅ Usuario creado en Firebase: {}", email);

        } catch (FirebaseAuthException e) {
            log.error("❌ Error creando usuario en Firebase: {}", e.getMessage());
            throw e;
        }

        // 2️⃣ Asignar Custom Claims
        try {
            Map<String, Object> claims = new HashMap<>();
            claims.put("tenantId", tenantId.toString());
            claims.put("role", role.name());

            firebaseAuth.setCustomUserClaims(userRecord.getUid(), claims);
            log.info("✅ Custom claims asignadas a usuario: {}", email);

        } catch (Exception e) {
            log.error("⚠️ Error asignando custom claims a {}: {}", email, e.getMessage());
        }

        // 3️⃣ Generar link de verificación de email
        String link = null;
        try {
            ActionCodeSettings settings = ActionCodeSettings.builder()
                    .setUrl("http://localhost:3000/tenants/email-verification?email=" +
                            URLEncoder.encode(email, StandardCharsets.UTF_8))
                    .setHandleCodeInApp(false)
                    .build();

            link = firebaseAuth.generateEmailVerificationLink(email, settings);
            log.info("🔗 Link de verificación generado: {}", link);

        } catch (FirebaseAuthException e) {
            log.error("⚠️ Error generando link de verificación para {}: {}", email, e.getMessage());
        }

        // 4️⃣ Enviar email de verificación (si se generó el link)
        if (link != null) {
            try {
                emailService.sendVerificationEmail(email, link);
                log.info("📧 Email de verificación enviado a: {}", email);
            } catch (Exception e) {
                log.error("⚠️ Error enviando email de verificación a {}: {}", email, e.getMessage());
            }
        } else {
            log.warn("⚠️ No se envió email de verificación a {} porque no se generó el link", email);
        }
    }
}
