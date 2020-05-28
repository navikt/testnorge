package no.nav.registre.sdForvalter.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.sdForvalter.database.model.GruppeModel;

@NoArgsConstructor(force = true)
@AllArgsConstructor
public class GruppeListe {

    @JsonProperty
    private final List<Gruppe> liste;

    public GruppeListe(Iterable<GruppeModel> itrable) {
        liste = new ArrayList<>();
        itrable.forEach(model -> liste.add(new Gruppe(model)));
    }
}