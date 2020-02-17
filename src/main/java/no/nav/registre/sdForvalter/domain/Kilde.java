package no.nav.registre.sdForvalter.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.sdForvalter.database.model.KildeModel;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Kilde {

    @JsonProperty
    private final String navn;

    public Kilde(KildeModel model) {
        this(model != null ? model.getNavn() : null);
    }
}