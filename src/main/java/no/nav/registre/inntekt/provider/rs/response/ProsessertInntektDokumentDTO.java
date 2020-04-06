package no.nav.registre.inntekt.provider.rs.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import no.nav.registre.inntekt.domain.dokmot.ProsessertInntektDokument;

@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProsessertInntektDokumentDTO {
    @JsonProperty
    private final String jornalpostId;
    @JsonProperty
    private final String xml;

    public ProsessertInntektDokumentDTO(ProsessertInntektDokument dokument, boolean includeXml) {
        this.jornalpostId = dokument.getJornalpostId();
        this.xml = includeXml ? dokument.getXml() : null;
    }
}
