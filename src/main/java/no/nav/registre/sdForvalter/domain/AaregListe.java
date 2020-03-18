package no.nav.registre.sdForvalter.domain;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

import no.nav.registre.sdForvalter.database.model.AaregModel;

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