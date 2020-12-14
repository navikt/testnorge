package no.nav.identpool.rs.v1;

import static no.nav.identpool.util.PersonidentUtil.validate;
import static no.nav.identpool.util.PersonidentUtil.validateMultiple;
import static no.nav.identpool.util.ValiderRequestUtil.validateDatesInRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.google.common.base.Strings;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.identpool.ajourhold.BatchService;
import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.postgres.Ident;
import no.nav.identpool.exception.IdentAlleredeIBrukException;
import no.nav.identpool.exception.UgyldigPersonidentifikatorException;
import no.nav.identpool.rs.v1.support.HentIdenterRequest;
import no.nav.identpool.rs.v1.support.MarkerBruktBatchRequest;
import no.nav.identpool.rs.v1.support.MarkerBruktBatchResponse;
import no.nav.identpool.rs.v1.support.MarkerBruktRequest;
import no.nav.identpool.service.IdentpoolService;
import no.nav.identpool.service.ny.PoolService;

@Slf4j
@RestController
@RequestMapping("/api/v1/identifikator")
@RequiredArgsConstructor
public class IdentpoolController {

    private final IdentpoolService identpoolService;
    private final BatchService batchService;
    private final PoolService poolService;

    @GetMapping
    @Operation(description = "hent informasjon lagret på en test-ident")
    public Ident lesInnhold(
            @RequestHeader String personidentifikator) {

        validate(personidentifikator);
        return identpoolService.lesInnhold(personidentifikator);
    }

    @PostMapping
    @Operation(description = "rekvirer nye test-identer")
    public List<String> rekvirer(
            @RequestParam(required = false, defaultValue = "true") boolean finnNaermesteLedigeDato,
            @RequestBody @Valid HentIdenterRequest hentIdenterRequest) {

        validateDatesInRequest(hentIdenterRequest);
        if (hentIdenterRequest.getFoedtFoer() == null) {
            hentIdenterRequest.setFoedtFoer(hentIdenterRequest.getFoedtEtter().plusDays(1));
        }
        if (Identtype.FDAT == hentIdenterRequest.getIdenttype()) {
            hentIdenterRequest.setKjoenn(null); // ident-pool ignorerer kjønn hos FDAT-identer
        }
        return poolService.allocateIdenter(hentIdenterRequest);

//        if (finnNaermesteLedigeDato) {
//            try {
//                return identpoolService.rekvirerNaermesteLedigDato(hentIdenterRequest);
//            } catch (IllegalArgumentException e) {
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage(), e);
//            }
//        } else {
//            try {
//                return identpoolService.rekvirer(hentIdenterRequest);
//            } catch (IllegalArgumentException e) {
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage(), e);
//            }
//        }
    }

    @PostMapping("/bruk")
    @Operation(description = "marker eksisterende og ledige identer som i bruk")
    public void markerBrukt(@RequestBody MarkerBruktRequest markerBruktRequest) {

        validate(markerBruktRequest.getPersonidentifikator());
        identpoolService.markerBrukt(markerBruktRequest);
    }

    @PostMapping("/brukFlere")
    @Operation(description = "Marker identer i gitt liste som I_BRUK i ident-pool-databasen. Returnerer en liste over de identene som nå er satt til I_BRUK.")
    public List<String> markerBruktIdenter(
            @RequestParam String rekvirertAv,
            @RequestBody List<String> identer) {

        if (Strings.isNullOrEmpty(rekvirertAv)) {
            throw new IllegalArgumentException("Felt 'rekvirertAv' må fylles ut");
        }
        validateMultiple(identer);
        return identpoolService.markerBruktFlere(rekvirertAv, identer);
    }

    /**
     * @deprecated (2019 - 02 - 09, erstattet av endepunkt / brukFlere, benytt endepunkt / brukFlere)
     */
    @Deprecated(since = "2019-02-09")
    @PostMapping("/bruk/batch")
    @Operation(description = "marker eksisterende og ledige identer som i bruk")
    public MarkerBruktBatchResponse markerBruktBatch(@RequestBody MarkerBruktBatchRequest markerBruktBatchRequest) {

        log.info("{} brukte deprecated endepunkt 'markerBruktBatch'", markerBruktBatchRequest.getBruker());
        MarkerBruktBatchResponse markerBruktBatchResponse = MarkerBruktBatchResponse.builder()
                .personidentifikatorerMarkertSomBrukt(new ArrayList<>())
                .personidentifikatorerSomIkkeKunneMarkeresSomBrukt(new ArrayList<>())
                .build();
        for (String personidentifikator : markerBruktBatchRequest.getPersonidentifikatorer()) {
            try {
                validate(personidentifikator);
                MarkerBruktRequest markerBruktRequest = MarkerBruktRequest.builder()
                        .personidentifikator(personidentifikator)
                        .bruker(markerBruktBatchRequest.getBruker())
                        .build();
                identpoolService.markerBrukt(markerBruktRequest);
                markerBruktBatchResponse.getPersonidentifikatorerMarkertSomBrukt().add(personidentifikator);
            } catch (UgyldigPersonidentifikatorException | IdentAlleredeIBrukException e) {
                markerBruktBatchResponse.getPersonidentifikatorerSomIkkeKunneMarkeresSomBrukt().add(personidentifikator);
                log.warn("Kunne ikke markere ident som brukt - {}", e.getLocalizedMessage(), e);
            }
        }
        return markerBruktBatchResponse;
    }

    @GetMapping("/ledig")
    @Operation(description = "returnerer true eller false avhengig av om en ident er ledig eller ikke")
    public Boolean erLedig(
            @RequestHeader String personidentifikator,
            @RequestParam(required = false) List<String> miljoer)  {

        validate(personidentifikator);
        return identpoolService.erLedig(personidentifikator, miljoer);
    }

    @GetMapping("/ledige")
    @Operation(description = "returnerer identer som er ledige og født mellom to datoer")
    public List<String> erLedige(
            @RequestParam int fromYear,
            @RequestParam int toYear) {

        return identpoolService.hentLedigeFNRFoedtMellom(LocalDate.of(fromYear, 1, 1), LocalDate.of(toYear, 1, 1));
    }

    @PostMapping("/frigjoer")
    @Operation(description = "Frigjør rekvirerte identer i en gitt liste. Returnerer de identene i den gitte listen som nå er ledige.")
    public List<String> frigjoerIdenter(
            @RequestParam String rekvirertAv,
            @RequestBody List<String> identer) {

        if (Strings.isNullOrEmpty(rekvirertAv)) {
            throw new IllegalArgumentException("Felt 'rekvirertAv' må fylles ut");
        }
        return identpoolService.frigjoerIdenter(rekvirertAv, identer);
    }

    @PostMapping("/frigjoerLedige")
    @Operation(description = "Frigjør rekvirerte, men ubrukte identer i en gitt liste. Returnerer de identene i den gitte listen som nå er ledige.")
    public List<String> frigjoerLedigeIdenter(@RequestBody List<String> identer) {

        return identpoolService.frigjoerLedigeIdenter(identer);
    }

    @GetMapping("/whitelist")
    @Operation(description = "returnerer en list over whitelisted identer")
    public List<String> hentWhitelist() {

        return identpoolService.hentWhitelist();
    }

    @PostMapping("/startBatch")
    public void startBatch() {

        batchService.startGeneratingIdentsBatch();
    }
}
