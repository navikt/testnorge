package no.nav.registre.inntekt.domain.dokmot;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
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
}
