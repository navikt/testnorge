package no.nav.registre.sdForvalter.domain;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

import no.nav.registre.sdForvalter.database.model.EregModel;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class EregListe extends FasteDataListe<Ereg> {

    public EregListe(Ereg... eregs){
        super(Arrays.asList(eregs));
    }

    public EregListe(Iterable<EregModel> iterable) {
        super(iterable);
    }

    public EregListe(List<Ereg> liste) {
        super(liste);
    }
}
