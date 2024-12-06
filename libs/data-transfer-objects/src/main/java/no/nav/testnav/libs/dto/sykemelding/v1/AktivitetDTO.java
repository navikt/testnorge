package no.nav.testnav.libs.dto.sykemelding.v1;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AktivitetDTO {
    private final Integer grad;
    private final Boolean reisetilskudd;
    private final Integer behandlingsdager;
    private final Aktivitet aktivitet;
}
