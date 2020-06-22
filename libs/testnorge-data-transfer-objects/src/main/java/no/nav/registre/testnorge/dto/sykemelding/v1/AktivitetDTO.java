package no.nav.registre.testnorge.dto.sykemelding.v1;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class AktivitetDTO {
    private final Integer grad;
    private final Boolean reisetilskudd;
    private final Integer behandlingsdager;
    private final Aktivitet aktivitet;
}
