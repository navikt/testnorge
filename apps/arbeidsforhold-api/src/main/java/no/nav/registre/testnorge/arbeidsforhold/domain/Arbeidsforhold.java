package no.nav.registre.testnorge.arbeidsforhold.domain;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

import no.nav.registre.testnorge.arbeidsforhold.consumer.dto.ArbeidsforholdDTO;

@Slf4j
@Getter
public class Arbeidsforhold {
    private final String arbeidsforholdId;
    private final String orgnummer;
    private final Double stillingsprosent;
    private final String yrke;
    private final LocalDate fom;
    private final LocalDate tom;
    private final String ident;

    public Arbeidsforhold(ArbeidsforholdDTO dto) {
        arbeidsforholdId = dto.getArbeidsforholdId();
        orgnummer = dto.getArbeidsgiver().getOrganisasjonsnummer();

        if (dto.getArbeidsavtaler().isEmpty()) {
            throw new RuntimeException("Finner ikke arbeidesforhold");
        }

        if (dto.getArbeidsavtaler().size() > 1) {
            log.warn("Fant flere arbeidsavtaler. Velger den første i listen");
        }

        var arbeidsavtale = dto.getArbeidsavtaler().get(0);
        stillingsprosent = arbeidsavtale.getStillingsprosent();
        yrke = arbeidsavtale.getYrke();
        fom = dto.getAnsettelsesperiode().getPeriode().getFom();
        tom = dto.getAnsettelsesperiode().getPeriode().getTom();
        ident = dto.getArbeidstaker().getOffentligIdent();
    }

    public Arbeidsforhold(no.nav.registre.testnorge.dto.arbeidsforhold.v1.ArbeidsforholdDTO dto) {
        arbeidsforholdId = dto.getArbeidsforholdId();
        orgnummer = dto.getOrgnummer();
        stillingsprosent = dto.getStillingsprosent();
        yrke = dto.getYrke();
        fom = dto.getFom();
        tom = dto.getTom();
        ident = dto.getIdent();
    }

    public no.nav.registre.testnorge.dto.arbeidsforhold.v1.ArbeidsforholdDTO toDTO() {
        return no.nav.registre.testnorge.dto.arbeidsforhold.v1.ArbeidsforholdDTO
                .builder()
                .arbeidsforholdId(arbeidsforholdId)
                .orgnummer(orgnummer)
                .stillingsprosent(stillingsprosent)
                .yrke(yrke)
                .fom(fom)
                .tom(tom)
                .ident(ident)
                .build();
    }
}
