package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.amelding;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.dto.syntrest.v1.InntektDTO;

@Value
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class Inntekt extends Generated {
    LocalDate startdatoOpptjeningsperiode;
    LocalDate sluttdatoOpptjeningsperiode;
    Integer antall;
    String opptjeningsland;
    List<Avvik> avvik;

    public Inntekt(no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.InntektDTO dto){
        sluttdatoOpptjeningsperiode = dto.getSluttdatoOpptjeningsperiode();
        startdatoOpptjeningsperiode = dto.getStartdatoOpptjeningsperiode();
        antall = dto.getAntall();
        opptjeningsland = dto.getOpptjeningsland();
        avvik = dto.getAvvik().stream().map(Avvik::new).collect(Collectors.toList());
    }

    public Inntekt(InntektDTO dto) {
        sluttdatoOpptjeningsperiode = dto.getSluttdatoOpptjeningsperiode();
        startdatoOpptjeningsperiode = dto.getStartdatoOpptjeningsperiode();
        antall = dto.getAntall();
        opptjeningsland = dto.getOpptjeningsland();
        avvik = dto.getAvvik() == null ? new ArrayList<>() : Collections.singletonList(new Avvik(dto.getAvvik()));
    }

    public InntektDTO toSynt() {
        return InntektDTO
                .builder()
                .startdatoOpptjeningsperiode(startdatoOpptjeningsperiode)
                .sluttdatoOpptjeningsperiode(sluttdatoOpptjeningsperiode)
                .antall(antall)
                .opptjeningsland(opptjeningsland)
                .avvik(toSyntAvvik(avvik))
                .build();
    }
}
