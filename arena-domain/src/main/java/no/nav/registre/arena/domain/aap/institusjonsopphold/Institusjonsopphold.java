package no.nav.registre.arena.domain.aap.institusjonsopphold;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class Institusjonsopphold {
    private String kode;
    private String verdi;
}
