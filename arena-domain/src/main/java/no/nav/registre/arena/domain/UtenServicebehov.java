package no.nav.registre.arena.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UtenServicebehov {
    @JsonProperty
    private Date stansDato;
}
