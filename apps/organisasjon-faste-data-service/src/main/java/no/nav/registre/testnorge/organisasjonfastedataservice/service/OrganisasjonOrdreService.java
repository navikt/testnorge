package no.nav.registre.testnorge.organisasjonfastedataservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.organisasjonfastedataservice.consumer.OrganisasjonBestillingConsumer;
import no.nav.registre.testnorge.organisasjonfastedataservice.consumer.OrganisasjonMottakConsumer;
import no.nav.registre.testnorge.organisasjonfastedataservice.domain.Organisasjon;
import no.nav.registre.testnorge.organisasjonfastedataservice.repository.OrganisasjonRepository;
import no.nav.testnav.libs.dto.organisajonbestilling.v1.ItemDTO;
import no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.Gruppe;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganisasjonOrdreService {
    private final OrganisasjonMottakConsumer organisasjonMottakConsumer;
    private final OrganisasjonBestillingConsumer organisasjonBestillingConsumer;
    private final OrganisasjonRepository repository;

    public Mono<String> create(Organisasjon organisasjon, String miljo) {
        return Mono.fromCallable(() -> {
            var ordreId = UUID.randomUUID().toString();
            return organisasjonMottakConsumer.create(organisasjon, miljo, ordreId);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<String> change(Organisasjon organisasjon, String miljo) {
        return Mono.fromCallable(() -> {
            var ordreId = UUID.randomUUID().toString();
            return organisasjonMottakConsumer.change(organisasjon, miljo, ordreId);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<List<ItemDTO>> getStatus(String ordreId) {
        return organisasjonBestillingConsumer.getOrdreStatus(ordreId);
    }

    public Mono<String> create(Gruppe gruppe, String miljo) {
        return Mono.fromCallable(() -> {
            var ordreId = UUID.randomUUID().toString();
            repository.findAllByGruppeAndOverenhetIsNull(gruppe)
                    .stream()
                    .map(Organisasjon::new)
                    .forEach(organisasjon -> organisasjonMottakConsumer.create(organisasjon, miljo, ordreId));
            return ordreId;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<String> change(Gruppe gruppe, String miljo) {
        return Mono.fromCallable(() -> {
            var ordreId = UUID.randomUUID().toString();
            repository.findAllByGruppeAndOverenhetIsNull(gruppe)
                    .stream()
                    .map(Organisasjon::new)
                    .forEach(organisasjon -> organisasjonMottakConsumer.change(organisasjon, miljo, ordreId));
            return ordreId;
        }).subscribeOn(Schedulers.boundedElastic());
    }

}
