package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Dokument;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.Collection;

public interface DokumentRepository extends ReactiveCrudRepository<Dokument, Long> {

    Flux<Dokument> getDokumentsByBestillingId(Long bestillingId);

    Flux<Dokument> getDokumentsByIdIsIn(Collection<Long> dokumentIdListe);
}