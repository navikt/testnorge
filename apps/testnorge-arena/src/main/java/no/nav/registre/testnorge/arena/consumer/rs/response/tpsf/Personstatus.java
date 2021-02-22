package no.nav.registre.testnorge.arena.consumer.rs.response.tpsf;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Personstatus {

    private LocalDate datoTom;

    private String kodePersonstatus;

    private String system;

    private String saksbehandler;

    private LocalDate datoFom;

}
