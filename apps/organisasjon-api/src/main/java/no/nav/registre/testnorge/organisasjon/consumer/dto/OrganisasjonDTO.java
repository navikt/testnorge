package no.nav.registre.testnorge.organisasjon.consumer.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganisasjonDTO {
    @JsonProperty(required = true)
    private final String organisasjonsnummer;
    @JsonProperty(required = true)
    @JsonAlias({"virksomhetDetaljer", "juridiskEnhetDetaljer", "organisasjonsleddDetaljer"})
    private final DetaljerDTO detaljer;
    @JsonProperty(required = true)
    private final NavnDTO navn;
    @JsonProperty(required = true)
    private final String type;
    @JsonProperty
    @JsonAlias({"inngaarIJuridiskEnheter"})
    private final List<OrganisasjonDTO> parents = new ArrayList<>();
    @JsonProperty
    private final OrganisasjonDetaljerDTO organisasjonDetaljer;
    @JsonProperty
    @JsonAlias({"driverVirksomheter"})
    private final List<OrganisasjonDTO> children = new ArrayList<>();
}