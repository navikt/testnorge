package no.nav.registre.arena.domain;

import lombok.*;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Aap {
    @JsonProperty("fraDato")
    private Date fraDato;
    @JsonProperty("tilDato")
    private Date tilDato;
}
