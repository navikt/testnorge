package no.nav.pdl.forvalter.service;

import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;

@FunctionalInterface
public interface Validation<T extends DbVersjonDTO> {

    void validate(T artifact);
}
