package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.amelding;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.dto.syntrest.v1.PermisjonDTO;

@Value
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class Permisjon extends Generated {
    String id;
    String beskrivelse;
    LocalDate startdato;
    LocalDate sluttdato;
    Float permisjonsprosent;
    List<Avvik> avvik;

    public Permisjon(no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.PermisjonDTO dto) {
        id = UUID.randomUUID().toString();
        beskrivelse = dto.getBeskrivelse();
        startdato = dto.getStartdato();
        sluttdato = dto.getSluttdato();
        permisjonsprosent = dto.getPermisjonsprosent();
        avvik = dto.getAvvik().stream().map(Avvik::new).collect(Collectors.toList());
    }

    public Permisjon(PermisjonDTO dto) {
        id = UUID.randomUUID().toString();
        beskrivelse = dto.getBeskrivelse();
        startdato = dto.getStartdato();
        sluttdato = dto.getSluttdato();
        permisjonsprosent = dto.getPermisjonsprosent() == null ? null : Float.parseFloat(dto.getPermisjonsprosent());
        avvik = dto.getAvvik() == null ? new ArrayList<>() : Collections.singletonList(new Avvik(dto.getAvvik()));
    }

    public PermisjonDTO toSynt() {
        return PermisjonDTO
                .builder()
                .beskrivelse(beskrivelse)
                .startdato(startdato)
                .sluttdato(sluttdato)
                .permisjonsprosent(permisjonsprosent == null ? null : permisjonsprosent.toString())
                .avvik(toSyntAvvik(avvik))
                .build();
    }
}
