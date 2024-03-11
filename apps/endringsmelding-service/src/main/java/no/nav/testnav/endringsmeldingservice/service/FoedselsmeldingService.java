package no.nav.testnav.endringsmeldingservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.endringsmeldingservice.consumer.AdresseServiceConsumer;
import no.nav.testnav.endringsmeldingservice.consumer.GenererNavnServiceConsumer;
import no.nav.testnav.endringsmeldingservice.consumer.IdentPoolConsumer;
import no.nav.testnav.endringsmeldingservice.consumer.TpsMessagingConsumer;
import no.nav.testnav.endringsmeldingservice.mapper.FoedselsmeldingRequestMapper;
import no.nav.testnav.endringsmeldingservice.mapper.IdentpoolRequestMapper;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.AdressehistorikkDTO;
import no.nav.testnav.libs.dto.endringsmelding.v2.FoedselsmeldingDTO;
import no.nav.testnav.libs.dto.endringsmelding.v2.FoedselsmeldingResponseDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoedselsmeldingService {

    private final IdentPoolConsumer identPoolConsumer;
    private final GenererNavnServiceConsumer genererNavnServiceConsumer;
    private final AdresseServiceConsumer adresseServiceConsumer;
    private final TpsMessagingConsumer tpsMessagingConsumer;

    public Mono<FoedselsmeldingResponseDTO> sendFoedselsmelding(FoedselsmeldingDTO request, Set<String> miljoer) {

        return Mono.zip(identPoolConsumer.acquireIdents(
                                IdentpoolRequestMapper.convert(request)),
                        genererNavnServiceConsumer.getNavn(),
                        adresseServiceConsumer.getVegadresse(),
                        tpsMessagingConsumer.getAdressehistorikk(request.getIdentMor(), request.getFoedselsdato(), miljoer).collectList(),
                        tpsMessagingConsumer.getAdressehistorikk(request.getIdentFar(), request.getFoedselsdato(), miljoer).collectList())
                .flatMap(opplysninger -> Mono.just(FoedselsmeldingRequestMapper.map(request, opplysninger))
                        .flatMap(foedselsmelding -> tpsMessagingConsumer.sendFoedselsmelding(foedselsmelding,
                                opplysninger.getT4().stream().map(AdressehistorikkDTO::getMiljoe).collect(Collectors.toSet()))))
                .map(response -> FoedselsmeldingResponseDTO.builder()
                        .ident(response.getIdent())
                        .miljoStatus(response.getMiljoStatus())
                        .build());
    }
}
