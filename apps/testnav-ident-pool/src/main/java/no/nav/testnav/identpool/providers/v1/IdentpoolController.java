package no.nav.testnav.identpool.providers.v1;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.identpool.domain.Ident;
import no.nav.testnav.identpool.dto.TpsStatusDTO;
import no.nav.testnav.identpool.providers.v1.support.HentIdenterRequest;
import no.nav.testnav.identpool.providers.v1.support.MarkerBruktRequest;
import no.nav.testnav.identpool.service.IdentpoolService;
import no.nav.testnav.identpool.service.PoolService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static no.nav.testnav.identpool.util.PersonidentUtil.validate;
import static no.nav.testnav.identpool.util.ValiderRequestUtil.validateDatesInRequest;

@Slf4j
@RestController
@RequestMapping("/api/v1/identifikator")
@RequiredArgsConstructor
public class IdentpoolController {

    private final IdentpoolService identpoolService;
    private final PoolService poolService;

    @GetMapping
    @Operation(description = "hent informasjon lagret på en test-ident")
    public Mono<Ident> lesInnhold(
            @RequestHeader String personidentifikator) {

        validate(personidentifikator);
        return identpoolService.lesInnhold(personidentifikator);
    }

    @PostMapping
    @Operation(description = "rekvirer nye test-identer")
    public Mono<List<String>> rekvirer(
            @RequestBody @Valid HentIdenterRequest hentIdenterRequest) {

        var startTime = System.currentTimeMillis();

        validateDatesInRequest(hentIdenterRequest);
        if (isNull(hentIdenterRequest.getFoedtFoer())) {
            hentIdenterRequest.setFoedtFoer(LocalDate.now());
        }
        if (isNull(hentIdenterRequest.getFoedtEtter())) {
            hentIdenterRequest.setFoedtEtter(LocalDate.of(1900, 1, 1));
        }
        return poolService.allocateIdenter(hentIdenterRequest)
                .doOnNext(identer ->
                        log.info("rekvirer: {} medgått tid: {} ms", hentIdenterRequest,
                                System.currentTimeMillis() - startTime));
    }

    @PostMapping("/bruk")
    @Operation(description = "marker eksisterende og ledige identer som i bruk")
    public Mono<Void> markerBrukt(@RequestBody MarkerBruktRequest markerBruktRequest) {

        validate(markerBruktRequest.getPersonidentifikator());
        return identpoolService.markerBrukt(markerBruktRequest)
                .then();
    }

    @GetMapping("/prod-sjekk")
    @Operation(description = "returnerer om en liste av identer finnes i prod.")
    public Flux<TpsStatusDTO> erIProd(@RequestParam Set<String> identer) {

        return identpoolService.finnesIProd(identer);
    }

    @GetMapping("/ledig")
    @Operation(description = "returnerer true eller false avhengig av om ident er ledig. " +
            "OBS kun TPS prod-miljø sjekkes for ikke-syntetisk")
    public Mono<Boolean> erLedig(
            @RequestHeader String personidentifikator) {

        validate(personidentifikator);
        return identpoolService.erLedig(personidentifikator);
    }

    @GetMapping("/ledige")
    @Operation(description = "returnerer identer (FNR) som er ledige og født mellom to år inklusive start og slutt år")
    public Mono<List<String>> erLedige(@RequestParam int fromYear, @RequestParam int toYear,
                                       @RequestParam(required = false, defaultValue = "true") Boolean syntetisk) {

        return identpoolService.hentLedigeFNRFoedtMellom(LocalDate.of(fromYear, 1, 1),
                LocalDate.of(toYear, 1, 1), syntetisk);
    }

    @PostMapping("/frigjoer")
    @Operation(description = "Frigjør rekvirerte identer i en gitt liste. Returnerer de identene i den gitte listen som nå er ledige.")
    public Mono<List<String>> frigjoerIdenter(@RequestBody List<String> identer) {

        return identpoolService.frigjoerIdenter(identer);
    }
}
