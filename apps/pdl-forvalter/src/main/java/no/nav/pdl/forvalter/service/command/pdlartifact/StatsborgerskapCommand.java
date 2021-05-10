package no.nav.pdl.forvalter.service.command.pdlartifact;

import no.nav.pdl.forvalter.domain.Identtype;
import no.nav.pdl.forvalter.domain.PdlInnflytting;
import no.nav.pdl.forvalter.domain.PdlStatsborgerskap;
import no.nav.pdl.forvalter.service.PdlArtifactService;
import no.nav.pdl.forvalter.service.command.IdenttypeFraIdentCommand;
import no.nav.pdl.forvalter.service.command.TilfeldigLandCommand;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.NORGE;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.isLandkode;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class StatsborgerskapCommand extends PdlArtifactService<PdlStatsborgerskap> {

    private static final String VALIDATION_LANDKODE_ERROR = "Landkode må oppgis i statsborgerskap";
    private static final String VALIDATION_OPPHOLD_ERROR = "Ugyldig datointervall: gyldigFom må være før gyldigTom";

    private final String ident;
    private final PdlInnflytting innflytting;

    public StatsborgerskapCommand(List<PdlStatsborgerskap> request, String ident, PdlInnflytting innflytting) {
        super(request);
        this.ident = ident;
        this.innflytting = innflytting;
    }

    @Override
    protected void validate(PdlStatsborgerskap statsborgerskap) {

        if (!isLandkode(statsborgerskap.getLandkode())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_LANDKODE_ERROR);
        }
        if (statsborgerskap.getGyldigFom().isAfter(statsborgerskap.getGyldigTom())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_OPPHOLD_ERROR);
        }
    }

    @Override
    public void handle(PdlStatsborgerskap statsborgerskap) {

        if (isBlank(statsborgerskap.getLandkode())) {
            if (nonNull(innflytting)) {
                statsborgerskap.setLandkode(innflytting.getFraflyttingsland());
            } else if (Identtype.FNR.equals(new IdenttypeFraIdentCommand(ident).call())) {
                statsborgerskap.setLandkode(NORGE);
            } else {
                statsborgerskap.setLandkode(new TilfeldigLandCommand().call());
            }
        }

        if (isNull(statsborgerskap.getGyldigFom())) {
            statsborgerskap.setGyldigFom(new DatoFraIdentCommand(ident).call().atStartOfDay());
        }
    }
}
