-- Fix trigger function column name mismatch
-- The trigger was created with 'updated_at' but column was renamed to 'update_at' in V3

-- Drop existing trigger function
DROP FUNCTION IF EXISTS update_teacher_monthly_summary_updated_at() CASCADE;

-- Recreate trigger function with correct column name
CREATE OR REPLACE FUNCTION update_teacher_monthly_summary_update_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.update_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Recreate trigger with updated function
CREATE TRIGGER trg_teacher_monthly_summary_update_at
    BEFORE UPDATE ON teacher_monthly_summary
    FOR EACH ROW
    EXECUTE FUNCTION update_teacher_monthly_summary_update_at();