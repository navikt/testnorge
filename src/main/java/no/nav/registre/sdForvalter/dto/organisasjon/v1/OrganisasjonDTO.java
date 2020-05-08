package no.nav.registre.sdForvalter.dto.organisasjon.v1;

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
    private final String orgnr;
    @JsonProperty(required = true)
    private final String enhetstype;
    @JsonProperty
    private final String navn;
    @JsonProperty
    private final String epost;
    @JsonProperty
    private final String internetAdresse;
    @JsonProperty
    private final String naeringskode;
    @JsonProperty
    private final String juridiskEnhet;
    @JsonProperty
    private final AdresseDTO forretningsAdresse;
    @JsonProperty
    private final AdresseDTO postadresse;
    @JsonProperty
    private final boolean kanHaArbeidsforhold;
    @JsonProperty
    private final String gruppe;
    @JsonProperty
    private final String opprinnelse;
}
