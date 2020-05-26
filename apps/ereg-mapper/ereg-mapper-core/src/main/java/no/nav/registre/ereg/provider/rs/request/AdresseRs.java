package no.nav.registre.ereg.provider.rs.request;

import lombok.*;

import javax.validation.constraints.NotNull;
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
