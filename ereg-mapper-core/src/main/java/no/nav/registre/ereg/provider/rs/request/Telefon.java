package no.nav.registre.ereg.provider.rs.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Telefon {

    private String mobil;
    private String fax;
    private String fast;

}
