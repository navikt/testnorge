package no.nav.registre.sdforvalter.domain;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class TpsIdentListe extends FasteDataListe<TpsIdent> {
    public TpsIdentListe(List<TpsIdent> liste) {
        super(liste);
    }
}