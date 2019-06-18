package no.nav.registre.arena.domain;

import lombok.*;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Aap115 {
    @JsonProperty("fraDato")
    private Date fraDato;
}
