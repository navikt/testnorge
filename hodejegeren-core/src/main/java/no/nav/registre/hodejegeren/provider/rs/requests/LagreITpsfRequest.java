package no.nav.registre.hodejegeren.provider.rs.requests;

import lombok.Getter;

import java.util.List;

import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;

@Getter
public class LagreITpsfRequest {

    private Long avspillergruppeId;
    private List<RsMeldingstype> skdMeldinger;
}
