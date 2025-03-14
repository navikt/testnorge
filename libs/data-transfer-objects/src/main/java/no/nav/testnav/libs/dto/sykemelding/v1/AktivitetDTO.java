package no.nav.testnav.libs.dto.sykemelding.v1;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AktivitetDTO {

    private Integer grad;
    private Boolean reisetilskudd;
    private Integer behandlingsdager;
    private Aktivitet aktivitet;
}
