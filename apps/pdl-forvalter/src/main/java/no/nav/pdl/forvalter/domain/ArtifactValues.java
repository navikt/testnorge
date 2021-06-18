package no.nav.pdl.forvalter.domain;

import lombok.Value;

import no.nav.pdl.forvalter.utils.PdlTestDataUrls;

@Value
public class ArtifactValues {
    PdlTestDataUrls.PdlArtifact artifact;
    String ident;
    PdlDbVersjon body;
}
