package no.nav.skattekortservice.service;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.skattekortservice.consumer.SokosSkattekortConsumer;
import no.nav.skattekortservice.dto.v2.HentSkattekortRequest;
import no.nav.skattekortservice.dto.v2.OpprettSkattekortRequest;
import no.nav.skattekortservice.dto.v2.SkattekortDTO;
import no.nav.skattekortservice.utility.SkattekortValidator;
import no.nav.testnav.libs.dto.skattekortservice.v1.SkattekortRequestDTO;
import no.nav.testnav.libs.dto.skattekortservice.v1.SkattekortResponseDTO;
import no.nav.testnav.libs.dto.skattekortservice.v1.Skattekortmelding;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
public class SkattekortService {

    private final MapperFacade mapperFacade;
    private final SokosSkattekortConsumer skattekortConsumer;

    public SkattekortService(MapperFacade mapperFacade,
                             SokosSkattekortConsumer skattekortConsumer) {
        this.mapperFacade = mapperFacade;
        this.skattekortConsumer = skattekortConsumer;
    }

    public Mono<String> sendSkattekort(SkattekortRequestDTO skattekort) {

        SkattekortValidator.validate(skattekort);

        var arbeidstaker = skattekort.getArbeidsgiver().getFirst()
                .getArbeidstaker().getFirst();

        OpprettSkattekortRequest request = mapperFacade.map(arbeidstaker, OpprettSkattekortRequest.class);
        log.info("Sender skattekort til Sokos med request: {}", request);

        return skattekortConsumer.sendSkattekort(request);
    }

    public Flux<SkattekortResponseDTO> hentSkattekort(String ident, Integer inntektsaar) {

        var request = HentSkattekortRequest.builder()
                .fnr(ident)
                .inntektsaar(inntektsaar)
                .build();

        return skattekortConsumer.hentSkattekort(request)
                .doOnNext(response -> log.info("Hentet resultat fra Sokos {}", response))
                .map(skattekortDTO -> convertResponse(ident, skattekortDTO));
    }

    private SkattekortResponseDTO convertResponse(String ident, SkattekortDTO skattekortDTO) {
        var skattekortmelding = mapperFacade.map(skattekortDTO, Skattekortmelding.class);
        skattekortmelding.setArbeidstakeridentifikator(ident);

        var arbeidsgiver = no.nav.testnav.libs.dto.skattekortservice.v1.ArbeidsgiverSkatt.builder()
                .arbeidstaker(List.of(skattekortmelding))
                .build();

        return SkattekortResponseDTO.builder()
                .ident(ident)
                .inntektsaar(String.valueOf(skattekortDTO.getInntektsaar()))
                .arbeidsgiver(List.of(arbeidsgiver))
                .build();
    }
}