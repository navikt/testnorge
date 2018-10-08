package no.nav.identpool.ident.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import no.nav.identpool.ident.domain.Identtype;
import no.nav.identpool.ident.domain.Rekvireringsstatus;

public interface IdentRepository extends JpaRepository<IdentEntity, Long>, QuerydslPredicateExecutor<IdentEntity> {

    boolean existsByPersonidentifikator(String identifikator);

    long countByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatus(LocalDate from, LocalDate to, Identtype type, Rekvireringsstatus rekvireringsstatus);

    IdentEntity findTopByPersonidentifikator(String personidentifkator);
}
