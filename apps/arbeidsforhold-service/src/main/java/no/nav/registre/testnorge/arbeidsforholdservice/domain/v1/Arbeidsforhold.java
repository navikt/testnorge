package no.nav.registre.testnorge.arbeidsforholdservice.domain.v1;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto.ArbeidsforholdDTO;
import no.nav.registre.testnorge.arbeidsforholdservice.exception.ArbeidsforholdNotFoundException;

import java.time.LocalDate;


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
        orgnummer = dto.getArbeidsgiver().getOrganisasjonsnummer();


        if (dto.getArbeidsavtaler().isEmpty()) {
            throw new ArbeidsforholdNotFoundException("Finner ikke arbeidsforhold");
        }

        if (dto.getArbeidsavtaler().size() > 1) {
            log.warn("Fant flere arbeidsavtaler. Velger den første i listen");
        }

        var arbeidsavtale = dto.getArbeidsavtaler().get(0);
        stillingsprosent = arbeidsavtale.getStillingsprosent();
        yrke = arbeidsavtale.getYrke();
        arbeidstidsordning = arbeidsavtale.getArbeidstidsordning();
        antallTimerPrUke = arbeidsavtale.getAntallTimerPrUke();
        sistLoennsendring = arbeidsavtale.getSistLoennsendring();
        fom = dto.getAnsettelsesperiode().getPeriode().getFom();
        tom = dto.getAnsettelsesperiode().getPeriode().getTom();
        ident = dto.getArbeidstaker().getOffentligIdent();
        type = dto.getType();
    }

    public no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v1.ArbeidsforholdDTO toDTO() {
        return no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v1.ArbeidsforholdDTO
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
