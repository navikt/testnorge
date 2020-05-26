package no.nav.registre.ereg.provider.rs.request;

import lombok.*;
import org.apache.commons.lang3.builder.HashCodeExclude;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
public class UtenlandsRegister {

    private List<String> navn = new ArrayList<>(3);
    private String registerNr;
    private AdresseRs adresse;

}
