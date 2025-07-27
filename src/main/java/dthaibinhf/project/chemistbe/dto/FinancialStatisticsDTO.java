package dthaibinhf.project.chemistbe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for financial statistics and dashboard overview.
 * 
 * This DTO provides comprehensive financial metrics including revenue,
 * outstanding payments, collection rates, and payment status distribution.
 */
@Value
@Builder
@AllArgsConstructor
public class FinancialStatisticsDTO implements Serializable {
    
    /**
     * Total revenue collected across all payments.
     */
    @JsonProperty("total_revenue")
    BigDecimal totalRevenue;
    
    /**
     * Total outstanding amount across all payment summaries.
     */
    @JsonProperty("total_outstanding")
    BigDecimal totalOutstanding;
    
    /**
     * Total amount due across all payment summaries.
     */
    @JsonProperty("total_amount_due")
    BigDecimal totalAmountDue;
    
    /**
     * Collection rate as percentage (0.0 to 100.0).
     */
    @JsonProperty("collection_rate")
    BigDecimal collectionRate;
    
    /**
     * Number of students with pending payments.
     */
    @JsonProperty("pending_payments_count")
    Integer pendingPaymentsCount;
    
    /**
     * Number of students with partial payments.
     */
    @JsonProperty("partial_payments_count")
    Integer partialPaymentsCount;
    
    /**
     * Number of students with completed payments.
     */
    @JsonProperty("paid_payments_count")
    Integer paidPaymentsCount;
    
    /**
     * Number of students with overdue payments.
     */
    @JsonProperty("overdue_payments_count")
    Integer overduePaymentsCount;
    
    /**
     * Total amount from overdue payments.
     */
    @JsonProperty("overdue_amount")
    BigDecimal overdueAmount;
    
    /**
     * Revenue for current month.
     */
    @JsonProperty("current_month_revenue")
    BigDecimal currentMonthRevenue;
    
    /**
     * Revenue for previous month.
     */
    @JsonProperty("previous_month_revenue")
    BigDecimal previousMonthRevenue;
    
    /**
     * Monthly revenue growth rate as percentage.
     */
    @JsonProperty("monthly_growth_rate")
    BigDecimal monthlyGrowthRate;
    
    /**
     * Average payment amount.
     */
    @JsonProperty("average_payment_amount")
    BigDecimal averagePaymentAmount;
    
    /**
     * Total number of payment transactions.
     */
    @JsonProperty("total_transactions")
    Integer totalTransactions;
    
    /**
     * Total number of active students with payment obligations.
     */
    @JsonProperty("active_students_count")
    Integer activeStudentsCount;
    
    /**
     * Percentage of students who have made at least one payment.
     */
    @JsonProperty("student_participation_rate")
    BigDecimal studentParticipationRate;
}