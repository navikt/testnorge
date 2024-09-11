package no.nav.testnav.libs.dto.organisasjon.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganisasjonDTO {

    private final String error;
    private final String orgnummer;
    private final String enhetType;
    private final String navn;
    private final String juridiskEnhet;
    private final AdresseDTO postadresse;
    private final AdresseDTO forretningsadresser;
    private final String redigertnavn;
    private final List<String> driverVirksomheter;
}

