package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Dokument;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

public interface DokumentRepository extends CrudRepository<Dokument, Long> {

    List<Dokument> getDokumentsByBestillingId(Long bestillingId);

    List<Dokument> getDokumentsByIdIsIn(Collection<Long> dokumentIdListe);
}