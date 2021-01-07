package no.nav.registre.syntrest.consumer.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.syntrest.domain.amelding.ArbeidsforholdAmelding;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AmeldingHistorikkResponse {

    @JsonAlias({ "MELDINGER", "meldinger" })
    private List<ArbeidsforholdAmelding> meldinger;

    @JsonAlias({ "HISTORIKK", "historikk", "change_history" })
    private String historikk;
}
