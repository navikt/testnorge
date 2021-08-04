package no.nav.testnav.libs.dto.statiskedataforvalter.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.Pattern;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class OrganisasjonDTO {

    @Pattern(regexp = "^(8|9)\\d{8}$")
    @JsonProperty(required = true)
    String orgnr;
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
    String juridiskEnhet;
    @JsonProperty
    AdresseDTO forretningsAdresse;
    @JsonProperty
    AdresseDTO postadresse;
    @JsonProperty
    boolean kanHaArbeidsforhold;
    @JsonProperty
    String gruppe;
    @JsonProperty
    String opprinnelse;
}
