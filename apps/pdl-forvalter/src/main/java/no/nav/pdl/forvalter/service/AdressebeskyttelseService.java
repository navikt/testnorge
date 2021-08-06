package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import org.springframework.stereotype.Service;

import java.util.List;

import static no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO.AdresseBeskyttelse.STRENGT_FORTROLIG_UTLAND;

@Service
public class AdressebeskyttelseService extends PdlArtifactService<AdressebeskyttelseDTO> {

    private static final String VALIDATION_UTLAND_MASTER_ERROR = "Gradering STRENGT_FORTROLIG_UTLAND kan kun settes hvis master er PDL";

    @Override
    protected void validate(AdressebeskyttelseDTO adressebeskyttelse) {

        if (STRENGT_FORTROLIG_UTLAND.equals(adressebeskyttelse.getGradering()) &&
                DbVersjonDTO.Master.FREG == adressebeskyttelse.getMaster()) {
            throw new InvalidRequestException(VALIDATION_UTLAND_MASTER_ERROR);
        }
    }

    @Override
    protected void handle(AdressebeskyttelseDTO adressebeskyttelse) {

        if (STRENGT_FORTROLIG_UTLAND.equals(adressebeskyttelse.getGradering())) {
            adressebeskyttelse.setMaster(DbVersjonDTO.Master.PDL);
        }
    }

    @Override
    protected void enforceIntegrity(List<AdressebeskyttelseDTO> type) {

        // Ingen integritet å ivareta
    }
}
