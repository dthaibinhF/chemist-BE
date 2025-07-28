package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.request.ForgetPasswordRequest;
import dthaibinhf.project.chemistbe.dto.request.VerifyOtpRequest;
import dthaibinhf.project.chemistbe.model.Account;
import dthaibinhf.project.chemistbe.model.PasswordResetToken;
import dthaibinhf.project.chemistbe.repository.AccountRepository;
import dthaibinhf.project.chemistbe.repository.PasswordResetTokenRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PasswordResetService {

    PasswordResetTokenRepository passwordResetTokenRepository;
    AccountRepository accountRepository;
    EmailService emailService;
    PasswordEncoder passwordEncoder;

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 10;
    private static final int MAX_ATTEMPTS = 3;
    private static final int RATE_LIMIT_MINUTES = 1;

    @Transactional
    public void requestPasswordReset(ForgetPasswordRequest request) {
        String email = request.getEmail().toLowerCase().trim();
        
        // Check if account exists
        Account account = accountRepository.findActiveByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        
        // Rate limiting: Check if user has requested OTP in the last minute
        OffsetDateTime rateLimitTime = OffsetDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"))
                .minusMinutes(RATE_LIMIT_MINUTES);
        long recentRequests = passwordResetTokenRepository.countRecentRequestsByEmail(email, rateLimitTime);
        
        if (recentRequests > 0) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, 
                "Please wait before requesting another OTP");
        }
        
        // Soft delete any existing active tokens for this email
        List<PasswordResetToken> existingTokens = passwordResetTokenRepository.findActiveByEmail(email);
        existingTokens.forEach(token -> {
            token.softDelete();
            passwordResetTokenRepository.save(token);
        });
        
        // Generate new OTP
        String otpCode = generateOtp();
        
        // Create new password reset token
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setEmail(email);
        resetToken.setOtpCode(otpCode);
        resetToken.setExpiresAt(OffsetDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).plusMinutes(OTP_EXPIRY_MINUTES));
        resetToken.setIsUsed(false);
        resetToken.setAttempts(0);
        
        passwordResetTokenRepository.save(resetToken);
        
        // Send OTP via email
        try {
            emailService.sendOtpEmail(email, otpCode);
            log.info("Password reset OTP sent successfully to email: {}", email);
        } catch (Exception e) {
            log.error("Failed to send OTP email to: {}", email, e);
            // Soft delete the token if email sending fails
            resetToken.softDelete();
            passwordResetTokenRepository.save(resetToken);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Failed to send OTP email");
        }
    }

    @Transactional
    public void verifyOtpAndResetPassword(VerifyOtpRequest request) {
        String email = request.getEmail().toLowerCase().trim();
        String otpCode = request.getOtpCode();
        String newPassword = request.getNewPassword();
        
        // Find the token
        PasswordResetToken resetToken = passwordResetTokenRepository
                .findActiveByEmailAndOtpCode(email, otpCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Invalid OTP code"));
        
        // Check if token is expired
        if (resetToken.isExpired()) {
            resetToken.softDelete();
            passwordResetTokenRepository.save(resetToken);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "OTP has expired. Please request a new one");
        }
        
        // Check if token is already used
        if (resetToken.getIsUsed()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "OTP has already been used");
        }
        
        // Check if max attempts exceeded
        if (resetToken.hasExceededMaxAttempts()) {
            resetToken.softDelete();
            passwordResetTokenRepository.save(resetToken);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Maximum verification attempts exceeded. Please request a new OTP");
        }
        
        // Find the account
        Account account = accountRepository.findActiveByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Account not found"));
        
        // Update password
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
        
        // Mark token as used
        resetToken.markAsUsed();
        passwordResetTokenRepository.save(resetToken);
        
        log.info("Password reset successful for email: {}", email);
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();
        
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        
        return otp.toString();
    }

    // Method to clean up expired tokens (can be called by a scheduled job)
    @Transactional
    public void cleanupExpiredTokens() {
        OffsetDateTime currentTime = OffsetDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        List<PasswordResetToken> expiredTokens = passwordResetTokenRepository.findExpiredTokens(currentTime);
        
        expiredTokens.forEach(token -> {
            token.softDelete();
            passwordResetTokenRepository.save(token);
        });
        
        if (!expiredTokens.isEmpty()) {
            log.info("Cleaned up {} expired password reset tokens", expiredTokens.size());
        }
    }
}