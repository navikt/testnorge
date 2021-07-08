package no.nav.pdl.forvalter.service;

import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.DbVersjonDTO;

import java.util.List;

import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.logging.log4j.util.Strings.isBlank;

public abstract class PdlArtifactService<T extends DbVersjonDTO> {

    protected List<T> convert(List<T> request) {

        for (var type : request) {

            if (isTrue(type.getIsNew())) {
                validate(type);

                handle(type);
                if (isBlank(type.getKilde())) {
                    type.setKilde("Dolly");
                }
            }
        }
        enforceIntegrity(request);
        return request;
    }

    protected abstract void validate(T type);

    protected abstract void handle(T type);

    protected abstract void enforceIntegrity(List<T> type);
}
