package no.nav.testnav.libs.dto.organisasjonfastedataservice.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Set;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class OrganisasjonDTO {
    @JsonProperty(required = true)
    String orgnummer;
    @JsonProperty(required = true)
    String enhetstype;
    @JsonProperty(required = true)
    String navn;
    String redigertNavn;
    String epost;
    String internetAdresse;
    String naeringskode;
    String overenhet;
    AdresseDTO forretningsAdresse;
    AdresseDTO postadresse;
    String opprinnelse;
    Set<String> tags;
}