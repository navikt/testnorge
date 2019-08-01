package no.nav.dolly.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import no.nav.dolly.aareg.AaregRestConsumer;
import no.nav.dolly.aareg.AaregWsConsumer;
import no.nav.dolly.domain.resultset.aareg.RsAaregOppdaterRequest;
import no.nav.dolly.domain.resultset.aareg.RsAaregOpprettRequest;
import no.nav.dolly.domain.resultset.aareg.RsAaregResponse;

@RestController
@RequestMapping(value = "/api/v1/aareg", produces = MediaType.APPLICATION_JSON_VALUE)
public class AaregController {

    protected static final String AAREG_JSON_COMMENT = "For AAREG-integrasjon er følgende felter kodeverksfelter med kodeverksdomene i parentes: <br />"
            + "&nbsp;&nbsp;&nbsp; arbeidsforholdstype: (Arbeidsforholdstyper) <br />"
            + "&nbsp;&nbsp;&nbsp; arbeidstidsordning: (Arbeidstidsordninger) <br />"
            + "&nbsp;&nbsp;&nbsp; avlønningstype: (Avlønningstyper) <br />"
            + "&nbsp;&nbsp;&nbsp; identtype: (Personidenter) <br />"
            + "&nbsp;&nbsp;&nbsp; land: (LandkoderISO2) <br />"
            + "&nbsp;&nbsp;&nbsp; permisjonOgPermittering: (PermisjonsOgPermitteringsBeskrivelse) <br />"
            + "&nbsp;&nbsp;&nbsp; yrke: (Yrker) <br /><br />"
            + "Alle datofelter har forventet innhold \"yyyy-MM-ddT00:00:00\". <br /> <br />"
            + "Attributt aktoer er abstrakt og kan ha et av følgende objekttyper: <br />"
            + "For organisajon: <br />"
            + "&nbsp; \"aktoer\": { <br />"
            + "&nbsp;&nbsp;&nbsp; \"aktoertype\": \"ORG\", <br />"
            + "&nbsp;&nbsp;&nbsp; \"orgnummer\": \"<orgnummer>\" <br />"
            + "&nbsp; } <br />"
            + "For person: <br />"
            + "&nbsp; \"aktoer\": { <br />"
            + "&nbsp;&nbsp;&nbsp; \"aktoertype\": \"PERS\", <br />"
            + "&nbsp;&nbsp;&nbsp; \"ident\": \"<ident>\" <br />"
            + "&nbsp; } <br /><br />";

    @Autowired
    private AaregWsConsumer aaregWsConsumer;

    @Autowired
    private AaregRestConsumer aaregRestConsumer;

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
}
