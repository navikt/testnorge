package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Dokument;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.Collection;
import java.util.List;

public interface DokumentRepository extends ReactiveCrudRepository<Dokument, Long> {

    List<Dokument> getDokumentsByBestillingId(Long bestillingId);

    List<Dokument> getDokumentsByIdIsIn(Collection<Long> dokumentIdListe);
}