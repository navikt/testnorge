package no.nav.testnav.endringsmeldingservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.endringsmeldingservice.consumer.TpsMessagingConsumer;
import no.nav.testnav.endringsmeldingservice.mapper.AdressehistorikkMapper;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.AdressehistorikkDTO;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.DoedsmeldingRequest;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.DoedsmeldingResponse;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.PersonMiljoeDTO;
import no.nav.testnav.libs.dto.endringsmelding.v2.DoedsmeldingDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Set;

import static no.nav.testnav.endringsmeldingservice.mapper.AdressehistorikkMapper.buildAdresseRequest;

@Service
@RequiredArgsConstructor
public class DoedsmeldingService {

    private final TpsMessagingConsumer tpsMessagingConsumer;

    public Flux<DoedsmeldingResponse> sendKansellerDoedsmelding(String ident, Set<String> miljoer) {

       return tpsMessagingConsumer.getPersondata(ident, miljoer)
                .filter(PersonMiljoeDTO::isOk)
                .filter(persondata -> persondata.getPerson().isDoed())
                .flatMap(persondata -> tpsMessagingConsumer.getAdressehistorikk(buildAdresseRequest(persondata),
                                Set.of(persondata.getMiljoe()))
                        .map(AdressehistorikkDTO::getPersondata)
                        .map(AdressehistorikkMapper::mapHistorikk)
                        .flatMap(person ->
                                tpsMessagingConsumer.sendKansellerDoedsmelding(person, Set.of(persondata.getMiljoe()))));
    }

    public Flux<DoedsmeldingResponse> sendDoedsmelding(DoedsmeldingDTO doedsmelding, Set<String> miljoer) {

        return tpsMessagingConsumer.getPersondata(doedsmelding.getIdent(), miljoer)
                .filter(PersonMiljoeDTO::isOk)
                .flatMap(persondata -> {

                    if (persondata.getPerson().isDoed()) {
                        return tpsMessagingConsumer.getAdressehistorikk(buildAdresseRequest(persondata),
                                        Set.of(persondata.getMiljoe()))
                                .map(AdressehistorikkDTO::getPersondata)
                                .map(AdressehistorikkMapper::mapHistorikk)
                                .flatMap(person ->
                                        tpsMessagingConsumer.sendKansellerDoedsmelding(person, Set.of(persondata.getMiljoe())))
                                .flatMap(response -> tpsMessagingConsumer.sendDoedsmelding(DoedsmeldingRequest.builder()
                                                .ident(doedsmelding.getIdent())
                                                .doedsdato(doedsmelding.getDoedsdato())
                                                .build(), Set.of(persondata.getMiljoe())));

                    } else {
                        return tpsMessagingConsumer.sendDoedsmelding(DoedsmeldingRequest.builder()
                                .ident(doedsmelding.getIdent())
                                .doedsdato(doedsmelding.getDoedsdato())
                                .build(), Set.of(persondata.getMiljoe()));

                    }
                });
    }
}
