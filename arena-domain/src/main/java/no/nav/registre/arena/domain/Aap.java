package no.nav.registre.arena.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Aap {
    @JsonProperty("fraDato")
    Date fraDato;
    @JsonProperty("tilDato")
    Date tilDato;
}
