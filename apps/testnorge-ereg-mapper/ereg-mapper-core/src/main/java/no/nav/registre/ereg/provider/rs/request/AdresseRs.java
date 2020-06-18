package no.nav.registre.ereg.provider.rs.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class AdresseRs {

    private List<String> adresser;
    private String postnr;
    private String kommunenr;
    private String landkode;
    private String poststed;
}
