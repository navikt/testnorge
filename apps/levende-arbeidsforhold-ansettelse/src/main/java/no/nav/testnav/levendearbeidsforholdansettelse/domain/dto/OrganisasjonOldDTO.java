package no.nav.testnav.levendearbeidsforholdansettelse.domain.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class OrganisasjonOldDTO {
    @JsonProperty(required = true)
    String organisasjonsnummer;
    @JsonProperty(required = true)
    @JsonAlias({"virksomhetDetaljer", "juridiskEnhetDetaljer", "organisasjonsleddDetaljer"})
    DetaljerDTO detaljer;
    @JsonProperty(required = true)
    NavnDTO navn;
    @JsonProperty(required = true)
    String type;
    @JsonProperty
    @JsonAlias({"inngaarIJuridiskEnheter"})
    List<OrganisasjonOldDTO> parents = new ArrayList<>();
    @JsonProperty
    OrganisasjonDetaljerDTO organisasjonDetaljer;
    @JsonProperty
    @JsonAlias({"driverVirksomheter"})
    List<OrganisasjonOldDTO> children = new ArrayList<>();
}