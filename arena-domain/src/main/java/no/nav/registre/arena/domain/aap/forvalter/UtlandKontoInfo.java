package no.nav.registre.arena.domain.aap.forvalter;

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
public class UtlandKontoInfo {

    private String adresseLinje1;
    private String adresseLinje2;
    private String adresseLinje3;
    private String bankkode;
    private String banknavn;
    private String landkode;
}
