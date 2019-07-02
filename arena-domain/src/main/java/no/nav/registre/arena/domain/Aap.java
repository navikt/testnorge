package no.nav.registre.arena.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import no.nav.registre.arena.domain.utils.CustomDateDeserializer;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Aap {
    @JsonProperty
    @JsonDeserialize(using = CustomDateDeserializer.class, as = Date.class)
    private Date fraDato;
    @JsonProperty
    @JsonDeserialize(using = CustomDateDeserializer.class, as = Date.class)
    private Date tilDato;
}
