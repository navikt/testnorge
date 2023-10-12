package no.nav.dolly.provider.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.consumer.kodeverk.KodeverkConsumer;
import no.nav.dolly.consumer.kodeverk.KodeverkMapper;
import no.nav.dolly.domain.resultset.RsDollyStatistikk;
import no.nav.dolly.domain.resultset.RsTransaksjonMapping;
import no.nav.dolly.domain.resultset.kodeverk.KodeverkAdjusted;
import no.nav.dolly.service.DollyStatistikkService;
import no.nav.dolly.service.InntektsmeldingEnumService;
import no.nav.dolly.service.InntektsmeldingEnumService.EnumTypes;
import no.nav.dolly.service.TransaksjonMappingService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static io.micrometer.common.util.StringUtils.isBlank;
import static no.nav.dolly.config.CachingConfig.CACHE_KODEVERK;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class OppslagController {

    private final KodeverkMapper kodeverkMapper;
    private final KodeverkConsumer kodeverkConsumer;
    private final InntektsmeldingEnumService inntektsmeldingEnumService;
    private final TransaksjonMappingService transaksjonMappingService;
    private final DollyStatistikkService dollyStatistikkService;

    @Cacheable(CACHE_KODEVERK)
    @GetMapping("/kodeverk/{kodeverkNavn}")
    @Operation(description = "Hent kodeverk etter kodeverkNavn")
    public KodeverkAdjusted fetchKodeverkByName(@PathVariable("kodeverkNavn") String kodeverkNavn) {

        var response = kodeverkConsumer.fetchKodeverkByName(kodeverkNavn);
        return kodeverkMapper.mapBetydningToAdjustedKodeverk(kodeverkNavn, response)
                .blockFirst();
    }

    @GetMapping("/kodeverk")
    @Operation(description = "Hent kodeverk, returnerer map")
    public Mono<Map<String, String>> fetchKodeverk(@RequestParam String kodeverk) {

        return kodeverkConsumer.getKodeverkByName(kodeverk);
    }

    @GetMapping("/inntektsmelding/{enumtype}")
    @Operation(description = "Henter enumtyper for inntektsmelding")
    public List<String> getInntektsmeldingeTyper(@PathVariable EnumTypes enumtype) {

        return inntektsmeldingEnumService.getEnumType(enumtype);
    }

    @GetMapping("/transaksjonid")
    @Operation(description = "Henter transaksjon IDer for bestillingId, ident og system")
    public List<RsTransaksjonMapping> getTransaksjonIderIdent(
            @Parameter(description = "En ID som identifiserer en bestilling mot Dolly") @RequestParam(required = false) Long
                    bestillingId,
            @Parameter(description = "Ident (f.eks FNR) på person knyttet til en bestilling") @RequestParam String
                    ident,
            @Parameter(description = "System kan hentes ut fra /api/v1/systemer") @RequestParam(required = false) String
                    system) {

        return transaksjonMappingService.getTransaksjonMapping(system, ident, bestillingId);
    }

    @GetMapping("/statistikk")
    @Operation(description = "Henter statistikk for bruk av dolly basert på brukerens ID")
    public RsDollyStatistikk getStatistikkByUser(
            @RequestParam(value = "brukerId") String brukerId) {
        if (isBlank(brukerId)) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Henting av statistikk krever ID for ønsket bruker");
        }

        return dollyStatistikkService.getDollyStatistikk(brukerId);
    }
}
