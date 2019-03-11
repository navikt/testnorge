package no.nav.dolly.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import no.nav.dolly.aareg.AaregConsumer;
import no.nav.dolly.domain.resultset.aareg.RsAaregOppdaterRequest;
import no.nav.dolly.domain.resultset.aareg.RsAaregOpprettRequest;
import no.nav.dolly.domain.resultset.aareg.RsAaregResponse;

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
            + "For organisajon: <br />"
            + "&nbsp; \"aktoer\": { <br />"
            + "&nbsp;&nbsp;&nbsp; \"aktoertype\": \"ORG\", <br />"
            + "&nbsp;&nbsp;&nbsp; \"orgnummer\": \"<orgnummer>\" <br />"
            + "&nbsp; } <br />"
            + "For person: <br />"
            + "&nbsp; \"aktoer\": { <br />"
            + "&nbsp;&nbsp;&nbsp; \"aktoertype\": \"PERS\", <br />"
            + "&nbsp;&nbsp;&nbsp; \"ident\": \"<ident>\", <br />"
            + "&nbsp;&nbsp;&nbsp; \"identtype\": \"<identtype>\" <br />"
            + "&nbsp; }";

    @Autowired
    private AaregConsumer aaregConsumer;

    @ApiOperation(value = "Opprett attributter for aareg ", notes = AAREG_JSON_COMMENT)
    @PostMapping("/arbeidsforhold")
    @ResponseStatus(HttpStatus.CREATED)
    public RsAaregResponse opprettArbeidsforhold(@RequestBody RsAaregOpprettRequest request) {

        return RsAaregResponse.builder().statusPerMiljoe(aaregConsumer.opprettArbeidsforhold(request)).build();
    }

    @ApiOperation(value = "Oppdater attributter for aareg ", notes = AAREG_JSON_COMMENT
            + "<br /><br />Rapporteringsperiode formidler innhold for måned og år kun.")
    @PutMapping("/arbeidsforhold")
    public RsAaregResponse oppdaterArbeidsforhold(@RequestBody RsAaregOppdaterRequest request) {

        return RsAaregResponse.builder().statusPerMiljoe(aaregConsumer.oppdaterArbeidsforhold(request)).build();
    }
}
