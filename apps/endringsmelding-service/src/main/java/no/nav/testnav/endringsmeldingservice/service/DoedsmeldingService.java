package no.nav.testnav.endringsmeldingservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.endringsmeldingservice.consumer.TpsMessagingConsumer;
import no.nav.testnav.endringsmeldingservice.mapper.AdressehistorikkMapper;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.AdressehistorikkDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.DoedsmeldingRequest;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.DoedsmeldingResponse;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.IdentMiljoeDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PersonMiljoeDTO;
import no.nav.testnav.libs.dto.endringsmelding.v2.DoedsmeldingDTO;
import no.nav.testnav.libs.dto.endringsmelding.v2.DoedsmeldingResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static no.nav.testnav.endringsmeldingservice.mapper.AdressehistorikkMapper.buildAdresseRequest;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class DoedsmeldingService {

    private final TpsMessagingConsumer tpsMessagingConsumer;

    public Mono<DoedsmeldingResponseDTO> sendDoedsmelding(DoedsmeldingDTO doedsmelding, Set<String> miljoer) {

        if (isNotBlank(validate(doedsmelding))) {

            return Mono.just(DoedsmeldingResponseDTO.builder()
                    .error(validate(doedsmelding))
                    .build());
        }

        return tpsMessagingConsumer.getEksistererPerson(Set.of(doedsmelding.getIdent()), miljoer)
                .collectList()
                .flatMap(resultater -> {

                    if (resultater.stream()
                            .anyMatch(resultat -> resultat.getMiljoer().contains("p") ||
                                    !resultat.getMiljoer().containsAll(miljoer))) {

                        return getDoedsmeldingResponseDTO(miljoer, resultater);

                    } else {

                        return tpsMessagingConsumer.getPersondata(doedsmelding.getIdent(), miljoer)
                                .filter(PersonMiljoeDTO::isOk)
                                .flatMap(persondata -> {

                                    if (persondata.getPerson().isDoed()) {
                                        return tpsMessagingConsumer.getAdressehistorikk(buildAdresseRequest(persondata),
                                                        Set.of(persondata.getMiljoe()))
                                                .filter(AdressehistorikkDTO::isOk)
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
                });
    }

    private static Mono<DoedsmeldingResponseDTO> getDoedsmeldingResponseDTO(Set<String> miljoer, List<IdentMiljoeDTO> resultater) {

        return resultater.stream()
                .filter(resultat -> resultat.getMiljoer().contains("p") ||
                        !resultat.getMiljoer().containsAll(miljoer))
                .map(resultat ->
                        Mono.just(DoedsmeldingResponseDTO.builder()
                                .ident(resultat.getIdent())
                                .miljoStatus(resultat.getMiljoer().stream()
                                        .collect(Collectors.toMap(miljoe -> miljoe, miljoe -> "Ident finnes, men kansellering ikke utført")))
                                .error("FEIL: ident %s finnes ikke i alle forspurte miljøer %s".formatted(
                                        resultat.getIdent(), miljoer))
                                .build()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Feilet å dekode identstatus"));
    }

    public Mono<DoedsmeldingResponseDTO> sendKansellerDoedsmelding(String ident, Set<String> miljoer) {

        if (isBlank(ident)) {

            return Mono.just(DoedsmeldingResponseDTO.builder()
                    .error("FEIL: ident mangler")
                    .build());
        }

        return tpsMessagingConsumer.getEksistererPerson(Set.of(ident), miljoer)
                .collectList()
                .flatMap(resultater -> {

                    if (resultater.stream()
                            .anyMatch(resultat -> !resultat.getMiljoer().containsAll(miljoer))) {

                        return getDoedsmeldingResponseDTO(miljoer, resultater);

                    } else {

                        return tpsMessagingConsumer.getPersondata(ident, miljoer)
                                .filter(PersonMiljoeDTO::isOk)
                                .flatMap(persondata -> {
                                    if (persondata.getPerson().isDoed()) {
                                       return tpsMessagingConsumer.getAdressehistorikk(buildAdresseRequest(persondata),
                                                        Set.of(persondata.getMiljoe()))
                                                .filter(AdressehistorikkDTO::isOk)
                                                .map(AdressehistorikkDTO::getPersondata)
                                                .map(AdressehistorikkMapper::mapHistorikk)
                                                .flatMap(person -> tpsMessagingConsumer.sendKansellerDoedsmelding(person,
                                                        Set.of(persondata.getMiljoe())));
                                    } else {
                                        return Flux.just(DoedsmeldingResponse.builder()
                                                .ident(ident)
                                                .miljoStatus(Map.of(persondata.getMiljoe(), "OK"))
                                                .build());
                                    }
                                })
                                .collectList()
                                .map(resultat -> convertResult(ident, resultat));
                    }
                });
    }

    private static DoedsmeldingResponseDTO convertResult(String ident, List<DoedsmeldingResponse> result) {

        return DoedsmeldingResponseDTO.builder()
                .ident(ident)
                .miljoStatus(result.stream()
                        .map(DoedsmeldingResponse::getMiljoStatus)
                        .map(Map::entrySet)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .build();
    }

    private static String validate(DoedsmeldingDTO doedsmelding) {

        if (isBlank(doedsmelding.getIdent())) {
            return "FEIL: ident mangler";

        } else if (isNull(doedsmelding.getDoedsdato())) {
            return "FEIL: dødsdato mangler";

        }
        return null;
    }
}
