package com.vendeskgt.vendeskgt.resend;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ResendWelcomeEmailService {
    private static final Logger log = LoggerFactory.getLogger(ResendWelcomeEmailService.class);

    private static final String SUBJECT = "¡Bienvenido a VendeskGT! 🎉";
    private static final String FROM = "VendeskGT <no-reply@registration.vendeskgt.win>";

    @Value("${INFISICAL_RESEND_KEY}")
    private String resendApiKey;

    @Value("${app.dashboard.url:http://localhost:3000/dashboard}")
    private String dashboardUrl;

    private Resend resend;

    @PostConstruct
    public void init() {
        this.resend = new Resend(resendApiKey);
        log.info("Resend Welcome Email Service inicializado");
    }

    // ✅ Recibe el nombre del usuario
    public void sendWelcomeEmail(String to, String userName) {
        CreateEmailOptions params = buildWelcomeEmail(to, userName);

        try {
            CreateEmailResponse response = resend.emails().send(params);
            log.info("✅ Email de bienvenida enviado a {} con ID: {}", to, response.getId());

        } catch (ResendException e) {
            log.error("❌ Error enviando email de bienvenida a: {}", to, e);
            throw new RuntimeException("Error al enviar email de bienvenida", e);
        }
    }

    private CreateEmailOptions buildWelcomeEmail(String to, String userName) {
        String htmlContent = generateEmailContent(userName, dashboardUrl);

        return CreateEmailOptions.builder()
                .from(FROM)
                .to(to)
                .subject(SUBJECT)
                .html(htmlContent)
                .build();
    }

    // ✅ Recibe los parámetros y usa String.format()
    private String generateEmailContent(String userName, String dashboardUrl) {
        return String.format("""
                <!DOCTYPE html>
                <html lang='es'>
                <head>
                  <meta charset='UTF-8'>
                  <meta name='viewport' content='width=device-width, initial-scale=1.0'>
                  <title>¡Bienvenido a VendeskGT!</title>
                </head>
                <body style='font-family: Arial, sans-serif; background-color: #f4f4f7; margin:0; padding:0;'>
                  <table width='100%%' cellpadding='0' cellspacing='0'>
                    <tr>
                      <td align='center'>
                        <table width='600' cellpadding='0' cellspacing='0' style='background-color:#ffffff; padding:30px; margin:20px 0; border-radius:10px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);'>
                          <tr>
                            <td style='text-align:center;'>
                              <!-- Header con icono -->
                              <div style='background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding:30px; border-radius:10px; margin-bottom:30px;'>
                                <h1 style='color:#ffffff; margin:0; font-size:32px;'>🎉</h1>
                                <h2 style='color:#ffffff; margin:10px 0 0 0;'>¡Tu cuenta está activa!</h2>
                              </div>
                              
                              <!-- Saludo personalizado -->
                              <h3 style='color:#333; margin-bottom:10px;'>Hola %s,</h3>
                              <p style='color:#555; font-size:16px; line-height:1.6;'>
                                ¡Felicitaciones! Has verificado tu correo exitosamente y tu cuenta en <strong>VendeskGT</strong> ya está activa y lista para usar.
                              </p>
                              
                              <!-- Características destacadas -->
                              <div style='background-color:#f9f9f9; padding:20px; border-radius:8px; margin:25px 0; text-align:left;'>
                                <h4 style='color:#333; margin-top:0;'>¿Qué puedes hacer ahora?</h4>
                                <ul style='color:#555; line-height:1.8; padding-left:20px;'>
                                  <li>✅ Gestionar tu inventario de productos</li>
                                  <li>✅ Realizar ventas y generar facturas</li>
                                  <li>✅ Crear y gestionar clientes</li>
                                  <li>✅ Visualizar reportes y estadísticas</li>
                                  <li>✅ Configurar tu tienda a tu medida</li>
                                </ul>
                              </div>
                              
                              <!-- CTA Button -->
                              <a href='%s' 
                                 style='display:inline-block; padding:16px 40px; margin:20px 0; font-size:18px; font-weight:bold; color:#ffffff; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); text-decoration:none; border-radius:50px; box-shadow: 0 4px 15px rgba(102,126,234,0.4);'>
                                Ir a mi Dashboard
                              </a>
                              
                              <!-- Tips section -->
                              <div style='background-color:#e8f5e9; padding:20px; border-radius:8px; margin:30px 0; border-left:4px solid #4CAF50;'>
                                <h4 style='color:#2e7d32; margin-top:0;'>💡 Consejo rápido</h4>
                                <p style='color:#555; font-size:14px; line-height:1.6; margin:0;'>
                                  Te recomendamos completar tu perfil y configurar tu primer producto para comenzar a vender de inmediato.
                                </p>
                              </div>
                              
                              <!-- Support -->
                              <p style='color:#666; font-size:14px; margin-top:30px;'>
                                ¿Necesitas ayuda? Nuestro equipo está aquí para ti.<br>
                                <a href='mailto:support@vendeskgt.win' style='color:#667eea; text-decoration:none;'>support@vendeskgt.win</a>
                              </p>
                              
                              <hr style='border:none; border-top:1px solid #eee; margin:30px 0;'>
                              
                              <!-- Footer -->
                              <p style='color:#999; font-size:12px;'>
                                Gracias por confiar en VendeskGT para tu negocio. 🚀
                              </p>
                              <p style='color:#999; font-size:11px; margin-top:20px;'>
                                © 2025 VendeskGT. Todos los derechos reservados.
                              </p>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """, userName, dashboardUrl); // ✅ Pasa los parámetros aquí
    }
}