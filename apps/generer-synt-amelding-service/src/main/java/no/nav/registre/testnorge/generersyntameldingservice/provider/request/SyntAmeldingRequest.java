package no.nav.registre.testnorge.generersyntameldingservice.provider.request;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.testnorge.generersyntameldingservice.domain.ArbeidsforholdType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SyntAmeldingRequest {

    private ArbeidsforholdType arbeidsforholdType;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate startdato;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate sluttdato;
}
