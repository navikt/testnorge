package no.nav.registre.aareg.provider.rs;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.util.Map;

import no.nav.registre.aareg.consumer.ws.request.RsAaregOppdaterRequest;
import no.nav.registre.aareg.consumer.ws.request.RsAaregOpprettRequest;
import no.nav.registre.aareg.provider.rs.response.RsAaregResponse;
import no.nav.registre.aareg.service.AaregService;

@RestController
@RequestMapping("api/v1/arbeidsforhold")
@RequiredArgsConstructor
public class ArbeidsforholdController {

    private final AaregService aaregService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RsAaregResponse opprettArbeidsforhold(
            @RequestBody RsAaregOpprettRequest request
    ) {
        return aaregService.opprettArbeidsforhold(request);
    }

    @PutMapping
    public RsAaregResponse oppdaterArbeidsforhold(
            @RequestBody RsAaregOppdaterRequest request
    ) {
        return RsAaregResponse.builder()
                .statusPerMiljoe(aaregService.oppdaterArbeidsforhold(request))
                .build();
    }

    @GetMapping
    public ResponseEntity<List<Map>> hentArbeidsforhold(
            @RequestParam String ident,
            @RequestParam String miljoe
    ) {
        return aaregService.hentArbeidsforhold(ident, miljoe);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> slettArbeidsforhold(
            @RequestParam String ident
    ) {
        return aaregService.slettArbeidsforhold(ident);
    }
}
