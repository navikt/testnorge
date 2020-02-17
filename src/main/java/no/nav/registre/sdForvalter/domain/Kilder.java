package no.nav.registre.sdForvalter.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import no.nav.registre.sdForvalter.database.model.KildeModel;


@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Kilder {

    @JsonProperty("kilder")
    private final Set<Kilde> set;

    public Kilder(Iterable<KildeModel> iterable) {
        set = new HashSet<>();
        iterable.forEach(model -> set.add(new Kilde(model)));
    }

    public Kilder(Collection<KildeModel> list) {
        set = list.stream().map(Kilde::new).collect(Collectors.toSet());
    }

    public boolean isNavnInKilder(String navn) {
        return getKildeByNavn(navn).isPresent();
    }

    public Optional<Kilde> getKildeByNavn(String navn) {
        return set.stream().filter(kilde -> kilde.getNavn().equals(navn)).findFirst();
    }

    public Stream<Kilde> stream() {
        return set.stream();
    }

    public boolean contains(Kilde kilde) {
        return set.contains(kilde);
    }


    public Kilder merge(Kilder kilder) {
        return new Kilder(Stream
                .concat(set.stream(), kilder.getSet().stream())
                .collect(Collectors.toSet())
        );
    }
}