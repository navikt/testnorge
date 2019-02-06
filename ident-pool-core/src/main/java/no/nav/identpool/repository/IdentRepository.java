package no.nav.identpool.repository;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.Repository;

import java.time.LocalDate;
import java.util.List;

import no.nav.identpool.domain.Ident;
import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Rekvireringsstatus;

public interface IdentRepository extends Repository<Ident, Long>, QuerydslPredicateExecutor<Ident> {

    boolean existsByPersonidentifikator(String identifikator);

    long countByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatus(LocalDate from, LocalDate to, Identtype type, Rekvireringsstatus rekvireringsstatus);

    Ident findTopByPersonidentifikator(String personidentifkator);

    List<Ident> saveAll(Iterable<Ident> entities);

    Ident save(Ident newIdent);

    List<Ident> findByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatus(LocalDate from, LocalDate to, Identtype type, Rekvireringsstatus rekvireringsstatus);

    long countByRekvireringsstatusAndIdenttype(Rekvireringsstatus rekvireringsstatus, Identtype identtype);

    void deleteAll();
}
