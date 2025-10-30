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
public class ResendVerifyEmailService {

    private static final Logger log = LoggerFactory.getLogger(ResendVerifyEmailService.class);

    // ✅ Constantes en mayúsculas
    private static final String SUBJECT = "Un último paso para activar tu cuenta en VendeskGT";
    private static final String FROM = "VendeskGT <no-reply@registration.vendeskgt.win>";

    @Value("${INFISICAL_RESEND_KEY}")
    private String resendApiKey;

    private Resend resend;

    @PostConstruct
    public void init() {
        this.resend = new Resend(resendApiKey);
        log.info("Resend Email Service inicializado");
    }

    public void sendVerificationEmail(String to, String link) {

        CreateEmailOptions params = buildVerificationEmail(to, link);

        try {
            CreateEmailResponse response = resend.emails().send(params);
            log.info("✅ Email de verificación enviado exitosamente a {} con ID: {}", to, response.getId());

        } catch (ResendException e) {
            log.error("❌ Error enviando email de verificación a: {}", to, e);
            throw new RuntimeException("Error al enviar email de verificación", e); // ✅ Propagar error
        }
    }

    private CreateEmailOptions buildVerificationEmail(String to, String link) {
        String htmlContent = generateEmailContent(link);

        return CreateEmailOptions.builder()
                .from(FROM)
                .to(to)
                .subject(SUBJECT)
                .html(htmlContent)
                .build();
    }

    private String generateEmailContent(String link) {
        return String.format("""
            <!DOCTYPE html>
            <html lang='es'>
            <head>
              <meta charset='UTF-8'>
              <meta name='viewport' content='width=device-width, initial-scale=1.0'>
              <title>Verifica tu correo</title>
            </head>
            <body style='font-family: Arial, sans-serif; background-color: #f4f4f7; margin:0; padding:0;'>
              <table width='100%%' cellpadding='0' cellspacing='0'>
                <tr>
                  <td align='center'>
                    <table width='600' cellpadding='0' cellspacing='0' style='background-color:#ffffff; padding:20px; margin:20px 0; border-radius:10px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);'>
                      <tr>
                        <td style='text-align:center;'>
                          <h2 style='color:#333; margin-bottom:10px;'>¡Bienvenido a VendeskGT! 🎉</h2>
                          <p style='color:#555; font-size:16px; line-height:1.5;'>
                            Gracias por registrarte. Por favor verifica tu correo electrónico haciendo clic en el botón a continuación:
                          </p>
                          <a href='%s' 
                             style='display:inline-block; padding:14px 30px; margin:25px 0; font-size:16px; font-weight:bold; color:#ffffff; background-color:#4CAF50; text-decoration:none; border-radius:5px; box-shadow: 0 2px 4px rgba(76,175,80,0.3);'>
                            Verificar mi correo
                          </a>
                          <p style='color:#888; font-size:14px; margin-top:30px;'>
                            O copia y pega este enlace en tu navegador:
                          </p>
                          <p style='color:#4CAF50; font-size:12px; word-break:break-all; padding:10px; background-color:#f9f9f9; border-radius:5px;'>
                            %s
                          </p>
                          <hr style='border:none; border-top:1px solid #eee; margin:30px 0;'>
                          <p style='color:#999; font-size:12px;'>
                            Si no creaste esta cuenta, puedes ignorar este correo de forma segura.
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
            """, link, link); // ✅ Link dos veces: botón y texto plano
    }
}