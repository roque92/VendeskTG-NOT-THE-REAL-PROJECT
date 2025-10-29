package com.vendeskgt.vendeskgt.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import com.google.firebase.auth.FirebaseAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


@Configuration
@Profile("!test")
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);
	
	@Value("${INFISICAL_FIREBASE_AUTH}")
	private String firebaseAuthJson;

    @Bean
    public FirebaseApp firebaseApp() throws Exception{

        // Evitar doble inicialización
        if(!FirebaseApp.getApps().isEmpty()){
            log.error("FirebaseApp ya inicializado, reutilizando instancia existente.");
            return FirebaseApp.getInstance();
        }

        // Cargar credenciales desde Infisical
        String json = firebaseAuthJson;
        if (json == null || json.trim().isEmpty()) {
            throw new RuntimeException("No se encontro INFISICAL_FIREBASE_AUTH en variables de entorno.");
            
        }
            
            log.warn("Cargando credenciales Firebase desde variable de entorno INFISICAL_FIREBASE_AUTH");
            
            try (InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
                GoogleCredentials credentials =  GoogleCredentials.fromStream(is);
                FirebaseOptions options = FirebaseOptions.builder()
                                                  .setCredentials(credentials)
                                                  .build();
                
                return FirebaseApp.initializeApp(options);
                
            }
    }

    @Bean
    public FirebaseAuth firebaseAuth (FirebaseApp firebaseApp){
        log.error("Creando bean FirebaseAuth");
        return FirebaseAuth.getInstance(firebaseApp);
    }
}
