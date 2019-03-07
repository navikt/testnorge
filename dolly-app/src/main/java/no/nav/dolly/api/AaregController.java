package no.nav.dolly.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.aareg.AaregConsumer;
import no.nav.dolly.domain.resultset.aareg.RsAaregRequest;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/aareg", produces = MediaType.APPLICATION_JSON_VALUE)
public class AaregController {

    private static final String AAREG_JSON_COMMENT = "Følgende felter er kodeverksfelter med kodeverksdomene i parentes: <br />"
            + "&nbsp;&nbsp;&nbsp; arbeidsforholdstype: (Arbeidsforholdstyper) <br />"
            + "&nbsp;&nbsp;&nbsp; arbeidstidsordning: (Arbeidstidsordninger) <br />"
            + "&nbsp;&nbsp;&nbsp; avlønningstype: (Avlønningstyper) <br />"
            + "&nbsp;&nbsp;&nbsp; identtype: (Personidenter) <br />"
            + "&nbsp;&nbsp;&nbsp; land: (Landkoder) <br />"
            + "&nbsp;&nbsp;&nbsp; permisjonOgPermittering: (PermisjonsOgPermitteringsBeskrivelse) <br />"
            + "&nbsp;&nbsp;&nbsp; yrke; (Yrker) <br /><br />"
            + "Alle datofelter har forventet innhold \"yyyy-MM-ddT00:00:00\". <br /> <br />"
            + "Attributt aktoer er abstrakt og kan ha et av følgende objekttyper: <br />"
            + "For Organisajon: <br />"
            + "&nbsp; \"aktoer\": { <br />"
            + "&nbsp;&nbsp;&nbsp; \"aktoertype\": \"ORG\", <br />"
            + "&nbsp;&nbsp;&nbsp; \"orgnummer\": \"<orgnummer>\" <br />"
            + "&nbsp; } <br />"
            + "For Person: <br />"
            + "&nbsp; \"aktoer\": { <br />"
            + "&nbsp;&nbsp;&nbsp; \"aktoertype\": \"PERS\", <br />"
            + "&nbsp;&nbsp;&nbsp; \"ident\": \"<ident>\", <br />"
            + "&nbsp;&nbsp;&nbsp; \"identtype\": \"<identtype>\" <br />"
            + "&nbsp; }";

    @Autowired
    private AaregConsumer aaregConsumer;

    @ApiOperation(value = "Opprett attributter for aareg ", notes = AAREG_JSON_COMMENT)
    @PostMapping("/arbeidsforhold")
    public void opprettArbeidsforhold(@RequestBody RsAaregRequest request) {
        try {
            aaregConsumer.opprettArbeidsforhold(request);
        } catch (Exception e) {
            log.error("Oppretting av arbeidsforhold feilet.", e);
        }
    }

    @ApiOperation(value = "Oppdater attributter for aareg ", notes = AAREG_JSON_COMMENT)
    @PutMapping("/arbeidsforhold")
    public void oppdaterArbeidsforhold(@RequestBody RsAaregRequest request) {
        try {
            aaregConsumer.oppdaterArbeidsforhold(request);
        } catch (Exception e) {
            log.error("Oppdatering av arbeidsforhold feilet.", e);
        }
    }
}
