package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.FinancialStatisticsDTO;
import dthaibinhf.project.chemistbe.model.PaymentStatus;
import dthaibinhf.project.chemistbe.model.StudentPaymentSummary;
import dthaibinhf.project.chemistbe.repository.PaymentDetailRepository;
import dthaibinhf.project.chemistbe.repository.StudentPaymentSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * Service for calculating financial statistics and generating dashboard data.
 * 
 * This service provides comprehensive financial metrics including:
 * - Revenue calculations and growth rates
 * - Outstanding payment tracking
 * - Collection rate analysis
 * - Payment status distribution
 * - Student participation metrics
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FinancialStatisticsService {
    
    private final PaymentDetailRepository paymentDetailRepository;
    private final StudentPaymentSummaryRepository summaryRepository;
    
    /**
     * Generate comprehensive financial statistics for the dashboard.
     * 
     * @return Financial statistics DTO with all metrics
     */
    public FinancialStatisticsDTO getFinancialDashboard() {
        log.info("Generating financial dashboard statistics");
        
        // Get basic totals
        BigDecimal totalRevenue = summaryRepository.getTotalPaidAmount();
        BigDecimal totalOutstanding = summaryRepository.getTotalOutstandingAmount();
        BigDecimal totalAmountDue = totalRevenue.add(totalOutstanding);
        
        // Calculate collection rate
        BigDecimal collectionRate = calculateCollectionRate(totalRevenue, totalAmountDue);
        
        // Get payment status counts
        PaymentStatusCounts statusCounts = getPaymentStatusCounts();
        
        // Get overdue amount
        BigDecimal overdueAmount = getOverdueAmount();
        
        // Get monthly revenue data
        MonthlyRevenueData monthlyData = getMonthlyRevenueData();
        
        // Get additional metrics
        BigDecimal averagePaymentAmount = calculateAveragePaymentAmount();
        int totalTransactions = getTotalTransactionCount();
        int activeStudentsCount = getActiveStudentsCount();
        BigDecimal participationRate = calculateStudentParticipationRate(activeStudentsCount);
        
        return FinancialStatisticsDTO.builder()
                .totalRevenue(totalRevenue)
                .totalOutstanding(totalOutstanding)
                .totalAmountDue(totalAmountDue)
                .collectionRate(collectionRate)
                .pendingPaymentsCount(statusCounts.pendingCount)
                .partialPaymentsCount(statusCounts.partialCount)
                .paidPaymentsCount(statusCounts.paidCount)
                .overduePaymentsCount(statusCounts.overdueCount)
                .overdueAmount(overdueAmount)
                .currentMonthRevenue(monthlyData.currentMonthRevenue)
                .previousMonthRevenue(monthlyData.previousMonthRevenue)
                .monthlyGrowthRate(monthlyData.growthRate)
                .averagePaymentAmount(averagePaymentAmount)
                .totalTransactions(totalTransactions)
                .activeStudentsCount(activeStudentsCount)
                .studentParticipationRate(participationRate)
                .build();
    }
    
    /**
     * Get financial statistics for a specific date range.
     * 
     * @param startDate Start date for the range
     * @param endDate End date for the range
     * @return Financial statistics for the date range
     */
    public FinancialStatisticsDTO getFinancialStatisticsForDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("Generating financial statistics for date range: {} to {}", startDate, endDate);
        
        OffsetDateTime startDateTime = startDate.atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
        OffsetDateTime endDateTime = endDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toOffsetDateTime();
        
        // Get revenue for the date range
        BigDecimal periodRevenue = paymentDetailRepository.getTotalRevenueByDateRange(startDateTime, endDateTime);
        
        // Get transactions count for the period
        int periodTransactions = paymentDetailRepository.findActiveByDateRange(startDateTime, endDateTime).size();
        
        // Calculate average payment for the period
        BigDecimal periodAveragePayment = periodTransactions > 0 
                ? periodRevenue.divide(BigDecimal.valueOf(periodTransactions), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        
        return FinancialStatisticsDTO.builder()
                .totalRevenue(periodRevenue)
                .totalTransactions(periodTransactions)
                .averagePaymentAmount(periodAveragePayment)
                .build();
    }
    
    /**
     * Calculate collection rate as a percentage.
     */
    private BigDecimal calculateCollectionRate(BigDecimal totalRevenue, BigDecimal totalAmountDue) {
        if (totalAmountDue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.valueOf(100); // 100% if no amount due
        }
        
        return totalRevenue.multiply(BigDecimal.valueOf(100))
                .divide(totalAmountDue, 2, RoundingMode.HALF_UP);
    }
    
    /**
     * Get payment status distribution counts.
     */
    private PaymentStatusCounts getPaymentStatusCounts() {
        List<StudentPaymentSummary> allSummaries = summaryRepository.findAllActive();
        
        int pendingCount = 0;
        int partialCount = 0;
        int paidCount = 0;
        int overdueCount = 0;
        
        for (StudentPaymentSummary summary : allSummaries) {
            switch (summary.getPaymentStatus()) {
                case PENDING:
                    pendingCount++;
                    break;
                case PARTIAL:
                    partialCount++;
                    break;
                case PAID:
                    paidCount++;
                    break;
                case OVERDUE:
                    overdueCount++;
                    break;
            }
        }
        
        return new PaymentStatusCounts(pendingCount, partialCount, paidCount, overdueCount);
    }
    
    /**
     * Get total overdue amount.
     */
    private BigDecimal getOverdueAmount() {
        OffsetDateTime currentDate = OffsetDateTime.now();
        List<StudentPaymentSummary> overdueSummaries = summaryRepository.findOverduePayments(currentDate);
        
        return overdueSummaries.stream()
                .map(StudentPaymentSummary::getOutstandingAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Get monthly revenue data and calculate growth rate.
     */
    private MonthlyRevenueData getMonthlyRevenueData() {
        LocalDate now = LocalDate.now();
        LocalDate currentMonthStart = now.withDayOfMonth(1);
        LocalDate previousMonthStart = currentMonthStart.minusMonths(1);
        LocalDate previousMonthEnd = currentMonthStart.minusDays(1);
        
        OffsetDateTime currentMonthStartDT = currentMonthStart.atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
        OffsetDateTime currentMonthEndDT = now.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toOffsetDateTime();
        OffsetDateTime previousMonthStartDT = previousMonthStart.atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
        OffsetDateTime previousMonthEndDT = previousMonthEnd.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toOffsetDateTime();
        
        BigDecimal currentMonthRevenue = paymentDetailRepository.getTotalRevenueByDateRange(currentMonthStartDT, currentMonthEndDT);
        BigDecimal previousMonthRevenue = paymentDetailRepository.getTotalRevenueByDateRange(previousMonthStartDT, previousMonthEndDT);
        
        BigDecimal growthRate = calculateMonthlyGrowthRate(currentMonthRevenue, previousMonthRevenue);
        
        return new MonthlyRevenueData(currentMonthRevenue, previousMonthRevenue, growthRate);
    }
    
    /**
     * Calculate monthly growth rate as percentage.
     */
    private BigDecimal calculateMonthlyGrowthRate(BigDecimal currentMonth, BigDecimal previousMonth) {
        if (previousMonth.compareTo(BigDecimal.ZERO) == 0) {
            return currentMonth.compareTo(BigDecimal.ZERO) > 0 ? BigDecimal.valueOf(100) : BigDecimal.ZERO;
        }
        
        return currentMonth.subtract(previousMonth)
                .multiply(BigDecimal.valueOf(100))
                .divide(previousMonth, 2, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate average payment amount across all transactions.
     */
    private BigDecimal calculateAveragePaymentAmount() {
        BigDecimal totalRevenue = summaryRepository.getTotalPaidAmount();
        int totalTransactions = getTotalTransactionCount();
        
        if (totalTransactions == 0) {
            return BigDecimal.ZERO;
        }
        
        return totalRevenue.divide(BigDecimal.valueOf(totalTransactions), 2, RoundingMode.HALF_UP);
    }
    
    /**
     * Get total number of payment transactions.
     */
    private int getTotalTransactionCount() {
        return paymentDetailRepository.findAllActivePaymentDetails().size();
    }
    
    /**
     * Get number of active students with payment obligations.
     */
    private int getActiveStudentsCount() {
        List<StudentPaymentSummary> allSummaries = summaryRepository.findAllActive();
        return (int) allSummaries.stream()
                .map(summary -> summary.getStudent().getId())
                .distinct()
                .count();
    }
    
    /**
     * Calculate student participation rate (students who made at least one payment).
     */
    private BigDecimal calculateStudentParticipationRate(int activeStudentsCount) {
        if (activeStudentsCount == 0) {
            return BigDecimal.ZERO;
        }
        
        List<StudentPaymentSummary> summariesWithPayments = summaryRepository.findAllActive().stream()
                .filter(summary -> summary.getTotalAmountPaid().compareTo(BigDecimal.ZERO) > 0)
                .toList();
        
        int studentsWithPayments = (int) summariesWithPayments.stream()
                .map(summary -> summary.getStudent().getId())
                .distinct()
                .count();
        
        return BigDecimal.valueOf(studentsWithPayments)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(activeStudentsCount), 2, RoundingMode.HALF_UP);
    }
    
    /**
     * Helper record for payment status counts.
     */
    private record PaymentStatusCounts(int pendingCount, int partialCount, int paidCount, int overdueCount) {}
    
    /**
     * Helper record for monthly revenue data.
     */
    private record MonthlyRevenueData(BigDecimal currentMonthRevenue, BigDecimal previousMonthRevenue, BigDecimal growthRate) {}
}