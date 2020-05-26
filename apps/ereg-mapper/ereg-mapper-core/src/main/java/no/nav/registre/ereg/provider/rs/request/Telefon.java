package no.nav.registre.ereg.provider.rs.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
public class Telefon {

    private String mobil;
    private String fax;
    private String fast;

}
