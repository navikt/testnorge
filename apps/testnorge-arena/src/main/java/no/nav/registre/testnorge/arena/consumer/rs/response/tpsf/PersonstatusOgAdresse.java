package no.nav.registre.testnorge.arena.consumer.rs.response.tpsf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonstatusOgAdresse {

    private Personstatus personstatus;
    private String identType;
    private BostedAdresse bostedAdresse;
    private String fnr;
}
