package no.nav.registre.syntrest.domain.amelding;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Avvik {

    @JsonAlias({"ID", "id"})
    private String id;

    @JsonAlias({"DETALJER", "detaljer"})
    private String detaljer;

    @JsonAlias({"NAVN", "navn"})
    private String navn;

    @JsonAlias({"ALVORLIGHETSGRAD", "alvorlighetsgrad"})
    private String alvorlighetsgrad;
}
