package no.nav.registre.sdforvalter.domain;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

import no.nav.registre.sdforvalter.database.model.AaregModel;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class AaregListe extends FasteDataListe<Aareg> {
    public AaregListe(Iterable<AaregModel> iterable) {
        super(iterable);
    }
    public AaregListe(List<Aareg> liste) {
        super(liste);
    }
}