package com.vendeskgt.vendeskgt.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.vendeskgt.vendeskgt.resend.ResendEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class FirebaseCreateUserService {
    
    Logger log = LoggerFactory.getLogger(FirebaseCreateUserService.class);

    @Autowired
    private FirebaseAuth firebaseAuth;
	@Autowired
	private ResendEmailService emailService;

    public void registerUser(String email, String password, UUID tenantId, String role)
            throws FirebaseAuthException {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password);

        UserRecord userRecord = firebaseAuth.createUser(request);

        //set Custom Claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("tenantId", tenantId.toString());
        claims.put("role", role);

        firebaseAuth.setCustomUserClaims(userRecord.getUid(), claims);
      
       String link = firebaseAuth.generateEmailVerificationLink(email);
	    log.warn("\uD83D\uDCE7 verification link: {}", link);
		
		emailService.sendVerificationEmail(email, link);
		
    }
}
