package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.domain.AdressebeskyttelseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static no.nav.pdl.forvalter.domain.AdresseDTO.Master.FREG;
import static no.nav.pdl.forvalter.domain.AdresseDTO.Master.PDL;
import static no.nav.pdl.forvalter.domain.AdressebeskyttelseDTO.AdresseBeskyttelse.STRENGT_FORTROLIG_UTLAND;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class AdressebeskyttelseService extends PdlArtifactService<AdressebeskyttelseDTO> {

    private static final String VALIDATION_UTLAND_MASTER_ERROR = "Gradering STRENGT_FORTROLIG_UTLAND kan kun settes hvis master er PDL";

    @Override
    protected void validate(AdressebeskyttelseDTO adressebeskyttelse) {

        if (STRENGT_FORTROLIG_UTLAND.equals(adressebeskyttelse.getGradering()) &&
                FREG.equals(adressebeskyttelse.getMaster())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_UTLAND_MASTER_ERROR);
        }
    }

    @Override
    protected void handle(AdressebeskyttelseDTO adressebeskyttelse) {

        if (STRENGT_FORTROLIG_UTLAND.equals(adressebeskyttelse.getGradering())) {
            adressebeskyttelse.setMaster(PDL);
        }
    }

    @Override
    protected void enforceIntegrity(List<AdressebeskyttelseDTO> type) {

        // Ingen integritet å ivareta
    }
}
