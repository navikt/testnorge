package no.nav.registre.arena.domain.aap.konto;

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
