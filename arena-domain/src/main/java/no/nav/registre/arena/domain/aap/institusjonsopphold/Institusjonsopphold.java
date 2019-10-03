package no.nav.registre.arena.domain.aap.institusjonsopphold;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Institusjonsopphold {
    private InstKoder kode;
    private InstOvKoder overordnet;
    private String verdi;
}
