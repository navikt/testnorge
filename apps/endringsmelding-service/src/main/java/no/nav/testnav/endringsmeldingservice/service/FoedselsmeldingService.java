package no.nav.testnav.endringsmeldingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.endringsmeldingservice.consumer.AdresseServiceConsumer;
import no.nav.testnav.endringsmeldingservice.consumer.GenererNavnServiceConsumer;
import no.nav.testnav.endringsmeldingservice.consumer.IdentPoolConsumer;
import no.nav.testnav.endringsmeldingservice.consumer.TpsMessagingConsumer;
import no.nav.testnav.endringsmeldingservice.mapper.FoedselsmeldingRequestMapper;
import no.nav.testnav.endringsmeldingservice.mapper.IdentpoolRequestMapper;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.AdressehistorikkDTO;
import no.nav.testnav.libs.dto.endringsmelding.v2.FoedselsmeldingDTO;
import no.nav.testnav.libs.dto.endringsmelding.v2.FoedselsmeldingResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoedselsmeldingService {

    private final IdentPoolConsumer identPoolConsumer;
    private final GenererNavnServiceConsumer genererNavnServiceConsumer;
    private final AdresseServiceConsumer adresseServiceConsumer;
    private final TpsMessagingConsumer tpsMessagingConsumer;

    public Mono<FoedselsmeldingResponseDTO> sendFoedselsmelding(FoedselsmeldingDTO request, Set<String> miljoer) {

        if (isNotBlank(validate(request))) {

            return Mono.just(FoedselsmeldingResponseDTO.builder()
                    .error(validate(request))
                    .build());
        }

        return tpsMessagingConsumer.getEksistererPerson(getForeldre(request), miljoer)
                .collectList()
                .flatMap(resultater -> {

                    if (resultater.stream()
                            .anyMatch(resultat -> !resultat.getMiljoer().containsAll(miljoer))) {

                        return resultater.stream()
                                .filter(resultat -> !resultat.getMiljoer().containsAll(miljoer))
                                .map(resultat ->
                                        Mono.just(FoedselsmeldingResponseDTO.builder()
                                                .ident(resultat.getIdent())
                                                .miljoStatus(resultat.getMiljoer().stream()
                                                        .collect(Collectors.toMap(miljoe -> miljoe, miljoe -> "Ident finnes, men oppretting ikke utført")))
                                                .error("FEIL: ident %s finnes ikke i alle forspurte miljøer %s".formatted(
                                                        resultat.getIdent(), miljoer))
                                                .build()))
                                .findFirst()
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Feilet å dekode identstatus"));

                    } else {
                        return Mono.zip(identPoolConsumer.acquireIdents(
                                                IdentpoolRequestMapper.convert(request)),
                                        genererNavnServiceConsumer.getNavn(),
                                        adresseServiceConsumer.getVegadresse(),
                                        tpsMessagingConsumer.getAdressehistorikk(request.getIdentMor(), request.getFoedselsdato(), miljoer).collectList(),
                                        isBlank(request.getIdentFar()) ? Mono.just(Collections.<AdressehistorikkDTO>emptyList()) :
                                        tpsMessagingConsumer.getAdressehistorikk(request.getIdentFar(), request.getFoedselsdato(), miljoer).collectList())
                                .flatMap(opplysninger -> Mono.just(FoedselsmeldingRequestMapper.map(request, opplysninger))
                                        .flatMap(foedselsmelding -> tpsMessagingConsumer.sendFoedselsmelding(foedselsmelding,
                                                opplysninger.getT4().stream().map(AdressehistorikkDTO::getMiljoe).collect(Collectors.toSet()))))
                                .map(response -> FoedselsmeldingResponseDTO.builder()
                                        .ident(response.getIdent())
                                        .miljoStatus(response.getMiljoStatus())
                                        .build());
                    }
                });
    }

    private static String validate(FoedselsmeldingDTO request) {

        if (isBlank(request.getIdentMor())) {
            return "FEIL: mors ident mangler";

        } else if (isNull(request.getFoedselsdato())) {
            return "FEIL: fødselsdato mangler";

        } else if (isNull(request.getIdenttype())) {
            return "FEIL: identtype mangler";

        } else return null;
    }

    private static Set<String> getForeldre(FoedselsmeldingDTO request) {

        return Stream.of(List.of(request.getIdentMor()),
                        isNotBlank(request.getIdentFar()) ? List.of(request.getIdentFar()) : Collections.<String>emptyList())
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }
}
