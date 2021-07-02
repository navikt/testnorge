package no.nav.registre.testnorge.arbeidsforholdservice.domain.v2;

import lombok.AllArgsConstructor;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto.ArbeidsforholdDTO;

import java.util.stream.Collectors;

@AllArgsConstructor
public class Arbeidsforhold {

    private final ArbeidsforholdDTO dto;


    public no.nav.registre.testnorge.arbeidsforholdservice.provider.v2.dto.ArbeidsforholdDTO toDTO() {

        var antallImerForTimeloennetList = dto.getAntallTimerForTimeloennet() == null ?
                null :
                dto.getAntallTimerForTimeloennet()
                        .stream()
                        .map(AntallTimerForTimeloennet::new)
                        .map(AntallTimerForTimeloennet::toDTO)
                        .collect(Collectors.toList());

        var utenlandsopphold = dto.getUtenlandsopphold() == null ?
                null :
                dto.getUtenlandsopphold()
                        .stream()
                        .map(Utenlandsopphold::new)
                        .map(Utenlandsopphold::toDTO)
                        .collect(Collectors.toList());

        var permisjoner = dto.getPermisjonPermitteringer() == null ?
                null :
                dto.getPermisjonPermitteringer()
                        .stream()
                        .map(Permisjon::new)
                        .map(Permisjon::toDTO)
                        .collect(Collectors.toList());



        return no.nav.registre.testnorge.arbeidsforholdservice.provider.v2.dto.ArbeidsforholdDTO
                .builder()
                .type(dto.getType())
                .arbeidsforholdId(dto.getArbeidsforholdId())
                .antallTimerForTimeloennet(antallImerForTimeloennetList)
                .arbeidsgiver(new Arbeidsgiver(dto.getArbeidsgiver()).toDTO())
                .ansettelsesperiode(new Ansettelsesperiode(dto.getAnsettelsesperiode()).toDTO())
                .arbeidsavtaler(dto.getArbeidsavtaler().stream().map(Arbeidsavtale::new).map(Arbeidsavtale::toDTO).collect(Collectors.toList()))
                .fartoy(dto.getArbeidsavtaler().get(0).getSkipstype() == null ? null : new Fartoy(dto.getArbeidsavtaler().get(0)).toDTO())
                .arbeidstaker(new Arbeidstaker(dto.getArbeidstaker()).toDTO())
                .utenlandsopphold(utenlandsopphold)
                .permisjonPermitteringer(permisjoner)
                .build();
    }

}
