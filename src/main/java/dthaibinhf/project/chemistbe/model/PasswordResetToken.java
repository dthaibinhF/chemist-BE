package dthaibinhf.project.chemistbe.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;
import java.time.ZoneId;

@Getter
@Setter
@Entity
@Table(name = "password_reset_token")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PasswordResetToken extends BaseEntity {

    @Column(name = "email", nullable = false, length = 100)
    String email;

    @Column(name = "otp_code", nullable = false, length = 6)
    String otpCode;

    @Column(name = "expires_at", nullable = false)
    OffsetDateTime expiresAt;

    @Column(name = "is_used", nullable = false)
    Boolean isUsed = false;

    @Column(name = "attempts", nullable = false)
    Integer attempts = 0;

    // Helper method to check if token is expired
    public boolean isExpired() {
        return OffsetDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).isAfter(this.expiresAt);
    }

    // Helper method to check if token has exceeded max attempts
    public boolean hasExceededMaxAttempts() {
        return this.attempts >= 3;
    }

    // Helper method to increment attempts
    public void incrementAttempts() {
        this.attempts++;
    }

    // Helper method to mark token as used
    public void markAsUsed() {
        this.isUsed = true;
    }
}