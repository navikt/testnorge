package no.nav.testnav.libs.dto.arena.testnorge.vedtak.forvalter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Adresse {

    private String adresseLinje1;
    private String adresseLinje2;
    private String adresseLinje3;
    private String fodselsnr;
    private String landkode;
    private String navn;
    private String orgnr;
    private String postnr;
}
