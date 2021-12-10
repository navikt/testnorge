package no.nav.pdl.forvalter.service;

import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;

import java.util.List;

import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

public interface Applicable<T> {

    default ? applicable(List<? extends DbVersjonDTO> artifact) {

        if (artifact.stream().anyMatch(type -> isTrue(type.getIsNew()))) {
            artifact.stream()
                    .filter(type -> isNotTrue(type.getIsNew()))
                    .forEach(type -> type.setGjeldende(false));
        }
    }
}
