package no.nav.registre.hodejegeren.provider.rs.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;

@Getter
@AllArgsConstructor
public class LagreITpsfRequest {

    private Long avspillergruppeId;
    private List<RsMeldingstype> skdMeldinger;
}
