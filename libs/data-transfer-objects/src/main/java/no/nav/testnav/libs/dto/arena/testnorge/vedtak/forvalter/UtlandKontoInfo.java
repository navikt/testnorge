package no.nav.testnav.libs.dto.arena.testnorge.vedtak.forvalter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
