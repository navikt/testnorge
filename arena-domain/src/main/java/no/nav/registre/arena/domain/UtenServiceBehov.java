package no.nav.registre.arena.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UtenServiceBehov {
    @JsonProperty("stansDato")
    private Date stansDato;
}
