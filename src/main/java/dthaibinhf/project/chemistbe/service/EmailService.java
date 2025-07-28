package dthaibinhf.project.chemistbe.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class EmailService {

    @Value("${sendgrid.api-key}")
    String sendGridApiKey;

    @Value("${sendgrid.from.email}")
    String fromEmail;

    @Value("${sendgrid.from.name}")
    String fromName;

    public void sendOtpEmail(String toEmail, String otpCode) {
        try {
            Email from = new Email(fromEmail, fromName);
            Email to = new Email(toEmail);
            String subject = "Password Reset OTP - Chemist App";
            
            String htmlContent = buildOtpEmailTemplate(otpCode);
            Content content = new Content("text/html", htmlContent);
            
            Mail mail = new Mail(from, subject, to, content);
            
            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();
            
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            
            Response response = sg.api(request);
            
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                log.info("OTP email sent successfully to: {}", toEmail);
            } else {
                log.error("Failed to send OTP email to: {}. Status: {}, Body: {}", 
                    toEmail, response.getStatusCode(), response.getBody());
                throw new RuntimeException("Failed to send OTP email");
            }
            
        } catch (IOException e) {
            log.error("Error sending OTP email to: {}", toEmail, e);
            throw new RuntimeException("Error sending OTP email: " + e.getMessage());
        }
    }

    private String buildOtpEmailTemplate(String otpCode) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Password Reset OTP</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
                <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;">
                    <h1 style="color: white; margin: 0; font-size: 28px;">Password Reset Request</h1>
                </div>
                
                <div style="background: white; padding: 30px; border-radius: 0 0 10px 10px; box-shadow: 0 4px 6px rgba(0,0,0,0.1);">
                    <p style="font-size: 16px; margin-bottom: 20px;">Hello,</p>
                    
                    <p style="font-size: 16px; margin-bottom: 25px;">
                        We received a request to reset your password for your Chemist App account. 
                        Use the OTP code below to complete your password reset:
                    </p>
                    
                    <div style="background: #f8f9fa; border: 2px dashed #667eea; border-radius: 8px; padding: 25px; text-align: center; margin: 25px 0;">
                        <p style="margin: 0; font-size: 14px; color: #666; margin-bottom: 10px;">Your OTP Code:</p>
                        <h2 style="font-size: 36px; font-weight: bold; color: #667eea; margin: 0; letter-spacing: 8px; font-family: 'Courier New', monospace;">
                            %s
                        </h2>
                    </div>
                    
                    <div style="background: #fff3cd; border: 1px solid #ffeaa7; border-radius: 6px; padding: 15px; margin: 20px 0;">
                        <p style="margin: 0; color: #856404; font-size: 14px;">
                            <strong>⚠️ Important Security Information:</strong><br>
                            • This OTP will expire in <strong>10 minutes</strong><br>
                            • You can attempt verification a maximum of <strong>3 times</strong><br>
                            • If you didn't request this, please ignore this email
                        </p>
                    </div>
                    
                    <p style="font-size: 14px; color: #666; margin-top: 30px;">
                        If you didn't request a password reset, please ignore this email. Your account remains secure.
                    </p>
                    
                    <hr style="border: none; border-top: 1px solid #eee; margin: 25px 0;">
                    
                    <p style="font-size: 12px; color: #999; text-align: center; margin: 0;">
                        This is an automated message from Chemist App. Please do not reply to this email.
                    </p>
                </div>
            </body>
            </html>
            """.formatted(otpCode);
    }
}