package com.davidantas.stayra.repository;

import com.davidantas.stayra.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PropertyRepository extends JpaRepository<Property, Long> {

    @Query(value = """
            SELECT * FROM property WHERE id = :property FOR UPDATE
            """, nativeQuery = true)
    Optional<Property> findByIdForUpdate(@Param("propertyId") Long propertyId);
    /*
    Quando uma transação executa essa consulta, o PostgreSQL bloqueia a linha daquela propriedade. Outra transação tentando obter o mesmo lock
    espera até a primeira concluir ou desfazer suas alterações. Consultas comuns ainda podem ler a linha.

    quem controla o lock é o PostgreSQL. -> os services vão obter esse mesmo lock antes de modificar reservas ou bloqueios
    * */
}
