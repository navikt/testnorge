package no.nav.registre.arena.core.consumer.rs.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AktoerInnhold {

    private String ident;
    private String identgruppe;
    private boolean gjeldende;
}
