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

import no.nav.registre.sdForvalter.database.model.KildeSystemModel;


@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class KildeSystemListe {

    @JsonProperty("kildeSystemListe")
    private final Set<KildeSystem> set;

    public KildeSystemListe(Iterable<KildeSystemModel> iterable) {
        set = new HashSet<>();
        iterable.forEach(model -> set.add(new KildeSystem(model)));
    }

    public KildeSystemListe(Collection<KildeSystemModel> list) {
        set = list.stream().map(KildeSystem::new).collect(Collectors.toSet());
    }
}