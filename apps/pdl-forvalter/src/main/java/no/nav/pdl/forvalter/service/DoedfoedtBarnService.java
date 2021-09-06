package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DoedfoedtBarnDTO;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;

@Service
public class DoedfoedtBarnService extends PdlArtifactService<DoedfoedtBarnDTO> {

    private static final String INVALID_DATO_MISSING = "DødfødtBarn: dato må oppgis";

    @Override
    public void validate(DoedfoedtBarnDTO type) {

        if (isNull(type.getDato())) {

            throw new InvalidRequestException(INVALID_DATO_MISSING);
        }
    }

    @Override
    protected void handle(DoedfoedtBarnDTO type) {
        // Ingen håndtering
    }

    @Override
    protected void enforceIntegrity(List<DoedfoedtBarnDTO> type) {

        // Ingen referansevalidering
    }
}
