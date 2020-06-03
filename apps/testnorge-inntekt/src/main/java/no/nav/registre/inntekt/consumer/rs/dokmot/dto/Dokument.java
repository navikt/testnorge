package no.nav.registre.inntekt.consumer.rs.dokmot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.Arrays;
import java.util.List;

import no.nav.registre.inntekt.domain.dokmot.RsJoarkMetadata;

@Value
@Builder
@AllArgsConstructor
public class Dokument {
    @JsonProperty("tittel")
    private String titel;
    @JsonProperty
    private String brevkode;
    @JsonProperty
    private String dokumentkategori;
    @JsonProperty
    private List<Dokumentvariant> dokumentvarianter;

    public Dokument(RsJoarkMetadata metadata, Dokumentvariant... dokumentvariants) {
        this.titel = metadata.getTittel();
        this.brevkode = metadata.getBrevkode();
        this.dokumentkategori = metadata.getBrevkategori();
        this.dokumentvarianter = Arrays.asList(dokumentvariants);
    }
}
