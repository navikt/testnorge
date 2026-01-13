package no.nav.testnav.libs.dto.organisasjon.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static java.util.Objects.nonNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganisasjonDTO {

    public static final String NOT_FOUND = "Ikke funnet";

    private String error;
    private String orgnummer;
    private String enhetType;
    private String navn;
    private String juridiskEnhet;
    private AdresseDTO postadresse;
    private AdresseDTO forretningsadresser;
    private String redigertnavn;
    private List<String> driverVirksomheter;

    @JsonIgnore
    public boolean isFunnet() {

        return nonNull(juridiskEnhet) && !NOT_FOUND.equals(juridiskEnhet);
    }
}
