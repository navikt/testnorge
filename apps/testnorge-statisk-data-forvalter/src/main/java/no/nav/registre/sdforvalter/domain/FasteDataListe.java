package no.nav.registre.sdforvalter.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import no.nav.registre.sdforvalter.database.model.FasteDataModel;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class FasteDataListe<T extends FasteData> {

    FasteDataListe() {
        this.liste = new ArrayList<>();
    }

    @JsonProperty
    private final List<T> liste;

     <I extends FasteDataModel<T>> FasteDataListe(Iterable<I> iterable) {
        liste = new ArrayList<>();
        iterable.forEach(item -> liste.add(item.toDomain()));
    }

    @JsonIgnore
    public int size(){
         return liste.size();
    }

    /**
     * Få alle elementer som ikke er i den andre listen.
     *
     * @param other den andre listen.
     * @return liste med elementer som ikke er i den andre listen.
     */
    public <I extends FasteDataListe<T>> List<T> itemsNotIn(I other) {
        if (other == null) {
            return liste;
        }
        return liste
                .stream()
                .filter(item -> !other.getListe().contains(item))
                .toList();
    }

    public Stream<T> stream() {
        return liste.stream();
    }

    public List<T> filterOnGruppe(String gruppe) {
        if (Strings.isBlank(gruppe)) {
            return liste;
        }
        return liste
                .stream()
                .filter(item -> Strings.isNotBlank(item.getGruppe()) && item.getGruppe().equals(gruppe))
                .toList();
    }
}