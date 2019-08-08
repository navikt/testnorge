package no.nav.identpool.rs.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static no.nav.identpool.util.PersonidentUtil.validate;
import no.nav.identpool.domain.Ident;
import no.nav.identpool.exception.IdentAlleredeIBrukException;
import no.nav.identpool.exception.UgyldigPersonidentifikatorException;
import no.nav.identpool.rs.v1.support.HentIdenterRequest;
import no.nav.identpool.rs.v1.support.MarkerBruktBatchRequest;
import no.nav.identpool.rs.v1.support.MarkerBruktBatchResponse;
import no.nav.identpool.rs.v1.support.MarkerBruktRequest;
import no.nav.identpool.service.IdentpoolService;

@Slf4j
@RestController
@Api(tags = { "identifikator" })
@RequestMapping("/api/v1/identifikator")
@RequiredArgsConstructor
public class IdentpoolController {

    private final IdentpoolService identpoolService;

    @GetMapping
    @ApiOperation(value = "hent informasjon lagret på en test-ident")
    public Ident lesInnhold(
            @RequestHeader String personidentifikator
    ) throws UgyldigPersonidentifikatorException {
        validate(personidentifikator);
        return identpoolService.lesInnhold(personidentifikator);
    }

    @PostMapping
    @ApiOperation(value = "rekvirer nye test-identer")
    public List<String> rekvirer(@RequestBody @Valid HentIdenterRequest hentIdenterRequest) throws Exception {
        try {
            return identpoolService.rekvirer(hentIdenterRequest);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage(), e);
        }
    }

    @PostMapping("/bruk")
    @ApiOperation(value = "marker eksisterende og ledige identer som i bruk")
    public void markerBrukt(@RequestBody MarkerBruktRequest markerBruktRequest) throws Exception {
        validate(markerBruktRequest.getPersonidentifikator());
        identpoolService.markerBrukt(markerBruktRequest);
    }

    @PostMapping("/bruk/batch")
    @ApiOperation(value = "marker eksisterende og ledige identer som i bruk")
    public MarkerBruktBatchResponse markerBruktBatch(@RequestBody MarkerBruktBatchRequest markerBruktBatchRequest) {
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
    @ApiOperation(value = "returnerer true eller false avhengig av om en ident er ledig eller ikke")
    public Boolean erLedig(
            @RequestHeader String personidentifikator
    ) throws UgyldigPersonidentifikatorException {
        validate(personidentifikator);
        return identpoolService.erLedig(personidentifikator);
    }

    @GetMapping("/ledige")
    @ApiOperation(value = "returnerer identer som er ledige og født mellom to datoer")
    public List<String> erLedige(@RequestParam int fromYear, @RequestParam int toYear) {
        return identpoolService.hentLedigeFNRFoedtMellom(LocalDate.of(fromYear, 1, 1), LocalDate.of(toYear, 1, 1));
    }

    @PostMapping("/frigjoer")
    @ApiOperation(value = "Frigjør rekvirerte, men ubrukte identer i en gitt liste. Returnerer de identene i den gitte listen som nå er ledige.")
    public List<String> frigjoerLedigeIdenter(@RequestBody List<String> identer) {
        return identpoolService.frigjoerLedigeIdenter(identer);
    }

    @GetMapping("/whitelist")
    @ApiOperation(value = "returnerer en list over whitelisted identer")
    public List<String> hentWhitelist() {
        return identpoolService.hentWhitelist();
    }
}
