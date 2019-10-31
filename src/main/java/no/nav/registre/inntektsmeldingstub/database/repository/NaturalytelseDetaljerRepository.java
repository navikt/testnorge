package no.nav.registre.inntektsmeldingstub.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import no.nav.registre.inntektsmeldingstub.database.model.NaturalytelseDetaljer;

@Repository
public interface NaturalytelseDetaljerRepository extends CrudRepository<NaturalytelseDetaljer, Long> {
}
