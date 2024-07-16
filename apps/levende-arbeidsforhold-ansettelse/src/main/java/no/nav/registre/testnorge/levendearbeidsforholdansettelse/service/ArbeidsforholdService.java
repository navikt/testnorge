package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.AaregConsumer;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.v1.*;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.v1.util.JavaTimeUtil;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsforholdService {
    private final AaregConsumer aaregConsumer;

    public List<Arbeidsforhold> hentArbeidsforhold(String ident) {
        return aaregConsumer.hentArbeidsforhold(ident);
    }

    public void opprettArbeidsforhold(String ident, String orgnummer) {
        aaregConsumer.opprettArbeidsforhold(lagArbeidsforhold(ident, orgnummer));
    }

    private Arbeidsforhold lagArbeidsforhold(String ident, String orgnummer) {
        List<Arbeidsforhold> arbeidsforholdList = hentArbeidsforhold(ident);

        log.info(String.valueOf(arbeidsforholdList.size()));

        var arbeidsforholdId = new AtomicInteger(arbeidsforholdList.size());

        Arbeidsforhold arbeidsforhold = Arbeidsforhold.builder()
                .arbeidsforholdId(Integer.toString(arbeidsforholdId.incrementAndGet()))
                .arbeidstaker(Person.builder()
                        .offentligIdent(ident)
                        .type("Person")
                        .build())
                .arbeidsgiver(Organisasjon.builder()
                        .organisasjonsnummer(orgnummer)
                        .type("Organisasjon")
                        .build())
                .type("ordinaertArbeidsforhold")
                .ansettelsesperiode(Ansettelsesperiode.builder()
                        .periode(Periode.builder()
                                .fom(LocalDate.now())
                                .build())
                        .build())
                .arbeidsavtaler(List.of(OrdinaerArbeidsavtale.builder()
                                .antallTimerPrUke(37.5)
                                .arbeidstidsordning("ikkeSkift")
                                .stillingsprosent(100.00)
                                .yrke("2130123")
                                .ansettelsesform("fast")
                                .sistStillingsendring(LocalDate.now())
                        .build()))
                .build();
        return arbeidsforhold;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void testOpprettArbeidsforhold() {
        opprettArbeidsforhold("23456833225", "896929119");
        log.info(String.valueOf(hentArbeidsforhold("23456833225")));
    }
}

