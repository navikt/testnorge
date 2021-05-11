package no.nav.pdl.forvalter.service.command.pdlartifact;

import no.nav.pdl.forvalter.domain.PdlAdressebeskyttelse;
import no.nav.pdl.forvalter.service.PdlArtifactService;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static no.nav.pdl.forvalter.domain.PdlAdresse.Master.FREG;
import static no.nav.pdl.forvalter.domain.PdlAdresse.Master.PDL;
import static no.nav.pdl.forvalter.domain.PdlAdressebeskyttelse.AdresseBeskyttelse.STRENGT_FORTROLIG_UTLAND;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class AdressebeskyttelseCommand extends PdlArtifactService<PdlAdressebeskyttelse> {

    private static final String VALIDATION_UTLAND_MASTER_ERROR = "Gradering STRENGT_FORTROLIG_UTLAND kan kun settes hvis master er PDL";

    public AdressebeskyttelseCommand(List<PdlAdressebeskyttelse> request) {
        super(request);
    }

    @Override
    protected void validate(PdlAdressebeskyttelse adressebeskyttelse) {

        if (STRENGT_FORTROLIG_UTLAND.equals(adressebeskyttelse.getGradering()) &&
                FREG.equals(adressebeskyttelse.getMaster())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_UTLAND_MASTER_ERROR);
        }
    }

    @Override
    protected void handle(PdlAdressebeskyttelse adressebeskyttelse) {

        if (STRENGT_FORTROLIG_UTLAND.equals(adressebeskyttelse.getGradering())) {
            adressebeskyttelse.setMaster(PDL);
        }
    }

    @Override
    protected void enforceIntegrity(List<PdlAdressebeskyttelse> type) {

    }
}
