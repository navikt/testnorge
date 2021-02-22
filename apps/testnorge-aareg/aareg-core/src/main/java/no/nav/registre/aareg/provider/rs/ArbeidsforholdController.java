package no.nav.registre.aareg.provider.rs;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import no.nav.registre.aareg.consumer.rs.MiljoerConsumer;
import no.nav.registre.aareg.consumer.ws.request.RsAaregOppdaterRequest;
import no.nav.registre.aareg.consumer.ws.request.RsAaregOpprettRequest;
import no.nav.registre.aareg.provider.rs.response.RsAaregResponse;
import no.nav.registre.aareg.service.AaregService;
import no.nav.registre.testnorge.domain.dto.aordningen.arbeidsforhold.Arbeidsforhold;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("api/v1/arbeidsforhold")
@RequiredArgsConstructor
public class ArbeidsforholdController {

    private final AaregService aaregService;
    private final MiljoerConsumer miljoerConsumer;

    @PostMapping
    @ApiOperation(value = "Legg arbeidsforhold inn i aareg.")
    @ResponseStatus(HttpStatus.CREATED)
    public RsAaregResponse opprettArbeidsforhold(
            @RequestHeader("Nav-Call-Id") String navCallId,
            @RequestBody RsAaregOpprettRequest request
    ) {
        if (request.getArkivreferanse() == null) {
            request.setArkivreferanse(navCallId);
        }
        return aaregService.opprettArbeidsforhold(request);
    }

    @PutMapping
    @ApiOperation(value = "Oppdater arbeidsforhold i aareg.")
    public RsAaregResponse oppdaterArbeidsforhold(
            @RequestHeader("Nav-Call-Id") String navCallId,
            @RequestBody RsAaregOppdaterRequest request
    ) {
        if (request.getArkivreferanse() == null) {
            request.setArkivreferanse(navCallId);
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
            @RequestHeader("Nav-Call-Id") String navCallId,
            @RequestParam String ident,
            @RequestParam(required = false, defaultValue = "") List<String> miljoer
    ) {
        if (miljoer == null || miljoer.isEmpty()) {
            miljoer = miljoerConsumer.hentMiljoer().getEnvironments();
        }
        return aaregService.slettArbeidsforhold(ident, miljoer, navCallId);
    }
}
