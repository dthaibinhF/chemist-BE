-- Create password_reset_token table for OTP-based password reset functionality
CREATE TABLE password_reset_token (
    id SERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    otp_code VARCHAR(6) NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    is_used BOOLEAN NOT NULL DEFAULT FALSE,
    attempts INTEGER NOT NULL DEFAULT 0,
    create_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_at TIMESTAMP WITH TIME ZONE,
    end_at TIMESTAMP WITH TIME ZONE
);

-- Create index on email for faster lookups
CREATE INDEX idx_password_reset_token_email ON password_reset_token(email);

-- Create index on expires_at for cleanup queries
CREATE INDEX idx_password_reset_token_expires_at ON password_reset_token(expires_at);

-- Create compound index for active tokens lookup (end_at IS NULL)
CREATE INDEX idx_password_reset_token_active ON password_reset_token(email, end_at) WHERE end_at IS NULL;