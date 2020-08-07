package no.nav.registre.testnorge.synt.arbeidsforhold.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import no.nav.registre.testnorge.dto.synt.arbeidsforhold.v1.SyntArbeidsforholdDTO;
import no.nav.registre.testnorge.synt.arbeidsforhold.consumer.ArbeidsforholdConsumer;
import no.nav.registre.testnorge.synt.arbeidsforhold.consumer.StatiskeDataForvalterConsumer;
import no.nav.registre.testnorge.synt.arbeidsforhold.consumer.SyntArbeidsforholdConsumer;
import no.nav.registre.testnorge.synt.arbeidsforhold.consumer.dto.AnsettelsePeriodeDTO;
import no.nav.registre.testnorge.synt.arbeidsforhold.domain.Arbeidsforhold;
import no.nav.registre.testnorge.synt.arbeidsforhold.domain.Organisasjon;

@Service
@RequiredArgsConstructor
public class SyntArbeidsforholdService {

    private static final int MINIMAL_WORKING_AGE = 16;
    private static final int AGE_OF_MAJORITY = 18;

    private final SyntArbeidsforholdConsumer syntArbeidsforholdConsumer;
    private final ArbeidsforholdConsumer arbeidsforholdConsumer;
    private final StatiskeDataForvalterConsumer statiskeDataForvalterConsumer;

    public void genrate(SyntArbeidsforholdDTO dto) {

        Organisasjon organisasjon = statiskeDataForvalterConsumer.getRandomOrganisasjonWhichSupportsArbeidsforhold();
        var generated = syntArbeidsforholdConsumer.genererArbeidsforhold(dto.getIdent());
        AnsettelsePeriodeDTO ansettelsesPeriode = generated.getAnsettelsesPeriode();

        LocalDate fom = adjustFom(ansettelsesPeriode.getFom(), dto);

        LocalDate tom = adjustTom(ansettelsesPeriode.getTom(), fom);

        Arbeidsforhold arbeidsforhold = Arbeidsforhold
                .builder()
                .dto(generated)
                .organisasjon(organisasjon)
                .fom(fom)
                .tom(tom)
                .build();

        arbeidsforholdConsumer.createArbeidsforhold(arbeidsforhold);
    }

    private LocalDate adjustFom(LocalDate fom, SyntArbeidsforholdDTO dto) {
        if (fom.minusYears(MINIMAL_WORKING_AGE).isBefore(dto.getFoedselsdato())) {
            return dto.getFoedselsdato().plusYears(AGE_OF_MAJORITY);
        } else if (fom.isAfter(LocalDate.now())) {
            return LocalDate.now();
        }
        return fom;
    }

    private LocalDate adjustTom(LocalDate tom, LocalDate fom) {
        if (tom == null) {
            return null;
        } else if (tom.isBefore(fom)) {
            return fom.plusYears(2);
        }
        return tom;
    }
}