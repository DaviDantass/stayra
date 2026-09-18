package com.davidantas.stayra.entity;

import com.davidantas.stayra.entity.enums.BookingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "booking", indexes = {
        @Index(name = "idx_booking_guest", columnList = "guest_id"),
        @Index(name = "idx_booking_property_dates", columnList = "property_id,check_in,check_out")
})
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guest_id", nullable = false)
    private User guest;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @NotNull
    @Column(name = "check_in", nullable = false)
    private LocalDate checkIn;

    @NotNull
    @Column(name = "check_out", nullable = false)
    private LocalDate checkOut;

    @NotNull
    @DecimalMin(value = "0.00")
    @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice;

    @NotNull
    @Pattern(regexp = "^[A-Z]{3}$")
    @Column(nullable = false, length = 3)
    private String currency;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BookingStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "confirmed_at")
    private OffsetDateTime confirmedAt;

    @Column(name = "cancelled_at")
    private OffsetDateTime cancelledAt;

    // this class (OffsetDateTime) is immutable and thread-safe
    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    public boolean expireIfOverdue(OffsetDateTime now) {
        if (status != BookingStatus.PENDING) {
            return false;
        }
        if (expiresAt == null) {
            throw new IllegalStateException("Reserva pendente deve ter prazo de expiração\"");
        }
        if (now.isBefore(expiresAt)) {
            return false;
        }
        this.status = BookingStatus.EXPIRED;
        return true;
    }
}
