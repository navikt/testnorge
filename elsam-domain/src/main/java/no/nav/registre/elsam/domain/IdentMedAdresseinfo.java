package no.nav.registre.elsam.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class IdentMedAdresseinfo {

    private String ident;

    @JsonProperty("kodeNAVenhet")
    private String navKontorIdNummer;

    @JsonProperty("kodeNAVenhetBeskr")
    private String navKontorNavn;

    private String gateadresse;
    private String postnummer;
    private String by;
    private String land;
}
