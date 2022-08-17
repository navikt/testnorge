package no.nav.registre.hodejegeren.provider.rs.responses;

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
public class NavEnhetResponse {

    private String ident;
    private String navEnhet;
    private String navEnhetBeskrivelse;
}
