package no.nav.registre.inntekt.domain.dokmot;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DokumentInfo {
    @JsonProperty
    private String brevkode;
    @JsonProperty
    private String dokumentInfoId;
    @JsonProperty
    private String tittel;
}
