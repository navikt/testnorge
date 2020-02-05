package no.nav.dolly.common.repository;

import org.springframework.data.repository.CrudRepository;

import no.nav.dolly.domain.jpa.Bruker;

public interface BrukerTestRepository extends CrudRepository<Bruker, Long> {

    Bruker findBrukerByBrukerId(String standardNavIdent);

    void flush();
}