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

import java.awt.*;

@Service
public class ResendEmailService {
	
	@Value("${INFISICAL_RESEND_KEY}")
	private String RESEND_API_KEY;
	
	private static final String subject = "Un último paso para activar tu cuenta en VendeskGT";
	private static final String from = "VendeskGT <no-replay@registration.vendeskgt.win>";
	
	private Resend resend;
	private static final Logger log = LoggerFactory.getLogger(ResendEmailService.class);
	
	
	@PostConstruct
	public void init() {
		this.resend = new Resend(RESEND_API_KEY);
	}
	
	public void sendVerificationEmail (String to, String link){
		
		CreateEmailOptions params = buildVerificationEmail(to, link);
		
		try{
			CreateEmailResponse date = resend.emails().send(params);
			log.info("Email enviado con ID: {}", date.getId());
		} catch (ResendException e) {
			log.error("Error enviando email a {}", to, e);
		}
		
	}
	
	private CreateEmailOptions buildVerificationEmail(String to, String link){
		String htmlContent = generateEmailContent(link);
		
		return CreateEmailOptions.builder()
				       .from(from)
				       .to(to)
				       .subject(subject)
				       .html(htmlContent)
				       .build();
	}
	
	private String generateEmailContent (String link){
		return String.format("""
            <!DOCTYPE html>
            <html lang='es'>
            <head>
              <meta charset='UTF-8'>
              <title>Verifica tu correo</title>
            </head>
            <body style='font-family: Arial, sans-serif; background-color: #f4f4f7; margin:0; padding:0;'>
              <table width='100%%' cellpadding='0' cellspacing='0'>
                <tr>
                  <td align='center'>
                    <table width='600' cellpadding='0' cellspacing='0' style='background-color:#ffffff; padding:20px; margin:20px 0; border-radius:10px;'>
                      <tr>
                        <td style='text-align:center;'>
                          <h2 style='color:#333;'>¡Bienvenido a VendeskGT!</h2>
                          <p style='color:#555;'>Gracias por registrarte. Por favor verifica tu correo haciendo clic en el botón a continuación:</p>
                          <a href='%s' style='display:inline-block; padding:12px 25px; margin:20px 0; font-size:16px; color:#ffffff; background-color:#4CAF50; text-decoration:none; border-radius:5px;'>Verificar correo</a>
                          <p style='color:#888; font-size:12px;'>Si no creaste esta cuenta, ignora este correo.</p>
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
            </body>
            </html>
            """, link);
	}
	
}
