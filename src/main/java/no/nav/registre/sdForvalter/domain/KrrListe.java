package no.nav.registre.sdForvalter.domain;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

import no.nav.registre.sdForvalter.database.model.KrrModel;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class KrrListe extends FasteDataListe<Krr> {
    public KrrListe(Iterable<KrrModel> iterable) {
        super(iterable);
    }

    public KrrListe(List<Krr> liste) {
        super(liste);
    }
}
