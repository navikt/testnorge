package no.nav.registre.syntrest.domain.amelding;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class Inntekt {

    @JsonAlias({ "STARTDATO_OPPTJENINGSPERIODE", "startdatoOpptjeningsperiode" })
    private LocalDate startdatoOpptjeningsperiode;

    @JsonAlias({ "SLUTTDATO_OPPTJENINGSPERIODE", "sluttdatoOpptjeningsperiode" })
    private LocalDate sluttdatoOpptjeningsperiode;

    @JsonAlias({ "LOENNSINNTEKT", "loennsinntekt" })
    private Loennsinntekt loennsinntekt;
}
