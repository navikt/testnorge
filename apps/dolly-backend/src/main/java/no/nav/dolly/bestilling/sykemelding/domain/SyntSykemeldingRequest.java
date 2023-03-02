package no.nav.dolly.bestilling.sykemelding.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SyntSykemeldingRequest {

    private String arbeidsforholdId;
    private String ident;
    private String orgnummer;
    private LocalDate startDato;
}
