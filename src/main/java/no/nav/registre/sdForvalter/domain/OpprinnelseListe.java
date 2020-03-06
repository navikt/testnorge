package no.nav.registre.sdForvalter.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.sdForvalter.database.model.OpprinnelseModel;


@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpprinnelseListe {

    @JsonProperty("liste")
    private final Set<Opprinnelse> set;

    public OpprinnelseListe(Iterable<OpprinnelseModel> iterable) {
        set = new HashSet<>();
        iterable.forEach(model -> set.add(new Opprinnelse(model)));
    }

    public OpprinnelseListe(Collection<OpprinnelseModel> list) {
        set = list.stream().map(Opprinnelse::new).collect(Collectors.toSet());
    }
}