package no.nav.registre.testnorge.hendelse.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import no.nav.registre.testnorge.hendelse.repository.model.IdentModel;

public interface IdentRepository extends JpaRepository<IdentModel, Long> {
    Optional<IdentModel> findByIdent(String ident);
}
