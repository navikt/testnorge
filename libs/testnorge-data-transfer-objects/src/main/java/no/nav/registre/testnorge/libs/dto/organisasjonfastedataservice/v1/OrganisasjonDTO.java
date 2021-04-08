package no.nav.registre.testnorge.libs.dto.organisasjonfastedataservice.v1;

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
    @JsonProperty
    String navn;
    @JsonProperty
    String redigertNavn;
    @JsonProperty
    String epost;
    @JsonProperty
    String internetAdresse;
    @JsonProperty
    String naeringskode;
    @JsonProperty
    String overenhet;
    @JsonProperty
    AdresseDTO forretningsAdresse;
    @JsonProperty
    AdresseDTO postadresse;
    @JsonProperty
    String opprinnelse;
    @JsonProperty
    Set<String> tags;
}