package no.nav.registre.testnorge.oppsummeringsdokuemntservice.domain;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v1.ArbeidsforholdDTO;

@Slf4j
@Getter
public class Arbeidsforhold {
    private final String arbeidsforholdId;
    private final String orgnummer;
    private final Float stillingsprosent;
    private final String arbeidstidsordning;
    private final String yrke;
    private final LocalDate fom;
    private final LocalDate tom;
    private final String ident;
    private final String type;
    private final Float antallTimerPrUke;
    private final LocalDate sistLoennsendring;


    public Arbeidsforhold(ArbeidsforholdDTO dto) {
        arbeidsforholdId = dto.getArbeidsforholdId();
        orgnummer = dto.getOrgnummer();
        stillingsprosent = dto.getStillingsprosent();
        arbeidstidsordning = dto.getArbeidstidsordning();
        yrke = dto.getYrke();
        fom = dto.getFom();
        tom = dto.getTom();
        antallTimerPrUke = dto.getAntallTimerPrUke();
        sistLoennsendring = dto.getSistLoennsendring();
        ident = dto.getIdent();
        type = dto.getType();
    }

    public ArbeidsforholdDTO toDTO() {
        return ArbeidsforholdDTO
                .builder()
                .arbeidsforholdId(arbeidsforholdId)
                .orgnummer(orgnummer)
                .stillingsprosent(stillingsprosent)
                .arbeidstidsordning(arbeidstidsordning)
                .yrke(yrke)
                .fom(fom)
                .tom(tom)
                .antallTimerPrUke(antallTimerPrUke)
                .sistLoennsendring(sistLoennsendring)
                .ident(ident)
                .type(type)
                .build();
    }
}
