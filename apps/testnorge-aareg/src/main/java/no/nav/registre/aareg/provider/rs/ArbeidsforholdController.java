package no.nav.registre.aareg.provider.rs;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.aareg.consumer.rs.MiljoerConsumer;
import no.nav.registre.aareg.consumer.ws.request.RsAaregOppdaterRequest;
import no.nav.registre.aareg.consumer.ws.request.RsAaregOpprettRequest;
import no.nav.registre.aareg.provider.rs.response.RsAaregResponse;
import no.nav.registre.aareg.service.AaregService;
import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.Arbeidsforhold;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("api/v1/arbeidsforhold")
@RequiredArgsConstructor
public class ArbeidsforholdController {

    private static final String NAV_CALL_ID = "DOLLY";

    private final AaregService aaregService;
    private final MiljoerConsumer miljoerConsumer;

    @PostMapping
    @ApiOperation(value = "Legg arbeidsforhold inn i aareg.")
    @ResponseStatus(HttpStatus.CREATED)
    public RsAaregResponse opprettArbeidsforhold(
            @RequestBody RsAaregOpprettRequest request
    ) {
        if (request.getArkivreferanse() == null) {
            request.setArkivreferanse(NAV_CALL_ID);
        }
        log.info("Arbeidsforhold mottatt: {}", Json.pretty(request));
        return aaregService.opprettArbeidsforhold(request);
    }

    @PutMapping
    @ApiOperation(value = "Oppdater arbeidsforhold i aareg.")
    public RsAaregResponse oppdaterArbeidsforhold(
            @RequestBody RsAaregOppdaterRequest request
    ) {
        if (request.getArkivreferanse() == null) {
            request.setArkivreferanse(NAV_CALL_ID);
        }
        return RsAaregResponse.builder()
                .statusPerMiljoe(aaregService.oppdaterArbeidsforhold(request))
                .build();
    }

    @GetMapping
    @ApiOperation(value = "Hent arbeidsforhold fra aareg.")
    public ResponseEntity<List<Arbeidsforhold>> hentArbeidsforhold(
            @RequestParam String ident,
            @RequestParam String miljoe
    ) {
        return aaregService.hentArbeidsforhold(ident, miljoe);
    }

    @DeleteMapping
    @ApiOperation(value = "Slett arbeidsforhold fra Aareg", notes = "Arbeidsforhold blir ikke slettet, men eksisterende forhold blir satt inaktive. Hvis miljøer ikke blir oppgitt,"
            + "vil applikasjonen utføre operasjonen i alle miljøer.")
    @ResponseStatus(HttpStatus.OK)
    public RsAaregResponse slettArbeidsforhold(
            @RequestParam String ident,
            @RequestParam(required = false, defaultValue = "") List<String> miljoer
    ) {
        if (miljoer == null || miljoer.isEmpty()) {
            miljoer = miljoerConsumer.hentMiljoer().getEnvironments();
        }
        return aaregService.slettArbeidsforhold(ident, miljoer, NAV_CALL_ID);
    }
}
