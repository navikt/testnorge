package no.nav.dolly.provider.api;

import static no.nav.dolly.provider.api.documentation.DocumentationNotes.AAREG_JSON_COMMENT;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.aareg.AaregReleaseIdentClient;
import no.nav.dolly.consumer.aareg.AaregRestConsumer;
import no.nav.dolly.consumer.aareg.AaregWsConsumer;
import no.nav.dolly.domain.resultset.aareg.RsAaregOppdaterRequest;
import no.nav.dolly.domain.resultset.aareg.RsAaregOpprettRequest;
import no.nav.dolly.domain.resultset.aareg.RsAaregResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/aareg", produces = MediaType.APPLICATION_JSON_VALUE)
public class AaregController {

    private final AaregWsConsumer aaregWsConsumer;
    private final AaregRestConsumer aaregRestConsumer;
    private final AaregReleaseIdentClient releaseIdentClient;

    @ApiOperation(value = "Opprett arbeidsforhold mot Aareg", notes = AAREG_JSON_COMMENT)
    @PostMapping("/arbeidsforhold")
    @ResponseStatus(HttpStatus.CREATED)
    public RsAaregResponse opprettArbeidsforhold(@RequestBody RsAaregOpprettRequest request) {

        return RsAaregResponse.builder().statusPerMiljoe(aaregWsConsumer.opprettArbeidsforhold(request)).build();
    }

    @ApiOperation(value = "Oppdater arbeidsforhold mot Aareg", notes = AAREG_JSON_COMMENT
            + "Rapporteringsperiode formidler innhold for måned og år kun.")
    @PutMapping("/arbeidsforhold")
    public RsAaregResponse oppdaterArbeidsforhold(@RequestBody RsAaregOppdaterRequest request) {

        return RsAaregResponse.builder().statusPerMiljoe(aaregWsConsumer.oppdaterArbeidsforhold(request)).build();
    }

    @ApiOperation(value = "Les arbeidsforhold fra Aareg")
    @GetMapping("/arbeidsforhold")
    public ResponseEntity lesArbeidsforhold(@RequestParam String ident, @RequestParam String environment) {

        return aaregRestConsumer.readArbeidsforhold(ident, environment);
    }

    @ApiOperation(value = "Slett arbeidsforhold fra Aareg", notes = "Arbeidsforhold blir ikke slettet, men eksisterende forhold blir satt inaktive")
    @DeleteMapping("/arbeidsforhold")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> slettArbeidsforhold(@RequestParam String ident) {

        return releaseIdentClient.deleteArbeidsforhold(ident);
    }
}
