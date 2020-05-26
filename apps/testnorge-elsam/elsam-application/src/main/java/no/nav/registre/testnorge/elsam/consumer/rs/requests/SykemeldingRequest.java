package no.nav.registre.testnorge.elsam.consumer.rs.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

import no.nav.registre.elsam.domain.Lege;
import no.nav.registre.elsam.domain.Pasient;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SykemeldingRequest {

    @JsonProperty("start_dato")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate startDato;
    private Pasient pasient;
    private Lege lege;
}
