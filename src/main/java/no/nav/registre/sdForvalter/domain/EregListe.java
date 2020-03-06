package no.nav.registre.sdForvalter.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import no.nav.registre.sdForvalter.database.model.EregModel;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class EregListe {

    @JsonProperty
    private final List<Ereg> liste;

    public EregListe(Iterable<EregModel> itrable) {
        liste = new ArrayList<>();
        itrable.forEach(model -> liste.add(new Ereg(model)));
    }

    public EregListe(Collection<EregModel> collection) {
        liste = new ArrayList<>();
        collection.forEach(model -> liste.add(new Ereg(model)));
    }
}
