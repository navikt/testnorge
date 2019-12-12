package no.nav.registre.arena.core.consumer.rs.responses.rettighet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Konto {

    private String kontonr;
    private List<UtlandKontoInfo> utland;
}
