package no.nav.pdl.forvalter.dto;

import lombok.Builder;
import lombok.Data;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact;

@Data
@Builder
public class ArtifactValue {

    PdlArtifact artifact;
    String ident;
    DbVersjonDTO body;
}