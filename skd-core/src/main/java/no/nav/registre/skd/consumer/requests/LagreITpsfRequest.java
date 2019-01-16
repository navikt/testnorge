package no.nav.registre.skd.consumer.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

import no.nav.registre.skd.skdmelding.RsMeldingstype;

@Getter
@AllArgsConstructor
public class LagreITpsfRequest {

    private Long avspillergruppeId;
    private List<RsMeldingstype> skdMeldinger;
}
