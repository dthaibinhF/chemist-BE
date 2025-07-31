-- V5__Remove_effective_discount_logic.sql
-- Simplify payment logic by removing redundant effectiveDiscount calculation
-- Fix existing data to match new constraint: amount + have_discount = generated_amount

-- First, check existing data structure
-- Current data shows: generated_amount appears to be the amount AFTER discount
-- We need to fix this to match the new business logic where:
-- generated_amount = original fee (before discount)
-- amount = final amount paid (after discount)  
-- have_discount = discount given
-- Constraint: amount + have_discount = generated_amount

-- Fix existing data where generated_amount + have_discount should equal the original amount
UPDATE payment_detail 
SET generated_amount = amount + COALESCE(have_discount, 0)
WHERE generated_amount IS NOT NULL 
  AND have_discount IS NOT NULL
  AND (amount + have_discount) != generated_amount;

-- For records where have_discount is null, generated_amount should equal amount
UPDATE payment_detail 
SET generated_amount = amount 
WHERE generated_amount IS NOT NULL 
  AND have_discount IS NULL
  AND generated_amount != amount;

-- Add constraint to ensure data integrity
-- amount + have_discount should equal generated_amount
ALTER TABLE payment_detail 
ADD CONSTRAINT chk_payment_detail_amount_integrity 
CHECK (
    -- If generated_amount is null, no constraint needed (backward compatibility)
    generated_amount IS NULL OR
    -- If have_discount is null, amount should equal generated_amount
    (have_discount IS NULL AND amount = generated_amount) OR
    -- If have_discount is provided, amount + discount must equal generated_amount
    (have_discount IS NOT NULL AND amount + have_discount = generated_amount)
);

-- Add helpful comments explaining the simplified business logic
COMMENT ON COLUMN payment_detail.have_discount IS 'Discount amount authorized by teacher/admin. When provided, constraint ensures: amount + have_discount = generated_amount';
COMMENT ON COLUMN payment_detail.amount IS 'Final amount paid by student after discount. Constraint ensures: amount + have_discount = generated_amount';
COMMENT ON COLUMN payment_detail.generated_amount IS 'Original fee amount before any discounts. Used with amount and have_discount to maintain data integrity';

-- Add index for performance on the constrained columns
CREATE INDEX IF NOT EXISTS idx_payment_detail_amount_integrity 
ON payment_detail(generated_amount, amount, have_discount) 
WHERE generated_amount IS NOT NULL;