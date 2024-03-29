package no.nav.pdl.forvalter.service;

import no.nav.testnav.libs.data.pdlforvalter.v1.DbVersjonDTO;

@FunctionalInterface
public interface BiValidation<T extends DbVersjonDTO, R> {

    void validate(T artifact, R person);
}
