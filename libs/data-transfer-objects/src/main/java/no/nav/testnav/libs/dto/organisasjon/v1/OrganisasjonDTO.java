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
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganisasjonDTO {

    public static final String NOT_FOUND = "Ikke funnet";

    private final String error;
    private final String orgnummer;
    private final String enhetType;
    private final String navn;
    private final String juridiskEnhet;
    private final AdresseDTO postadresse;
    private final AdresseDTO forretningsadresser;
    private final String redigertnavn;
    private final List<String> driverVirksomheter;

    @JsonIgnore
    public boolean isFunnet() {

        return nonNull(juridiskEnhet) && !NOT_FOUND.equals(juridiskEnhet);
    }
}
