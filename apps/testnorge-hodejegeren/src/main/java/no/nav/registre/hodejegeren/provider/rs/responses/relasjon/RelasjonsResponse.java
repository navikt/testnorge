package no.nav.registre.hodejegeren.provider.rs.responses.relasjon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelasjonsResponse {

    private String fnr;
    private List<Relasjon> relasjoner;
}
