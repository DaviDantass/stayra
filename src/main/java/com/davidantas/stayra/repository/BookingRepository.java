package com.davidantas.stayra.repository;

import com.davidantas.stayra.entity.Booking;
import com.davidantas.stayra.entity.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface BookingRepository
        extends JpaRepository<Booking, Long> {

    List<Booking> findByStatusAndExpiresAtLessThanEqual(
            BookingStatus status,
            OffsetDateTime now
    );

    List<Booking> findByProperty_IdAndStatusAndExpiresAtLessThanEqual(
            Long propertyId,
            BookingStatus status,
            OffsetDateTime now
    );
}