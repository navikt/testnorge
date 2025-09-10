package no.nav.dolly.budpro.generate;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.budpro.ansettelsestype.AnsettelsestypeService;
import no.nav.dolly.budpro.kommune.KommuneService;
import no.nav.dolly.budpro.koststed.Koststed;
import no.nav.dolly.budpro.koststed.KoststedService;
import no.nav.dolly.budpro.navn.GeneratedNameService;
import no.nav.dolly.budpro.organisasjonsenhet.OrganisasjonsenhetService;
import no.nav.dolly.budpro.ressursnummer.LeaderGenerator;
import no.nav.dolly.budpro.ressursnummer.ResourceNumberGenerator;
import no.nav.dolly.budpro.stillinger.StillingService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
class BudProService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final String UNSPECIFIED_ID = "000000";
    private static final String UNSPECIFIED_NAME = "Uspesifisert";

    private final AnsettelsestypeService ansettelsestypeService;
    private final KoststedService koststedService;
    private final GeneratedNameService nameService;
    private final KommuneService kommuneService;
    private final StillingService stillingService;
    private final OrganisasjonsenhetService organisasjonsenhetService;

    Flux<BudproRecord> override(Long seed, int numberOfEmployees, BudproRecord override) {
        return randomize(seed, numberOfEmployees)
                .map(randomized ->
                        new BudproRecord(
                                replace(randomized.aga(), override.aga()),
                                replace(randomized.agaBeskrivelse(), override.agaBeskrivelse()),
                                replace(randomized.ansettelsestype(), override.ansettelsestype()),
                                replace(randomized.arbeidsstedKommune(), override.arbeidsstedKommune()),
                                replace(randomized.felles(), override.felles()),
                                replace(randomized.fellesBeskrivelse(), override.fellesBeskrivelse()),
                                replace(randomized.fraDato(), override.fraDato()),
                                replace(randomized.foedselsdato(), override.foedselsdato()),
                                replace(randomized.koststed(), override.koststed()),
                                replace(randomized.koststedBeskrivelse(), override.koststedBeskrivelse()),
                                replace(randomized.koststedUtlaantFra(), override.koststedUtlaantFra()),
                                replace(randomized.koststedUtlaantFraBeskrivelse(), override.koststedUtlaantFraBeskrivelse()),
                                replace(randomized.lederUtlaantFra(), override.lederUtlaantFra()),
                                replace(randomized.ledersNavn(), override.ledersNavn()),
                                replace(randomized.ledersRessursnummer(), override.ledersRessursnummer()),
                                replace(randomized.navn(), override.navn()),
                                replace(randomized.oppgave(), override.oppgave()),
                                replace(randomized.oppgaveBeskrivelse(), override.oppgaveBeskrivelse()),
                                replace(randomized.oppgaveUtlaantFra(), override.oppgaveUtlaantFra()),
                                replace(randomized.oppgaveUtlaantFraBeskrivelse(), override.oppgaveUtlaantFraBeskrivelse()),
                                replace(randomized.orgenhet(), override.orgenhet()),
                                replace(randomized.orgenhetNavn(), override.orgenhetNavn()),
                                replace(randomized.permisjonskode(), override.permisjonskode()),
                                replace(randomized.produkt(), override.produkt()),
                                replace(randomized.produktBeskrivelse(), override.produktBeskrivelse()),
                                replace(randomized.produktUtlaantFra(), override.produktUtlaantFra()),
                                replace(randomized.produktUtlaantFraBeskrivelse(), override.produktUtlaantFraBeskrivelse()),
                                replace(randomized.ressursnummer(), override.ressursnummer()),
                                replace(randomized.skattekommune(), override.skattekommune()),
                                replace(randomized.sluttetDato(), override.sluttetDato()),
                                replace(randomized.statskonto(), override.statskonto()),
                                replace(randomized.statskontoKapittel(), override.statskontoKapittel()),
                                replace(randomized.statskontoPost(), override.statskontoPost()),
                                replace(randomized.stillingsnummer(), override.stillingsnummer()),
                                replace(randomized.stillingsprosent(), override.stillingsprosent()),
                                replace(randomized.tilDato(), override.tilDato()),
                                replace(randomized.aarsloennInklFasteTillegg(), override.aarsloennInklFasteTillegg())
                        )
                );
    }

    private static String replace(String original, String replacement) {
        return replacement == null ? original : replacement;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    Flux<BudproRecord> randomize(Long seed, int numberOfEmployees) {
        var random = seed == null ? new Random() : new Random(seed);
        var numberOfLeaders = (int) Math.ceil((double) numberOfEmployees / 10);
        return nameService.getNames(seed, numberOfEmployees + numberOfLeaders)
            .collectList()
            .flatMapMany(allNamesList -> {
                String[] allNames = allNamesList.toArray(new String[0]);
                String[] employeeNames = Arrays.copyOfRange(allNames, numberOfLeaders, numberOfEmployees + numberOfLeaders);
                String[] leaderNames = Arrays.copyOfRange(allNames, 0, numberOfLeaders);
                var resourceNumberGenerator = new ResourceNumberGenerator(random);
                var leaderResourceNumbers = resourceNumberGenerator.get(numberOfLeaders);
                var leaderGenerator = new LeaderGenerator(leaderNames, leaderResourceNumbers);
                return Flux.range(0, numberOfEmployees)
                    .map(i -> {
                        var leader = leaderGenerator.getRandom(random);
                        var municipality = kommuneService.getRandom(random);
                        var aga = "060501180000";
                        var agaBeskrivelse = "NAV Trygder, pensjon";
                        var ansettelsestype = ansettelsestypeService.getRandom(random);
                        var arbeidsstedKommune = municipality.getId();
                        var felles = UNSPECIFIED_ID;
                        var fellesBeskrivelse = UNSPECIFIED_NAME;
                        var fraDato = fraDato(random);
                        var foedselsdato = foedselsdato(random);
                        var koststed = koststedService.getRandom(random);
                        var koststedUtlaantFra = koststedUtlaantFra(random, koststed);
                        var lederUtlaantFra = leader.utlaantFra();
                        var ledersNavn = leader.navn();
                        var ledersRessursnummer = leader.ressursnummer();
                        var navn = employeeNames[i];
                        var oppgave = UNSPECIFIED_ID;
                        var oppgaveBeskrivelse = UNSPECIFIED_NAME;
                        var oppgaveUtlaantFra = UNSPECIFIED_ID;
                        var oppgaveUtlaantFraBeskrivelse = UNSPECIFIED_NAME;
                        var organisasjonsenhet = organisasjonsenhetService.getRandom(random);
                        var orgenhet = organisasjonsenhet.getId();
                        var orgenhetNavn = organisasjonsenhet.getName();
                        var permisjonskode = "";
                        var produkt = UNSPECIFIED_ID;
                        var produktBeskrivelse = UNSPECIFIED_NAME;
                        var produktUtlaantFra = UNSPECIFIED_ID;
                        var produktUtlaantFraBeskrivelse = UNSPECIFIED_NAME;
                        var ressursnummer = resourceNumberGenerator.next();
                        var sluttetDato = LocalDate.of(2099, 12, 31).format(DATE_FORMATTER);
                        var skattekommune = municipality.getId();
                        var statskonto = "060501000000";
                        var statskontoKapittel = "0605";
                        var statskontoPost = "01";
                        var stillingsnummer = stillingService.getRandom(random).getNumber();
                        var stillingsprosent = stillingsprosent(random);
                        var tilDato = tilDato(random);
                        var aarsloennInklFasteTillegg = aarsloenn(random);
                        return new BudproRecord(
                            aga,
                            agaBeskrivelse,
                            ansettelsestype,
                            arbeidsstedKommune,
                            felles,
                            fellesBeskrivelse,
                            fraDato,
                            foedselsdato,
                            koststed.getId(),
                            koststed.getDescription(),
                            koststedUtlaantFra.orElse(Koststed.EMPTY).getId(),
                            koststedUtlaantFra.orElse(Koststed.EMPTY).getDescription(),
                            lederUtlaantFra,
                            ledersNavn,
                            ledersRessursnummer,
                            navn,
                            oppgave,
                            oppgaveBeskrivelse,
                            oppgaveUtlaantFra,
                            oppgaveUtlaantFraBeskrivelse,
                            orgenhet,
                            orgenhetNavn,
                            permisjonskode,
                            produkt,
                            produktBeskrivelse,
                            produktUtlaantFra,
                            produktUtlaantFraBeskrivelse,
                            ressursnummer,
                            skattekommune,
                            sluttetDato,
                            statskonto,
                            statskontoKapittel,
                            statskontoPost,
                            stillingsnummer,
                            stillingsprosent,
                            tilDato,
                            aarsloennInklFasteTillegg
                        );
                    });
            });
    }

    private String aarsloenn(Random random) {
        var value = random.nextInt(500000, 1000000);
        return String.valueOf(value - (value % 100));
    }

    private String foedselsdato(Random random) {
        var ageInYears = random.nextInt(18, 67);
        var then = LocalDate
                .now()
                .minusDays(random.nextInt(365))
                .plusDays(random.nextInt(365))
                .minusYears(ageInYears);
        return then.format(DATE_FORMATTER);
    }

    private String stillingsprosent(Random random) {
        var percentage = random.nextInt(100);
        if (percentage > 90) {
            return "0";
        }
        if (percentage > 80) {
            return "20";
        }
        if (percentage > 50) {
            return "80";
        }
        return "100";
    }

    private Optional<Koststed> koststedUtlaantFra(Random random, Koststed exception) {
        var percentage = random.nextInt(100);
        if (percentage > 50) {
            return Optional.of(koststedService.getRandomExcept(random, exception));
        }
        return Optional.empty();
    }

    private String fraDato(Random random) {
        var employmentInYears = random.nextInt(1, 20);
        var then = LocalDate
                .now()
                .minusDays(random.nextInt(365))
                .plusDays(random.nextInt(365))
                .minusYears(employmentInYears);
        return then.format(DATE_FORMATTER);
    }

    private String tilDato(Random random) {
        var monthsAhead = random.nextInt(1, 24);
        var then = LocalDate
                .now()
                .plusDays(random.nextInt(365))
                .plusDays(random.nextInt(365))
                .plusMonths(monthsAhead);
        return then.format(DATE_FORMATTER);
    }

}
