package no.nav.dolly.bestilling.sykemelding.domain;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SyntSykemeldingRequest {

    private String arbeidsforholdId;
    private String ident;
    private String orgnummer;
    private LocalDate startDato;
}
