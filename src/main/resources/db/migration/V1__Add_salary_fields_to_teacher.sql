-- Migration: Add salary configuration fields to teacher table
-- Version: V1
-- Description: Add salary_type and base_rate columns to support teacher salary calculation

-- Add salary_type column with default value (if not exists)
DO $$ 
BEGIN 
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'teacher' AND column_name = 'salary_type') THEN
        ALTER TABLE teacher ADD COLUMN salary_type VARCHAR(20) DEFAULT 'PER_LESSON';
    END IF;
END $$;

-- Add base_rate column for salary calculations (if not exists)
DO $$ 
BEGIN 
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'teacher' AND column_name = 'base_rate') THEN
        ALTER TABLE teacher ADD COLUMN base_rate DECIMAL(10,2);
    END IF;
END $$;

-- Add comments for documentation
DO $$ 
BEGIN 
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'teacher' AND column_name = 'salary_type') THEN
        EXECUTE 'COMMENT ON COLUMN teacher.salary_type IS ''Salary calculation type: PER_LESSON or FIXED''';
    END IF;
    
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'teacher' AND column_name = 'base_rate') THEN
        EXECUTE 'COMMENT ON COLUMN teacher.base_rate IS ''Base rate for salary calculation in VND''';
    END IF;
END $$;

-- Create index for salary_type for better query performance (if not exists)
DO $$ 
BEGIN 
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE tablename = 'teacher' AND indexname = 'idx_teacher_salary_type') THEN
        CREATE INDEX idx_teacher_salary_type ON teacher(salary_type);
    END IF;
END $$;