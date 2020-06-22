package no.nav.registre.inntekt.consumer.rs.dokmot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProsessertInntektDokumentDTO {
    @JsonProperty
    private final String journalpostId;
    @JsonProperty
    private final String dokumentInfoId;
    @JsonProperty
    private final String xml;

    public ProsessertInntektDokumentDTO(ProsessertInntektDokument dokument, boolean includeXml) {
        this.journalpostId = dokument.getJournalpostId();
        this.dokumentInfoId = dokument.getDokumentInfoId();
        this.xml = includeXml ? dokument.getXml() : null;
    }
}
