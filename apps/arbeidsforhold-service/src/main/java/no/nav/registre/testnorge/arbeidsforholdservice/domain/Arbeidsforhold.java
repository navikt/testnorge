package no.nav.registre.testnorge.arbeidsforholdservice.domain;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto.ArbeidsforholdDTO;
import no.nav.registre.testnorge.arbeidsforholdservice.exception.ArbeidsforholdNotFoundException;

import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class Arbeidsforhold {

    private final ArbeidsforholdDTO dto;

    public  no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v1.ArbeidsforholdDTO toV1DTO() {

        if (dto.getArbeidsavtaler().isEmpty()) {
            throw new ArbeidsforholdNotFoundException("Finner ikke arbeidsforhold");
        }

        if (dto.getArbeidsavtaler().size() > 1) {
            log.warn("Fant flere arbeidsavtaler. Velger den første i listen");
        }


        var arbeidsavtale = dto.getArbeidsavtaler().get(0);

        return no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v1.ArbeidsforholdDTO
                .builder()
                .arbeidsforholdId(dto.getArbeidsforholdId())
                .stillingsprosent(arbeidsavtale.getStillingsprosent())
                .yrke(arbeidsavtale.getYrke())
                .arbeidstidsordning(arbeidsavtale.getArbeidstidsordning())
                .antallTimerPrUke(arbeidsavtale.getAntallTimerPrUke())
                .sistLoennsendring(arbeidsavtale.getSistLoennsendring())
                .fom(dto.getAnsettelsesperiode().getPeriode().getFom())
                .tom(dto.getAnsettelsesperiode().getPeriode().getTom())
                .ident(dto.getArbeidstaker().getOffentligIdent())
                .type(dto.getType())
                .build();
    }


    public no.nav.registre.testnorge.arbeidsforholdservice.provider.v2.dto.ArbeidsforholdDTO toV2DTO() {

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
