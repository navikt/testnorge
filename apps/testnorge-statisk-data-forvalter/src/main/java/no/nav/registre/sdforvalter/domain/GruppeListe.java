package no.nav.registre.sdforvalter.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import no.nav.registre.sdforvalter.database.model.GruppeModel;

import java.util.ArrayList;
import java.util.List;

@Getter
@Schema(description = "En liste definerte grupper.")
public class GruppeListe {

    @JsonProperty
    private final List<Gruppe> liste = new ArrayList<>();

    public GruppeListe(Iterable<GruppeModel> iterable) {
        iterable.forEach(model -> liste.add(new Gruppe(model)));
    }

}