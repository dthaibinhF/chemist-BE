-- V4__Enhance_payment_system.sql
-- Add payment system enhancements: payment status, due dates, and student payment summary

-- Add new columns to payment_detail table
ALTER TABLE payment_detail 
ADD COLUMN payment_status VARCHAR(20) DEFAULT 'PENDING',
ADD COLUMN due_date TIMESTAMP WITH TIME ZONE,
ADD COLUMN generated_amount DECIMAL(10,2);

-- Create student_payment_summary table
CREATE TABLE student_payment_summary (
    id SERIAL PRIMARY KEY,
    student_id INTEGER NOT NULL,
    fee_id INTEGER NOT NULL,
    academic_year_id INTEGER NOT NULL,
    group_id INTEGER,
    
    -- Payment obligation details
    total_amount_due DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total_amount_paid DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    outstanding_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    
    -- Status and dates
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    due_date TIMESTAMP WITH TIME ZONE,
    enrollment_date TIMESTAMP WITH TIME ZONE NOT NULL,
    
    -- Base entity fields
    create_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    update_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    end_at TIMESTAMP WITH TIME ZONE,
    
    -- Foreign key constraints
    CONSTRAINT fk_student_payment_summary_student 
        FOREIGN KEY (student_id) REFERENCES student(id),
    CONSTRAINT fk_student_payment_summary_fee 
        FOREIGN KEY (fee_id) REFERENCES fee(id),
    CONSTRAINT fk_student_payment_summary_academic_year 
        FOREIGN KEY (academic_year_id) REFERENCES academic_year(id),
    CONSTRAINT fk_student_payment_summary_group 
        FOREIGN KEY (group_id) REFERENCES "group"(id),
    
    -- Unique constraint to prevent duplicate summaries
    CONSTRAINT uk_student_payment_summary_unique 
        UNIQUE (student_id, fee_id, academic_year_id, group_id)
);

-- Add foreign key constraints to existing payment_detail columns if not already present
-- (This is safe to run even if constraints already exist)
DO $$
BEGIN
    -- Check if payment_status constraint exists, if not add it
    IF NOT EXISTS (SELECT 1 FROM pg_constraint 
                   WHERE conname = 'chk_payment_detail_status') THEN
        ALTER TABLE payment_detail 
        ADD CONSTRAINT chk_payment_detail_status 
        CHECK (payment_status IN ('PENDING', 'PARTIAL', 'PAID', 'OVERDUE'));
    END IF;
END $$;

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_payment_detail_status ON payment_detail(payment_status);
CREATE INDEX IF NOT EXISTS idx_payment_detail_due_date ON payment_detail(due_date);
CREATE INDEX IF NOT EXISTS idx_payment_detail_student_id ON payment_detail(student_id);
CREATE INDEX IF NOT EXISTS idx_payment_detail_fee_id ON payment_detail(fee_id);

CREATE INDEX IF NOT EXISTS idx_student_payment_summary_student_id ON student_payment_summary(student_id);
CREATE INDEX IF NOT EXISTS idx_student_payment_summary_fee_id ON student_payment_summary(fee_id);
CREATE INDEX IF NOT EXISTS idx_student_payment_summary_academic_year ON student_payment_summary(academic_year_id);
CREATE INDEX IF NOT EXISTS idx_student_payment_summary_status ON student_payment_summary(payment_status);
CREATE INDEX IF NOT EXISTS idx_student_payment_summary_due_date ON student_payment_summary(due_date);

-- Update existing payment_detail records with default values
-- Set generated_amount equal to amount for existing records
UPDATE payment_detail 
SET generated_amount = amount 
WHERE generated_amount IS NULL;

-- Set due_date to 30 days from created_at for existing records without due_date
UPDATE payment_detail 
SET due_date = create_at + INTERVAL '30 days'
WHERE due_date IS NULL;

-- Add comments for documentation
COMMENT ON TABLE student_payment_summary IS 'Tracks payment obligations and status for students per fee/academic year/group';
COMMENT ON COLUMN payment_detail.payment_status IS 'Payment status: PENDING, PARTIAL, PAID, OVERDUE';
COMMENT ON COLUMN payment_detail.due_date IS 'Date when payment is due';
COMMENT ON COLUMN payment_detail.generated_amount IS 'Original amount that was supposed to be paid (before discounts)';
COMMENT ON COLUMN student_payment_summary.total_amount_due IS 'Total amount the student owes for this fee';
COMMENT ON COLUMN student_payment_summary.total_amount_paid IS 'Total amount the student has paid';
COMMENT ON COLUMN student_payment_summary.outstanding_amount IS 'Remaining amount due (calculated field)';