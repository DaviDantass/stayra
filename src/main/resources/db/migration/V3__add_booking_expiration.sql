-- Adiciona o prazo de expiração à reserva.
ALTER TABLE booking
    ADD COLUMN expires_at TIMESTAMPTZ;

-- Atualiza a lista de status permitidos.
ALTER TABLE booking
DROP
CONSTRAINT ck_booking_status;

ALTER TABLE booking
    ADD CONSTRAINT ck_booking_status CHECK (
        status IN (
                   'PENDING',
                   'CONFIRMED',
                   'CANCELLED',
                   'COMPLETED',
                   'EXPIRED'
            )
        );

UPDATE booking
SET expires_at = CURRENT_TIMESTAMP
WHERE status = 'PENDING';

ALTER TABLE booking
    ADD CONSTRAINT ck_booking_pending_expiration CHECK (
            status <> 'PENDING' OR expires_at IS NOT NULL
        );
