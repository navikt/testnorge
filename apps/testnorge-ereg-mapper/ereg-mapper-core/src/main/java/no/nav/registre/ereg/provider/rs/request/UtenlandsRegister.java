package no.nav.registre.ereg.provider.rs.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
