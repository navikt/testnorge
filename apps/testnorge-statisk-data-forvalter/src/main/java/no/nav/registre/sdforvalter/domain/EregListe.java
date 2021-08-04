package no.nav.registre.sdforvalter.domain;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.testnav.libs.dto.statiskedataforvalter.v1.OrganisasjonListeDTO;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class EregListe extends FasteDataListe<Ereg> {

    public EregListe(Ereg... eregs) {
        super(Arrays.asList(eregs));
    }

    public EregListe(List<Ereg> liste) {
        super(liste);
    }


    public EregListe(OrganisasjonListeDTO listeDTO) {
        this(listeDTO.getListe().stream().map(Ereg::new).collect(Collectors.toList()));
    }

    public OrganisasjonListeDTO toDTO() {
        return OrganisasjonListeDTO
                .builder()
                .liste(this.getListe().stream().map(Ereg::toDTO).collect(Collectors.toList()))
                .build();
    }

}
