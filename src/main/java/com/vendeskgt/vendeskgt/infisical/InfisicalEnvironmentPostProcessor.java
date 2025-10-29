package com.vendeskgt.vendeskgt.infisical;

import com.infisical.sdk.InfisicalSdk;
import com.infisical.sdk.config.SdkConfig;
import com.infisical.sdk.models.Secret;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfisicalEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

        try {
            // Leer variables de entorno de sistema
            String clientId = environment.getProperty("INFISICAL_CLIENT_ID");
            String clientSecret = environment.getProperty("INFISICAL_CLIENT_SECRET");
            String projectID = environment.getProperty("INFISICAL_PROJECT_ID");
            String env = environment.getProperty("INFISICAL_ENVIRONMENT", "dev");
            String path = environment.getProperty("INFISICAL_SECRET_PATH", "/");

            // ⚠️ Si no hay credenciales, no hacemos nada
            if (clientId == null || clientSecret == null || projectID == null) {
                System.out.println("⚠️ Infisical credentials not found, skipping secret loading");
                return;
            }

            System.out.println("🔧 Loading secrets from Infisical...");

            InfisicalSdk sdk = new InfisicalSdk(new SdkConfig.Builder().build());
            sdk.Auth().UniversalAuthLogin(clientId, clientSecret);

            List<Secret> secrets = sdk.Secrets().ListSecrets(
                    projectID,
                    env,
                    path,
                    false,
                    false,
                    false,
                    true
            );

            Map<String, Object> infisicalProperties = new HashMap<>();
            for (Secret secret : secrets) {
                infisicalProperties.put(secret.getSecretKey(), secret.getSecretValue());
                System.out.println("🔑 Loaded: " + secret.getSecretKey());
            }

            // Inyectar secrets al environment de Spring
            environment.getPropertySources().addFirst(
                    new MapPropertySource("infisical", infisicalProperties)
            );

            System.out.println("✅ " + secrets.size() + " secrets loaded successfully");

        } catch (Exception e) {
            System.err.println("❌ Error loading Infisical secrets: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
