package no.nav.registre.aareg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.aareg.domain.Arbeidsforhold;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter

public class IdentMedData {
    private String id;
    private List<Arbeidsforhold> data;
}
