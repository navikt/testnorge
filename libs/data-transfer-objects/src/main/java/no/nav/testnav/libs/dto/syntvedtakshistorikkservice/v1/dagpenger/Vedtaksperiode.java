package no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.dagpenger;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
public class Vedtaksperiode {
    @JsonProperty
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fom;

    @JsonProperty
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tom;
}
