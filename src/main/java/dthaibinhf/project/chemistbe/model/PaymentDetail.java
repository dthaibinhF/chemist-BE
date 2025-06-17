package dthaibinhf.project.chemistbe.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "payment_detail")
public class PaymentDetail extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fee_id", nullable = false)
    @JsonBackReference
    private Fee fee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonBackReference
    private Student student;

    @Column(name = "pay_method", nullable = false, length = 20)
    private String payMethod;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "have_discount", precision = 10, scale = 2)
    private BigDecimal haveDiscount;

}