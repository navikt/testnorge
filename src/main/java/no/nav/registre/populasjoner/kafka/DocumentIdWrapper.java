package no.nav.registre.populasjoner.kafka;

import lombok.Value;

import no.nav.registre.populasjoner.kafka.domain.PdlDokument;

@Value
class DocumentIdWrapper {

    String identifator;
    PdlDokument dokument;

    boolean isTombstone() {
        return dokument == null;
    }
}
