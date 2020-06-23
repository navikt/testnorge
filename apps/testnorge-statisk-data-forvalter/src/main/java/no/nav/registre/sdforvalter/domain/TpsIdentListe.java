package no.nav.registre.sdforvalter.domain;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

import no.nav.registre.sdforvalter.database.model.TpsIdentModel;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class TpsIdentListe extends FasteDataListe<TpsIdent> {
    public TpsIdentListe(Iterable<TpsIdentModel> iterable) {
        super(iterable);
    }

    public TpsIdentListe(List<TpsIdent> liste) {
        super(liste);
    }
}