package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DoedsfallDTO;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;

@Service
public class DoedsfallService extends PdlArtifactService<DoedsfallDTO> {

    private static final String INVALID_DATO_MISSING = "Dødsfall: dødsdato må oppgis";

    @Override
    protected void validate(DoedsfallDTO type) {

        if (isNull(type.getDoedsdato())) {

            throw new InvalidRequestException(INVALID_DATO_MISSING);
        }
    }

    @Override
    protected void handle(DoedsfallDTO type) {
        // Ingen håndtering
    }

    @Override
    protected void enforceIntegrity(List<DoedsfallDTO> type) {

        // Ingen referansevalidering
    }
}
