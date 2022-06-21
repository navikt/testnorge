package no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.dagpenger;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
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
