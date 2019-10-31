package no.nav.registre.aareg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import no.nav.registre.aareg.domain.RsArbeidsforhold;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdentMedData {

    private String id;
    private List<RsArbeidsforhold> data;
}
