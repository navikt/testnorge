package no.nav.registre.spion.provider.rs;

import java.util.List;
import java.util.Objects;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.spion.provider.rs.request.SyntetiserSpionRequest;
import no.nav.registre.spion.provider.rs.response.SyntetiserSpionResponse;
import no.nav.registre.spion.service.SyntetiseringService;

@RestController
@RequestMapping("api/v1/syntetisering")
@RequiredArgsConstructor
public class SyntetiseringController {

    private final SyntetiseringService syntetiseringService;

    private static final String PARAM_DESCRIPTION = "Ingen av parameterne i request body er nødvendig. Hvis en " +
            "parameter mangler blir den satt til default verdi. \n\n " +
            "- **antallPerioder**: antall perioder det skal syntetiseres vedtak for. Hvis satt gjelder det for alle" +
            " personer/identer. Default: for hver person det skal syntetiseres vedtak for blir et tilfeldig tall " +
            "mellom 1 og 15 valgt.\n\n " +
            "- **antallPersoner**: antall personer det skal syntetiseres vedtak for. Tilfeldig ident med arbeidsforhold " +
            "blir hentet fra aareg for hver person. Default: 1. \n\n " +
            "- **avspillergruppeId**: hvilken avspillergruppe i tps forvalteren som identer skal hentes fra. Default:" +
            " avspillergruppeId for Mini-Norge(q2). \n\n " +
            "- **miljoe**: hvilket miljø som avspillergruppeId er koblet opp mot. Default: q2. \n\n " +
            "- **startDato**: startdato for perioden det ønsket å syntetisere vedtak for. Default: hvis sluttDato har " +
            "verdi blir den satt til sluttDato minus 18 måneder. Hvis sluttDato også mangler verdi blir den satt til " +
            "dagens dato minus 18 måneder. \n\n" +
            "- **sluttDato**: sluttdato for perioden det ønskes å syntetisere vedtak for. Default: hvis startDato har " +
            "verdi blir den satt til startDato pluss 18 måneder. Hvis startDato også mangler verdi blir den satt til " +
            "dagens dato. ";

    @Value("${avspillergruppe-id}")
    private String defaultAvspillergruppeId;

    @Value("${avspillergruppe-miljoe}")
    private String defaultMiljoe;

    @PostMapping(value = "/vedtak")
    @ApiOperation(value="Generer syntetiske vedtak for et gitt antall personer.",
            notes = "")
    public List<SyntetiserSpionResponse> genererVedtak(@ApiParam(value=PARAM_DESCRIPTION) @RequestBody SyntetiserSpionRequest request){

        return syntetiseringService.syntetiserVedtak(
                Objects.isNull(request.getAvspillergruppeId())?
                        Long.valueOf(defaultAvspillergruppeId) : request.getAvspillergruppeId() ,
                Objects.isNull(request.getMiljoe()) ? defaultMiljoe : request.getMiljoe(),
                request.getNumPersons(),
                request.getStartDate(),
                request.getEndDate(),
                request.getNumPeriods());
    }
}
