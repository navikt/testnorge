package no.nav.registre.testnorge.organisasjonservice.consumer.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class OrganisasjonDTO {
    @JsonProperty
    private String organisasjonsnummer;
    @JsonProperty
    @JsonAlias({"virksomhetDetaljer", "juridiskEnhetDetaljer", "organisasjonsleddDetaljer"})
    private DetaljerDTO detaljer;
    @JsonProperty
    private NavnDTO navn;
    @JsonProperty
    private String type;
    @JsonProperty
    @JsonAlias({"inngaarIJuridiskEnheter"})
    @Builder.Default
    private List<OrganisasjonDTO> parents = new ArrayList<>();
    @JsonProperty
    private OrganisasjonDetaljerDTO organisasjonDetaljer;
    @JsonProperty
    @JsonAlias({"driverVirksomheter"})
    @Builder.Default
    private List<OrganisasjonDTO> children = new ArrayList<>();
}