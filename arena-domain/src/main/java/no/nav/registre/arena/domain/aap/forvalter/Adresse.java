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
