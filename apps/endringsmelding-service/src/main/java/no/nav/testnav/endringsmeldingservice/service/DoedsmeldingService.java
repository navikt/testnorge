package no.nav.testnav.endringsmeldingservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.endringsmeldingservice.consumer.TpsMessagingConsumer;
import no.nav.testnav.endringsmeldingservice.mapper.AdressehistorikkMapper;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.AdressehistorikkDTO;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.DoedsmeldingRequest;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.DoedsmeldingResponse;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.PersonMiljoeDTO;
import no.nav.testnav.libs.dto.endringsmelding.v2.DoedsmeldingDTO;
import no.nav.testnav.libs.dto.endringsmelding.v2.DoedsmeldingResponseDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

import static no.nav.testnav.endringsmeldingservice.mapper.AdressehistorikkMapper.buildAdresseRequest;

@Service
@RequiredArgsConstructor
public class DoedsmeldingService {

    private final TpsMessagingConsumer tpsMessagingConsumer;

    public Mono<DoedsmeldingResponseDTO> sendDoedsmelding(DoedsmeldingDTO doedsmelding, Set<String> miljoer) {

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
                })
                .map(DoedsmeldingResponse::getMiljoStatus)
                .reduce((firstMap, secondMap) -> {
                    firstMap.putAll(secondMap);
                    return firstMap;
                })
                .map(status -> DoedsmeldingResponseDTO.builder()
                        .ident(doedsmelding.getIdent())
                        .miljoStatus(status)
                        .build());
    }

    public Mono<DoedsmeldingResponseDTO> sendKansellerDoedsmelding(String ident, Set<String> miljoer) {

        return tpsMessagingConsumer.getPersondata(ident, miljoer)
                        .filter(PersonMiljoeDTO::isOk)
                        .filter(persondata -> persondata.getPerson().isDoed())
                        .collectList()
                        .flatMap(persondata -> tpsMessagingConsumer.getAdressehistorikk(buildAdresseRequest(persondata.getFirst()),
                                        persondata.stream().map(PersonMiljoeDTO::getMiljoe).collect(Collectors.toSet()))
                                .map(AdressehistorikkDTO::getPersondata)
                                .map(AdressehistorikkMapper::mapHistorikk)
                                .collectList()
                                .flatMap(personer ->
                                        tpsMessagingConsumer.sendKansellerDoedsmelding(personer.getFirst(),
                                                persondata.stream().map(PersonMiljoeDTO::getMiljoe).collect(Collectors.toSet()))))
                .map(response -> DoedsmeldingResponseDTO.builder()
                        .ident(ident)
                        .miljoStatus(response.getMiljoStatus())
                        .build());
    }
}
