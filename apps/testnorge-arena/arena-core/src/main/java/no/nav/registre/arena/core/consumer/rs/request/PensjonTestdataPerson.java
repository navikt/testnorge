package no.nav.registre.arena.core.consumer.rs.request;

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
    private LocalDate dodsDato;
    private String fnr;
    private LocalDate fodselsDato;
    private List<String> miljoer;
    private LocalDate utvandringsDato;
}
