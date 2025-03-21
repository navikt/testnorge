package no.nav.testnav.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.levendearbeidsforholdansettelse.consumers.TenorConsumer;
import no.nav.testnav.levendearbeidsforholdansettelse.domain.DatoIntervall;
import no.nav.testnav.levendearbeidsforholdansettelse.domain.dto.ArbeidsforholdDTO;
import no.nav.testnav.levendearbeidsforholdansettelse.domain.dto.ArbeidsforholdResponseDTO;
import no.nav.testnav.levendearbeidsforholdansettelse.domain.dto.KanAnsettesDTO;
import no.nav.testnav.levendearbeidsforholdansettelse.domain.dto.OrganisasjonResponseDTO;
import no.nav.testnav.levendearbeidsforholdansettelse.domain.dto.PdlPersonDTO;
import no.nav.testnav.levendearbeidsforholdansettelse.domain.kodeverk.KodeverkNavn;
import no.nav.testnav.levendearbeidsforholdansettelse.entity.AnsettelseLogg;
import no.nav.testnav.levendearbeidsforholdansettelse.entity.JobbParameterNavn;
import no.nav.testnav.levendearbeidsforholdansettelse.utility.SannsynlighetVelger;
import no.nav.testnav.libs.dto.levendearbeidsforhold.v1.Arbeidsavtale;
import no.nav.testnav.libs.dto.levendearbeidsforhold.v1.Arbeidsforhold;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.testnav.levendearbeidsforholdansettelse.entity.JobbParameterNavn.ANTALL_ORGANISASJONER;
import static no.nav.testnav.levendearbeidsforholdansettelse.entity.JobbParameterNavn.ANTALL_PERSONER;
import static no.nav.testnav.levendearbeidsforholdansettelse.entity.JobbParameterNavn.ARBEIDSFORHOLD_TYPE;
import static no.nav.testnav.levendearbeidsforholdansettelse.entity.JobbParameterNavn.STILLINGSPROSENT;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNumeric;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnsettelseService {

    private static final Random RANDOM = new SecureRandom();
    private static final Integer ANTALL_RETRIES = 3;

    private final PdlService pdlService;
    private final TenorConsumer tenorConsumer;
    private final ArbeidsforholdService arbeidsforholdService;
    private final ParameterService parameterService;
    private final KodeverkService kodeverkService;
    private final AnsettelseLoggService ansettelseLoggService;

    @Async
    public void runAnsettelseService() {

        var startTime = System.currentTimeMillis();

        //Henter parametere fra db
        var parametere = parameterService.hentParametere()
                .doOnNext(param ->
                        log.info("Startet oppretting av {} personer i {} organisasjoner",
                                param.get("antallPersoner"),
                                param.get("antallOrganisasjoner")));

        //Henter yrkeskoder for å gi tilfeldige yrker
        var yrkeskoder = kodeverkService.hentKodeverkValues(KodeverkNavn.YRKER.value);

        //Initialiserer liste over alderspenn og liste med tidligste og seneste gyldig dato for ansttelse
        var datoIntervaller = SannsynlighetVelger.getDatointervaller();

        //Kjører ansettelse per org
        Flux.zip(parametere, yrkeskoder)
                .flatMap(tuple -> Flux.range(0, (int) getParameterValue(tuple.getT1(), ANTALL_ORGANISASJONER))
                        .flatMap(count -> tenorConsumer.hentOrganisasjon())
                        .flatMap(organisasjon -> sjekkOgSendinnArbeidsforhold(organisasjon, tuple.getT1(), tuple.getT2(), datoIntervaller)))
                .collectList()
                .subscribe(status -> log.info("Oppretting ferdig, antall ansettelser {}, medgått tid {} sekunder", status.size(),
                        (System.currentTimeMillis() - startTime) / 1000));
    }

    private Flux<AnsettelseLogg> sjekkOgSendinnArbeidsforhold(OrganisasjonResponseDTO organisasjon, Map<String, String> parametere,
                                                              List<String> yrkeskoder, List<DatoIntervall> datointervaller) {

        return  Flux.fromIterable(getFordeling(parametere).entrySet())
                .limitRate(1)
                .flatMap(intervall -> Flux.range(0, intervall.getValue())
                        .flatMap(index -> hentOgAnsett(organisasjon, parametere,
                                hentTilfeldigYrkeskode(yrkeskoder),
                                datointervaller.get(intervall.getKey()), new AtomicInteger(ANTALL_RETRIES))));
    }

    private Flux<AnsettelseLogg> hentOgAnsett(OrganisasjonResponseDTO organisasjon,
                                              Map<String, String> parametere,
                                              String yrkeskode, DatoIntervall datointervall, AtomicInteger retries) {

        if (retries.get() == 0) {
            return Flux.empty();
        }

        return getPersonSomKanAnsettes((int) getParameterValue(parametere, STILLINGSPROSENT),
                datointervall, organisasjon)
                .flatMap(kanAnsettes -> ansettPerson(kanAnsettes, yrkeskode, parametere)
                        .flatMap(response -> {
                            if (response.getStatusCode().is2xxSuccessful()) {
                                log.info("Opprettet arbeidsforhold orgnummer {}, ident {}, status {}",
                                        kanAnsettes.getOrgnummer(), kanAnsettes.getIdent(), response.getStatusCode());
                                return ansettelseLoggService.lagreAnsettelse(kanAnsettes, parametere);
                            } else {
                                log.error("Oppretting mot AAREG feilet, orgnummer {}, ident {} med feilmelding {} ",
                                        kanAnsettes.getOrgnummer(), kanAnsettes.getIdent(), response.getFeilmelding());
                                return hentOgAnsett(organisasjon, parametere,
                                        yrkeskode,
                                        datointervall, new AtomicInteger(retries.decrementAndGet()));
                            }
                        }));
    }

    private Flux<KanAnsettesDTO> getPersonSomKanAnsettes(Integer stillingsprosent, DatoIntervall intervall,
                                                         OrganisasjonResponseDTO organisasjon) {

        return getArbeidsforhold(intervall, organisasjon.getPostnummer())
                .flatMap(arbeidsforhold1 -> Flux.fromIterable(arbeidsforhold1.getArbeidsforhold())
                        .map(Arbeidsforhold::getArbeidsavtaler)
                        .flatMap(Flux::fromIterable)
                        .filter(arbeidsavtale -> nonNull(arbeidsavtale.getBruksperiode()) &&
                                isNull(arbeidsavtale.getBruksperiode().getTom()))
                        .filter(arbeidsavtale -> nonNull(arbeidsavtale.getStillingsprosent()))
                        .map(Arbeidsavtale::getStillingsprosent)
                        .reduce(0, (a, b) -> (int) (a + b))
                        .map(sum -> sum + stillingsprosent <= 100)
                        .map(done -> {
                            if (isTrue(done)) {
                                return Flux.just(KanAnsettesDTO.builder()
                                        .ident(arbeidsforhold1.getIdent())
                                        .orgnummer(organisasjon.getOrgnummer())
                                        .kanAnsettes(true)
                                        .antallEksisterendeArbeidsforhold(arbeidsforhold1.getArbeidsforhold().size())
                                        .build());
                            } else {
                                return getPersonSomKanAnsettes(stillingsprosent, intervall, organisasjon);
                            }
                        }))
                .flatMap(Flux::from);
    }

    private Flux<ArbeidsforholdDTO> getArbeidsforhold(DatoIntervall datoIntervall, String postnummer) {

        return pdlService.getPerson(datoIntervall, postnummer)
                .doOnNext(person -> log.info("Hentet person fra PDL med ident {}",
                        person.getFolkeregisteridentifikator().stream()
                                .filter(PdlPersonDTO.Person.Folkeregisteridentifikator::isIBRUK)
                                .map(PdlPersonDTO.Person.Folkeregisteridentifikator::getIdentifikasjonsnummer)
                                .findFirst().orElse(null)))
                .flatMap(person -> Flux.fromIterable(person.getFolkeregisteridentifikator())
                        .filter(PdlPersonDTO.Person.Folkeregisteridentifikator::isIBRUK)
                        .map(PdlPersonDTO.Person.Folkeregisteridentifikator::getIdentifikasjonsnummer))
                .flatMap(ident -> arbeidsforholdService.getArbeidsforhold(ident)
                        .collectList()
                        .map(arbeidforhold -> ArbeidsforholdDTO.builder()
                                .ident(ident)
                                .arbeidsforhold(arbeidforhold)
                                .build()));
    }

    private Flux<ArbeidsforholdResponseDTO> ansettPerson(KanAnsettesDTO arbeidsforhold, String yrke,
                                                         Map<String, String> parametere) {

        return arbeidsforholdService.opprettArbeidsforhold(arbeidsforhold, yrke,
                (String) getParameterValue(parametere, ARBEIDSFORHOLD_TYPE),
                (int) getParameterValue(parametere, STILLINGSPROSENT));
    }

    private static int getAntallAnsettelserIHverOrg(Map<String, String> parametere) {

        //Kan implementere mer tilfeldig fordelig, foreløpig får alle organisasjonene like mange folk
        return (int) getParameterValue(parametere, ANTALL_PERSONER) /
                (int) getParameterValue(parametere, ANTALL_ORGANISASJONER);
    }

    private String hentTilfeldigYrkeskode(List<String> yrkeskoder) {

        return yrkeskoder.get(RANDOM.nextInt(yrkeskoder.size()));
    }

    private static Object getParameterValue(Map<String, String> parametere, JobbParameterNavn parameterNavn) {

        var parameter = parametere.get(parameterNavn.value);
        return isNumeric(parameter) ? Integer.parseInt(parameter) : parameter;
    }

    private Map<Integer, Integer> getFordeling(Map<String, String> parametre) {

        return SannsynlighetVelger.getFordeling(getAntallAnsettelserIHverOrg(parametre));
    }
}
