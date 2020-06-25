package no.nav.registre.populasjoner.kafka;

import lombok.Value;

import no.nav.registre.populasjoner.kafka.domain.PdlDokument;

@Value
public class DocumentIdWrapper {

    String identifikator;
    PdlDokument dokument;

    boolean isTombstone() {
        return dokument == null;
    }
}
