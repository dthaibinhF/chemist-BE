-- Migration: Create teacher_monthly_summary table for salary tracking
-- Version: V2
-- Description: Create comprehensive table to track teacher monthly salary summaries with performance metrics

CREATE TABLE teacher_monthly_summary (
    id SERIAL PRIMARY KEY,
    teacher_id INTEGER NOT NULL,
    month INTEGER NOT NULL CHECK (month >= 1 AND month <= 12),
    year INTEGER NOT NULL CHECK (year >= 2020 AND year <= 2100),
    
    -- Lesson metrics
    scheduled_lessons INTEGER NOT NULL DEFAULT 0,
    completed_lessons INTEGER NOT NULL DEFAULT 0,
    completion_rate DECIMAL(5,4) NOT NULL DEFAULT 0.0000,
    
    -- Salary calculation
    rate_per_lesson DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    base_salary DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    performance_bonus DECIMAL(10,2) DEFAULT 0.00,
    total_salary DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    end_at TIMESTAMP WITH TIME ZONE,
    
    -- Constraints
    CONSTRAINT fk_teacher_monthly_summary_teacher 
        FOREIGN KEY (teacher_id) REFERENCES teacher(id) ON DELETE CASCADE,
    CONSTRAINT uk_teacher_monthly_summary_unique 
        UNIQUE(teacher_id, month, year)
);

-- Create indexes for better query performance
CREATE INDEX idx_teacher_monthly_summary_teacher_id ON teacher_monthly_summary(teacher_id);
CREATE INDEX idx_teacher_monthly_summary_month_year ON teacher_monthly_summary(month, year);
CREATE INDEX idx_teacher_monthly_summary_created_at ON teacher_monthly_summary(created_at);

-- Add comments for documentation
COMMENT ON TABLE teacher_monthly_summary IS 'Monthly salary summary and performance metrics for teachers';
COMMENT ON COLUMN teacher_monthly_summary.completion_rate IS 'Percentage of completed lessons (0.0000 to 1.0000)';
COMMENT ON COLUMN teacher_monthly_summary.performance_bonus IS 'Bonus amount based on performance metrics';
COMMENT ON COLUMN teacher_monthly_summary.end_at IS 'Soft delete timestamp for audit trail';

-- Create trigger to automatically update updated_at column
CREATE OR REPLACE FUNCTION update_teacher_monthly_summary_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_teacher_monthly_summary_updated_at
    BEFORE UPDATE ON teacher_monthly_summary
    FOR EACH ROW
    EXECUTE FUNCTION update_teacher_monthly_summary_updated_at();