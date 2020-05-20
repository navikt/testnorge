package no.nav.registre.ereg.provider.rs.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
public class Naeringskode {

    private String kode;
    private String gyldighetsdato;
    private Boolean hjelpeEnhet;

}
