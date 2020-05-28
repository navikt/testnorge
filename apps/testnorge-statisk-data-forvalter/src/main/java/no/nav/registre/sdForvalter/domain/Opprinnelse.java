package no.nav.registre.sdForvalter.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.sdForvalter.database.model.OpprinnelseModel;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Opprinnelse {

    @JsonProperty
    private final String navn;

    public Opprinnelse(OpprinnelseModel model) {
        this(model.getNavn());
    }
}