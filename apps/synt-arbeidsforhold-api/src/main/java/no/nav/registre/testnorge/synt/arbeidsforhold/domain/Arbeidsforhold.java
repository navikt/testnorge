package no.nav.registre.testnorge.synt.arbeidsforhold.domain;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

import no.nav.registre.testnorge.dto.arbeidsforhold.v1.ArbeidsforholdDTO;
import no.nav.registre.testnorge.synt.arbeidsforhold.consumer.dto.SyntArbeidsforholdDTO;

@Value
@Builder
public class Arbeidsforhold {
    SyntArbeidsforholdDTO dto;
    Organisasjon organisasjon;
    LocalDate fom;
    LocalDate tom;
    KodeverkSet kodeverk;

    public ArbeidsforholdDTO toDTO() {
        String kode = kodeverk.contains(dto.getArbeidsavtale().getYrke())
                ? dto.getArbeidsavtale().getYrke()
                : kodeverk.getRandomKode();

        return ArbeidsforholdDTO
                .builder()
                .ident(dto.getArbeidstaker().getIdent())
                .fom(fom)
                .tom(tom)
                .orgnummer(organisasjon.getOrgnummer())
                .stillingsprosent(dto.getArbeidsavtale().getStillingsprosent())
                .yrke(kode)
                .arbeidstidsordning(dto.getArbeidsavtale().getArbeidstidsordning())
                .arbeidsforholdId(dto.getArbeidsforholdID())
                .build();
    }
}
