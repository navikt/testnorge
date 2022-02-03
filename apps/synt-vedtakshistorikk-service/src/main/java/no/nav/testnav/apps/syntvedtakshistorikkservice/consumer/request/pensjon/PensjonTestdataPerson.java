package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.pensjon;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PensjonTestdataPerson {

    private String bostedsland;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate dodsDato;

    private String fnr;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate fodselsDato;

    private List<String> miljoer;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate utvandringsDato;
}
