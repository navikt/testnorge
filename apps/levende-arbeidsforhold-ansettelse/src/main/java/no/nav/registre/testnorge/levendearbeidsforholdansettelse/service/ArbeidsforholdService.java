package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.AaregConsumer;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.arbeidsforhold.*;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsforholdService {
    private final AaregConsumer aaregConsumer;
    private final String arbeidstakerType = "Person";
    private final String arbeidsgiverType = "Organisasjon";
    private final String arbeidsforholdType = "ordinaertArbeidsforhold"; //#TODO KAN BLI HENTET FRA DB
    private final String arbeidstidsordning = "ikkeSkift";
    private final String ansettelsesform = "fast";

    public List<Arbeidsforhold> hentArbeidsforhold(String ident) {
        return aaregConsumer.hentArbeidsforhold(ident);
    }

    public HttpStatusCode opprettArbeidsforhold(String ident, String orgnummer, String yrke, String stillingsprosent) {
        return aaregConsumer.opprettArbeidsforhold(lagArbeidsforhold(ident, orgnummer, yrke, stillingsprosent));
    }

    private Arbeidsforhold lagArbeidsforhold(String ident, String orgnummer, String yrke, String prosent) {
        List<Arbeidsforhold> arbeidsforholdList = hentArbeidsforhold(ident);
        Double stillingsprosent = Double.valueOf(prosent);
        Double antallTimerPrUke = BigDecimal.valueOf(37.5*stillingsprosent/100).setScale(1, RoundingMode.HALF_UP).doubleValue();
        var arbeidsforholdId = new AtomicInteger(arbeidsforholdList.size());

        return Arbeidsforhold.builder()
                .arbeidsforholdId(Integer.toString(arbeidsforholdId.incrementAndGet()))
                .arbeidstaker(Person.builder()
                        .offentligIdent(ident)
                        .type(arbeidstakerType)
                        .build())
                .arbeidsgiver(Organisasjon.builder()
                        .organisasjonsnummer(orgnummer)
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

