package no.nav.registre.testnorge.synt.arbeidsforhold.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import no.nav.registre.testnorge.dto.synt.arbeidsforhold.v1.SyntArbeidsforholdDTO;
import no.nav.registre.testnorge.synt.arbeidsforhold.consumer.ArbeidsforholdConsumer;
import no.nav.registre.testnorge.synt.arbeidsforhold.consumer.KodeverkConsumer;
import no.nav.registre.testnorge.synt.arbeidsforhold.consumer.StatiskeDataForvalterConsumer;
import no.nav.registre.testnorge.synt.arbeidsforhold.consumer.SyntArbeidsforholdConsumer;
import no.nav.registre.testnorge.synt.arbeidsforhold.consumer.dto.AnsettelsePeriodeDTO;
import no.nav.registre.testnorge.synt.arbeidsforhold.domain.Arbeidsforhold;
import no.nav.registre.testnorge.synt.arbeidsforhold.domain.KodeverkSet;
import no.nav.registre.testnorge.synt.arbeidsforhold.domain.Organisasjon;

@Service
@RequiredArgsConstructor
public class SyntArbeidsforholdService {

    private static final int MINIMAL_WORKING_AGE = 16;
    private static final int AGE_OF_MAJORITY = 18;

    private final SyntArbeidsforholdConsumer syntArbeidsforholdConsumer;
    private final ArbeidsforholdConsumer arbeidsforholdConsumer;
    private final StatiskeDataForvalterConsumer statiskeDataForvalterConsumer;
    private final KodeverkConsumer kodeverkConsumer;

    public void genrate(SyntArbeidsforholdDTO dto) {

        Organisasjon organisasjon = statiskeDataForvalterConsumer.getRandomOrganisasjonWhichSupportsArbeidsforhold();

        var yrkerKodeverk = kodeverkConsumer.getYrkerKodeverk();
        var generated = syntArbeidsforholdConsumer.genererArbeidsforhold(dto.getIdent());


        AnsettelsePeriodeDTO ansettelsesPeriode = generated.getAnsettelsesPeriode();
        LocalDate generatedFom = ansettelsesPeriode.getFom();
        LocalDate generatedTom = ansettelsesPeriode.getTom();

        LocalDate fom = generatedFom.minusYears(MINIMAL_WORKING_AGE).isBefore(dto.getFoedselsdato())
                ? dto.getFoedselsdato().plusYears(AGE_OF_MAJORITY)
                : generatedFom;

        LocalDate tom = generatedTom == null ? null : generatedTom.isBefore(fom)
                ? generatedTom.plusYears(2)
                : generatedTom;

        Arbeidsforhold arbeidsforhold = Arbeidsforhold
                .builder()
                .dto(generated)
                .kodeverk(yrkerKodeverk)
                .organisasjon(organisasjon)
                .fom(fom)
                .tom(tom)
                .build();

        arbeidsforholdConsumer.createArbeidsforhold(arbeidsforhold);
    }
}