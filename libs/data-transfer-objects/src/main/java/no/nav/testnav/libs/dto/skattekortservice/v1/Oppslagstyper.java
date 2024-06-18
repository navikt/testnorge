package no.nav.testnav.libs.dto.skattekortservice.v1;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Oppslagstyper {

    RESULTATSTATUS(Resultatstatus.class),
    TABELLTYPE(Tabelltype.class),
    TILLEGGSOPPLYSNING(Tilleggsopplysning.class),
    TREKKODE(Trekkode.class);

    private final Class value;
}
