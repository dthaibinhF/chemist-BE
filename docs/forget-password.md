🎉 Forget Password Feature - COMPLETED

✅ What's Been Implemented:

1. Dependencies & Configuration
   - Added SendGrid Java SDK to pom.xml
   - Configured SendGrid API key and email settings in application.yaml
2. Database Schema
   - Created PasswordResetToken entity with OTP security features
   - Added Flyway migration V6__Create_password_reset_token_table.sql
   - Includes proper indexing for performance
3. API Layer (6-Layer Architecture)
   - DTOs: ForgetPasswordRequest, VerifyOtpRequest with validation
   - Entity: PasswordResetToken extending BaseEntity with soft delete support
   - Repository: PasswordResetTokenRepository with soft delete queries
   - Services: EmailService (SendGrid) + PasswordResetService (business
   logic)
   - Controller: Extended AuthController with new endpoints
4. Security Features
   - 6-digit random OTP generation using SecureRandom
   - 10-minute expiration time for OTPs
   - Maximum 3 verification attempts per OTP
   - Rate limiting (1 request per minute per email)
   - One-time use only (prevents replay attacks)
   - Beautiful HTML email template with security warnings
5. API Endpoints
   POST /api/v1/auth/forget-password
   Body: {"email": "user@example.com"}

POST /api/v1/auth/verify-otp
Body: {
"email": "user@example.com",
"otp_code": "123456",
"new_password": "newpassword123"
}

🔧 How to Use:

1. Set Environment Variable:
   export SENDGRID_API_KEY="SG.JdaSUe53RpyTnhlcnSUTVA.tTODs-7CBxj_FFd6CltJaf-Pp
   E_HzolPMEpoOneSimk"
2. Test the Flow:
   - Request OTP: POST /api/v1/auth/forget-password
   - Check email for 6-digit OTP
   - Verify & reset: POST /api/v1/auth/verify-otp
3. Run Application:
   ./mvnw spring-boot:run

🛡️ Security Highlights:

- Professional email template with clear security warnings
- Atomic transactions for data consistency
- Comprehensive error handling and validation
- Follows existing codebase patterns (soft delete, timezone handling)
- Rate limiting and attempt restrictions
- Clean separation of concerns

The feature is now production-ready and follows all your project's
established patterns and security best practices!