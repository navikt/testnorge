package no.nav.registre.testnorge.organisasjonservice.consumer.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
@ToString
public class OrganisasjonDTO {
    @JsonProperty
    String organisasjonsnummer;
    @JsonProperty
    @JsonAlias({"virksomhetDetaljer", "juridiskEnhetDetaljer", "organisasjonsleddDetaljer"})
    DetaljerDTO detaljer;
    @JsonProperty
    NavnDTO navn;
    @JsonProperty
    String type;
    @JsonProperty
    @JsonAlias({"inngaarIJuridiskEnheter"})
    List<OrganisasjonDTO> parents = new ArrayList<>();
    @JsonProperty
    OrganisasjonDetaljerDTO organisasjonDetaljer;
    @JsonProperty
    @JsonAlias({"driverVirksomheter"})
    List<OrganisasjonDTO> children = new ArrayList<>();
}