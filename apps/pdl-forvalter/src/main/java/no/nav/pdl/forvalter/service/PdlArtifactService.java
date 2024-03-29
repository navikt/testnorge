package no.nav.pdl.forvalter.service;

import no.nav.testnav.libs.data.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.DbVersjonDTO.Master;

import java.util.List;

import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

public abstract class PdlArtifactService<T extends DbVersjonDTO> implements Validation<T> {

    protected List<T> convert(List<T> request) {

        for (var type : request) {

            if (isTrue(type.getIsNew())) {

                type.setKilde(getKilde(type));
                type.setMaster(nonNull(type.getMaster()) ? type.getMaster() : Master.FREG);
                handle(type);
            }
        }
        enforceIntegrity(request);
        return request;
    }

    protected abstract void handle(T type);

    protected abstract void enforceIntegrity(List<T> type);
}
