package no.nav.dolly.provider.api;

import static no.nav.dolly.provider.api.documentation.DocumentationNotes.AAREG_JSON_COMMENT;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import java.util.Map;

import no.nav.dolly.consumer.aareg.TestnorgeAaregConsumer;
import no.nav.dolly.domain.resultset.aareg.RsAaregOppdaterRequest;
import no.nav.dolly.domain.resultset.aareg.RsAaregOpprettRequest;
import no.nav.dolly.domain.resultset.aareg.RsAaregResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/aareg", produces = MediaType.APPLICATION_JSON_VALUE)
public class AaregController {

    private final TestnorgeAaregConsumer testnorgeAaregConsumer;

    @ApiOperation(value = "Opprett arbeidsforhold mot Aareg", notes = AAREG_JSON_COMMENT)
    @PostMapping("/arbeidsforhold")
    @ResponseStatus(HttpStatus.CREATED)
    public RsAaregResponse opprettArbeidsforhold(@RequestBody RsAaregOpprettRequest request) {
        return testnorgeAaregConsumer.opprettArbeidsforhold(request);
    }

    @ApiOperation(value = "Oppdater arbeidsforhold mot Aareg", notes = AAREG_JSON_COMMENT
            + "Rapporteringsperiode formidler innhold for måned og år kun.")
    @PutMapping("/arbeidsforhold")
    public RsAaregResponse oppdaterArbeidsforhold(@RequestBody RsAaregOppdaterRequest request) {
        return testnorgeAaregConsumer.oppdaterArbeidsforhold(request);
    }

    @ApiOperation(value = "Les arbeidsforhold fra Aareg")
    @GetMapping("/arbeidsforhold")
    public ResponseEntity lesArbeidsforhold(@RequestParam String ident, @RequestParam String environment) {
        return testnorgeAaregConsumer.hentArbeidsforhold(ident, environment);
    }

    @ApiOperation(value = "Slett arbeidsforhold fra Aareg", notes = "Arbeidsforhold blir ikke slettet, men eksisterende forhold blir satt inaktive")
    @DeleteMapping("/arbeidsforhold")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> slettArbeidsforhold(@RequestParam String ident) {
        return testnorgeAaregConsumer.slettArbeidsforholdFraAlleMiljoer(ident);
    }
}
