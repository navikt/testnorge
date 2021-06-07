package no.nav.registre.testnorge.libs.dto.dokarkiv.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.Arrays;
import java.util.List;

@Value
@Builder
@AllArgsConstructor
public class Dokument {
    @JsonProperty
    private String tittel;
    @JsonProperty
    private String brevkode;
    @JsonProperty
    private String dokumentkategori;
    @JsonProperty
    private List<Dokumentvariant> dokumentvarianter;

    public Dokument(RsJoarkMetadata metadata, Dokumentvariant... dokumentvarianter) {
        this.tittel = metadata.getTittel();
        this.brevkode = metadata.getBrevkode();
        this.dokumentkategori = metadata.getBrevkategori();
        this.dokumentvarianter = Arrays.asList(dokumentvarianter);
    }
}
