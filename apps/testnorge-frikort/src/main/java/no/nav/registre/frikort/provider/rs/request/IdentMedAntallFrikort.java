package no.nav.registre.frikort.provider.rs.request;

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
public class IdentMedAntallFrikort {

    private String ident;
    private Integer antallFrikort;
}
