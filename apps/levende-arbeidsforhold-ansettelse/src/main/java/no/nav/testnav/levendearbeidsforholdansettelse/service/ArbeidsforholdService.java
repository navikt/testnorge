package no.nav.testnav.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.levendearbeidsforholdansettelse.consumers.AaregConsumer;
import no.nav.testnav.levendearbeidsforholdansettelse.domain.dto.KanAnsettesDTO;
import no.nav.testnav.libs.dto.levendearbeidsforhold.v1.Ansettelsesperiode;
import no.nav.testnav.libs.dto.levendearbeidsforhold.v1.Arbeidsforhold;
import no.nav.testnav.libs.dto.levendearbeidsforhold.v1.OrdinaerArbeidsavtale;
import no.nav.testnav.libs.dto.levendearbeidsforhold.v1.Organisasjon;
import no.nav.testnav.libs.dto.levendearbeidsforhold.v1.Periode;
import no.nav.testnav.libs.dto.levendearbeidsforhold.v1.Person;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsforholdService {

    private static final String arbeidstakerType = "Person";
    private static final String arbeidsgiverType = "Organisasjon";
    private static final String arbeidstidsordning = "ikkeSkift";
    private static final String ansettelsesform = "fast";
    private static final double TIMER_HUNDRE_PROSENT = 37.5;
    private static final int HUNDRE_PROSENT = 100;

    private final AaregConsumer aaregConsumer;

    /**
     * @param ident Identnummeret til personen det skal hentes arbeidsforhold for
     * @return Liste med arbeidsforhold
     */
    public Flux<Arbeidsforhold> getArbeidsforhold(String ident) {

        return aaregConsumer.hentArbeidsforhold(ident);
    }


    /**
     * Oppretter arbeidsforhold via aaregConsumer
     *
     * @param kanAnsettes      ident og orgnummer
     * @param yrke             Yrkeskoden til yrket personen skal ansettes med
     * @param stillingsprosent Stillinsprosenten arbeidsforholdet skal ha
     * @return HttpStatusCode basert på resultatet av spørringen
     */
    public Flux<HttpStatusCode> opprettArbeidsforhold(KanAnsettesDTO kanAnsettes, String yrke,
                                                      String arbeidsforholdstype, Integer stillingsprosent) {

        return aaregConsumer.opprettArbeidsforhold(lagArbeidsforhold(kanAnsettes, yrke, arbeidsforholdstype, stillingsprosent));
    }


    /**
     * Bygger et rbeidsforhold-objekt basert på parameterene
     *
     * @param kanAnsettes ident og orgnummer
     * @param yrke      Yrkeskoden til yrket personen skal ansettes med
     * @param prosent   Stillinsprosenten arbeidsforholdet skal ha
     * @return Et Arbeidsforhold-objekt basert på parameterene
     */
    private Arbeidsforhold lagArbeidsforhold(KanAnsettesDTO kanAnsettes, String yrke, String arbeidsforholdType, Integer prosent) {

        var stillingsprosent = (double) prosent;
        var antallTimerPrUke = BigDecimal.valueOf(TIMER_HUNDRE_PROSENT * stillingsprosent / HUNDRE_PROSENT).setScale(1, RoundingMode.HALF_UP).doubleValue();

        return Arbeidsforhold.builder()
                .arbeidsforholdId(Integer.toString(kanAnsettes.getAntallEksisterendeArbeidsforhold() + 1))
                .arbeidstaker(Person.builder()
                        .offentligIdent(kanAnsettes.getIdent())
                        .type(arbeidstakerType)
                        .build())
                .arbeidsgiver(Organisasjon.builder()
                        .organisasjonsnummer(kanAnsettes.getOrgnummer())
                        .type(arbeidsgiverType)
                        .build())
                .type(arbeidsforholdType)
                .ansettelsesperiode(Ansettelsesperiode.builder()
                        .periode(Periode.builder()
                                .fom(LocalDate.now())
                                .build())
                        .build())
                .arbeidsavtaler(List.of(OrdinaerArbeidsavtale.builder()
                        .antallTimerPrUke(antallTimerPrUke)
                        .arbeidstidsordning(arbeidstidsordning)
                        .stillingsprosent(stillingsprosent)
                        .yrke(yrke)
                        .ansettelsesform(ansettelsesform)
                        .sistStillingsendring(LocalDate.now())
                        .build()))
                .build();
    }
}
