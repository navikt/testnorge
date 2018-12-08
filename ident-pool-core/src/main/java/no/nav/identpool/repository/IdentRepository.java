package no.nav.identpool.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Rekvireringsstatus;
import org.springframework.data.repository.Repository;

public interface IdentRepository extends Repository<IdentEntity, Long>, QuerydslPredicateExecutor<IdentEntity> {

    boolean existsByPersonidentifikator(String identifikator);

    long countByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatus(LocalDate from, LocalDate to, Identtype type, Rekvireringsstatus rekvireringsstatus);

    IdentEntity findTopByPersonidentifikator(String personidentifkator);

    List<IdentEntity> saveAll(Iterable<IdentEntity> entities);

    IdentEntity save(IdentEntity newIdentEntity);

    void deleteAll();
}
