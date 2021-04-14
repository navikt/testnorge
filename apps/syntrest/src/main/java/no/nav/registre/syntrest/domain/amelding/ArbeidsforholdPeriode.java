package no.nav.registre.syntrest.domain.amelding;

import java.time.LocalDate;
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
public class ArbeidsforholdPeriode {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate startdato;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate sluttdato;
}

