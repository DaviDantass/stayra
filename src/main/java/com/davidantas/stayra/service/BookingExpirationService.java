package com.davidantas.stayra.service;

import com.davidantas.stayra.entity.Booking;
import com.davidantas.stayra.entity.enums.BookingStatus;
import com.davidantas.stayra.exception.ResourceNotFoundException;
import com.davidantas.stayra.repository.BookingRepository;
import com.davidantas.stayra.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingExpirationService {
    private final PropertyRepository propertyRepository;
    private final BookingRepository bookingRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public int expireOverdueForPropery(Long propertyId) {
        // obtem o lock antes de buscar as reservas (pg)
        propertyRepository.findByIdForUpdate(propertyId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Propriedade não encontrada"
                        )
                );
        // captura o horario antes de obter o lock
        OffsetDateTime now =
                OffsetDateTime.now(ZoneOffset.UTC);

        // Busca o estado atual das pendencias vencidas
        List<Booking> overdueBookings =
                bookingRepository
                        .findByProperty_IdAndStatusAndExpiresAtLessThanEqual(
                                propertyId,
                                BookingStatus.PENDING,
                                now
                        );
        // Altera as entidades dentro da mesma transação.
        int expiredCount = 0;

        for (Booking booking : overdueBookings) {
            if (booking.expireIfOverdue(now)) {
                expiredCount++;
            }
        }
        return expiredCount;
    }

    /*
  → obter lock da propriedade
  → capturar horário
  → consultar reservas
  → alterar status
  → gravar e concluir transação
  → liberar lock
    * */
}
