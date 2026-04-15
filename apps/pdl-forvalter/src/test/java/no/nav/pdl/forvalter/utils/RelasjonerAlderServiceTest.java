package no.nav.pdl.forvalter.utils;

import no.nav.pdl.forvalter.service.RelasjonerAlderService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BestillingRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelatertBiPersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RelasjonerAlderServiceTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 4, 7, 12, 0);
    private static final ZoneId ZONE = ZoneId.of("Europe/Oslo");

    @Mock
    private Clock clock;

    @InjectMocks
    private RelasjonerAlderService relasjonerAlderService;

    // --- fixFoedsel: foedselsdato ---

    @Test
    void shouldSetFoedtRangeFromFoedselsdato() {

        var bestilling = buildBestilling(PersonDTO.builder()
                .foedsel(List.of(FoedselDTO.builder()
                        .foedselsdato(LocalDateTime.of(1985, 1, 1, 1, 1))
                        .build()))
                .build());

        var result = relasjonerAlderService.fixRelasjonerAlder(bestilling);

        assertThat(result.getFoedtEtter(), is(equalTo(LocalDateTime.of(1984, 12, 31, 1, 1))));
        assertThat(result.getFoedtFoer(), is(equalTo(LocalDateTime.of(1985, 1, 2, 1, 1))));
    }

    // --- fixFoedsel: foedselsaar ---

    @Test
    void shouldSetFoedtRangeFromFoedselsaar() {

        var bestilling = buildBestilling(PersonDTO.builder()
                .foedsel(List.of(FoedselDTO.builder()
                        .foedselsaar(1985)
                        .build()))
                .build());

        var result = relasjonerAlderService.fixRelasjonerAlder(bestilling);

        assertThat(result.getFoedtEtter(), is(equalTo(LocalDateTime.of(1984, 12, 31, 23, 59))));
        assertThat(result.getFoedtFoer(), is(equalTo(LocalDateTime.of(1986, 1, 1, 0, 0))));
    }

    // --- fixFoedsel: alder ---

    @Test
    void shouldConvertAlderToFoedtRange() {

        stubClock();

        var bestilling = buildBestilling(PersonDTO.builder().build());
        bestilling.setAlder(30);

        var result = relasjonerAlderService.fixRelasjonerAlder(bestilling);

        assertThat(result.getAlder(), is(nullValue()));
        assertThat(result.getFoedtFoer(), is(equalTo(NOW.minusYears(30))));
        assertThat(result.getFoedtEtter(), is(equalTo(NOW.minusYears(30).minusMonths(6))));
    }

    @Test
    void shouldPreferAlderOverFoedselsdato() {

        stubClock();

        var bestilling = buildBestilling(PersonDTO.builder()
                .foedsel(List.of(FoedselDTO.builder()
                        .foedselsdato(LocalDateTime.of(1985, 1, 1, 0, 0))
                        .build()))
                .build());
        bestilling.setAlder(25);

        var result = relasjonerAlderService.fixRelasjonerAlder(bestilling);

        assertThat(result.getAlder(), is(nullValue()));
        assertThat(result.getFoedtFoer(), is(equalTo(NOW.minusYears(25))));
    }

    // --- fixFoedsel: no birth info ---

    @Test
    void shouldNotSetFoedtRangeWhenNoBirthInfo() {

        var bestilling = buildBestilling(PersonDTO.builder().build());

        var result = relasjonerAlderService.fixRelasjonerAlder(bestilling);

        assertThat(result.getFoedtEtter(), is(nullValue()));
        assertThat(result.getFoedtFoer(), is(nullValue()));
        assertThat(result.getAlder(), is(nullValue()));
    }

    // --- barn med eksisterende ident: setter hovedpersons alder ---

    @Test
    void shouldSetHovedpersonAlderBasedOnEldsteBarnWithIdent() {

        stubClock();

        var barnIdent = "01032050123";

        var bestilling = buildBestilling(PersonDTO.builder()
                .forelderBarnRelasjon(new ArrayList<>(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.FORELDER)
                                .relatertPersonsRolle(Rolle.BARN)
                                .relatertPerson(barnIdent)
                                .build())))
                .build());

        var result = relasjonerAlderService.fixRelasjonerAlder(bestilling);

        assertThat(result.getFoedtFoer(), is(notNullValue()));
        assertThat(result.getFoedtEtter(), is(notNullValue()));
    }

    // --- barn med nyRelatertPerson (alder satt) ---

    @Test
    void shouldSetHovedpersonAlderBasedOnBarnWithAlder() {

        stubClock();

        var bestilling = buildBestilling(PersonDTO.builder()
                .forelderBarnRelasjon(new ArrayList<>(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.FORELDER)
                                .relatertPersonsRolle(Rolle.BARN)
                                .nyRelatertPerson(PersonRequestDTO.builder()
                                        .alder(5)
                                        .build())
                                .build())))
                .build());

        var result = relasjonerAlderService.fixRelasjonerAlder(bestilling);

        assertThat(result.getFoedtFoer(), is(equalTo(NOW.minusYears(18 + 5))));
        assertThat(result.getFoedtEtter(), is(equalTo(NOW.minusYears(18 + 5).minusYears(18))));
    }

    // --- barn med RelatertBiPerson (foedselsdato) ---

    @Test
    void shouldSetHovedpersonAlderBasedOnBarnUtenFolkeregisterident() {

        stubClock();

        var bestilling = buildBestilling(PersonDTO.builder()
                .forelderBarnRelasjon(new ArrayList<>(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.MOR)
                                .relatertPersonsRolle(Rolle.BARN)
                                .relatertPersonUtenFolkeregisteridentifikator(
                                        RelatertBiPersonDTO.builder()
                                                .foedselsdato(NOW.minusYears(10).atZone(ZONE).toLocalDateTime())
                                                .build())
                                .build())))
                .build());

        var result = relasjonerAlderService.fixRelasjonerAlder(bestilling);

        assertThat(result.getFoedtFoer(), is(equalTo(NOW.minusYears(18 + 10))));
        assertThat(result.getFoedtEtter(), is(equalTo(NOW.minusYears(18 + 10).minusYears(18))));
    }

    // --- flere barn: eldste styrer alder ---

    @Test
    void shouldUseEldsteBarnWhenMultipleChildren() {

        stubClock();

        var bestilling = buildBestilling(PersonDTO.builder()
                .forelderBarnRelasjon(new ArrayList<>(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.FORELDER)
                                .relatertPersonsRolle(Rolle.BARN)
                                .nyRelatertPerson(PersonRequestDTO.builder()
                                        .alder(3)
                                        .build())
                                .build(),
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.FORELDER)
                                .relatertPersonsRolle(Rolle.BARN)
                                .nyRelatertPerson(PersonRequestDTO.builder()
                                        .alder(12)
                                        .build())
                                .build())))
                .build());

        var result = relasjonerAlderService.fixRelasjonerAlder(bestilling);

        assertThat(result.getFoedtFoer(), is(equalTo(NOW.minusYears(18 + 12))));
    }

    // --- barn uten aldersinfo returnerer null -> ingen foedtFoer ---

    @Test
    void shouldNotSetFoedtFoerWhenBarnHasNoAgeInfo() {

        var bestilling = buildBestilling(PersonDTO.builder()
                .forelderBarnRelasjon(new ArrayList<>(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.FORELDER)
                                .relatertPersonsRolle(Rolle.BARN)
                                .nyRelatertPerson(PersonRequestDTO.builder().build())
                                .build())))
                .build());

        var result = relasjonerAlderService.fixRelasjonerAlder(bestilling);

        assertThat(result.getFoedtFoer(), is(nullValue()));
        assertThat(result.getFoedtEtter(), is(nullValue()));
    }

    // --- forelder-relasjon med rolle BARN ignoreres i barn-scan ---

    @Test
    void shouldIgnoreRelasjonerWhereMinRolleIsBarn() {

        var bestilling = buildBestilling(PersonDTO.builder()
                .forelderBarnRelasjon(new ArrayList<>(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.BARN)
                                .relatertPersonsRolle(Rolle.FORELDER)
                                .nyRelatertPerson(PersonRequestDTO.builder()
                                        .alder(45)
                                        .build())
                                .build())))
                .build());

        var result = relasjonerAlderService.fixRelasjonerAlder(bestilling);

        assertThat(result.getFoedtFoer(), is(nullValue()));
    }

    // --- barn finnes men hovedperson har allerede alder ---

    @Test
    void shouldNotOverrideExistingAlderFromBarn() {

        stubClock();

        var bestilling = buildBestilling(PersonDTO.builder()
                .forelderBarnRelasjon(new ArrayList<>(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.FORELDER)
                                .relatertPersonsRolle(Rolle.BARN)
                                .nyRelatertPerson(PersonRequestDTO.builder()
                                        .alder(5)
                                        .build())
                                .build())))
                .build());
        bestilling.setAlder(40);

        var result = relasjonerAlderService.fixRelasjonerAlder(bestilling);

        assertThat(result.getFoedtFoer(), is(equalTo(NOW.minusYears(40))));
    }

    @Test
    void shouldNotOverrideExistingFoedtFoerFromBarn() {

        var foedtFoer = LocalDateTime.of(1980, 1, 1, 0, 0);

        var bestilling = buildBestilling(PersonDTO.builder()
                .forelderBarnRelasjon(new ArrayList<>(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.FORELDER)
                                .relatertPersonsRolle(Rolle.BARN)
                                .nyRelatertPerson(PersonRequestDTO.builder()
                                        .alder(5)
                                        .build())
                                .build())))
                .build());
        bestilling.setFoedtFoer(foedtFoer);

        var result = relasjonerAlderService.fixRelasjonerAlder(bestilling);

        assertThat(result.getFoedtFoer(), is(equalTo(foedtFoer)));
    }

    // --- partner alder propageres fra hovedperson ---

    @Test
    void shouldPropagateAlderToPartnerWhenPartnerHasNoAge() {

        stubClock();

        var bestilling = buildBestilling(PersonDTO.builder()
                .forelderBarnRelasjon(new ArrayList<>(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.FORELDER)
                                .relatertPersonsRolle(Rolle.BARN)
                                .nyRelatertPerson(PersonRequestDTO.builder()
                                        .alder(5)
                                        .build())
                                .build())))
                .sivilstand(new ArrayList<>(List.of(
                        SivilstandDTO.builder()
                                .type(Sivilstand.GIFT)
                                .build())))
                .build());

        var result = relasjonerAlderService.fixRelasjonerAlder(bestilling);

        var partner = result.getPerson().getSivilstand().getFirst().getNyRelatertPerson();
        assertThat(partner, is(notNullValue()));
        assertThat(partner.getFoedtFoer(), is(equalTo(result.getFoedtFoer())));
        assertThat(partner.getFoedtEtter(), is(equalTo(result.getFoedtEtter())));
    }

    @Test
    void shouldPropagateAlderToSamboerPartner() {

        stubClock();

        var bestilling = buildBestilling(PersonDTO.builder()
                .forelderBarnRelasjon(new ArrayList<>(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.FORELDER)
                                .relatertPersonsRolle(Rolle.BARN)
                                .nyRelatertPerson(PersonRequestDTO.builder()
                                        .alder(5)
                                        .build())
                                .build())))
                .sivilstand(new ArrayList<>(List.of(
                        SivilstandDTO.builder()
                                .type(Sivilstand.SAMBOER)
                                .build())))
                .build());

        var result = relasjonerAlderService.fixRelasjonerAlder(bestilling);

        var partner = result.getPerson().getSivilstand().getFirst().getNyRelatertPerson();
        assertThat(partner, is(notNullValue()));
        assertThat(partner.getFoedtFoer(), is(equalTo(result.getFoedtFoer())));
    }

    @Test
    void shouldPropagateAlderToRegistrertPartner() {

        stubClock();

        var bestilling = buildBestilling(PersonDTO.builder()
                .forelderBarnRelasjon(new ArrayList<>(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.FORELDER)
                                .relatertPersonsRolle(Rolle.BARN)
                                .nyRelatertPerson(PersonRequestDTO.builder()
                                        .alder(5)
                                        .build())
                                .build())))
                .sivilstand(new ArrayList<>(List.of(
                        SivilstandDTO.builder()
                                .type(Sivilstand.REGISTRERT_PARTNER)
                                .build())))
                .build());

        var result = relasjonerAlderService.fixRelasjonerAlder(bestilling);

        var partner = result.getPerson().getSivilstand().getFirst().getNyRelatertPerson();
        assertThat(partner, is(notNullValue()));
        assertThat(partner.getFoedtFoer(), is(equalTo(result.getFoedtFoer())));
    }

    // --- partner med eksisterende alder beholdes ---

    @Test
    void shouldNotOverridePartnerWithExistingAlder() {

        stubClock();

        var bestilling = buildBestilling(PersonDTO.builder()
                .forelderBarnRelasjon(new ArrayList<>(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.FORELDER)
                                .relatertPersonsRolle(Rolle.BARN)
                                .nyRelatertPerson(PersonRequestDTO.builder()
                                        .alder(5)
                                        .build())
                                .build())))
                .sivilstand(new ArrayList<>(List.of(
                        SivilstandDTO.builder()
                                .type(Sivilstand.GIFT)
                                .nyRelatertPerson(PersonRequestDTO.builder()
                                        .alder(35)
                                        .build())
                                .build())))
                .build());

        var result = relasjonerAlderService.fixRelasjonerAlder(bestilling);

        assertThat(result.getPerson().getSivilstand().getFirst().getNyRelatertPerson().getAlder(),
                is(equalTo(35)));
    }

    @Test
    void shouldNotOverridePartnerWithExistingIdent() {

        stubClock();

        var partnerIdent = "01016512345";

        var bestilling = buildBestilling(PersonDTO.builder()
                .forelderBarnRelasjon(new ArrayList<>(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.FORELDER)
                                .relatertPersonsRolle(Rolle.BARN)
                                .nyRelatertPerson(PersonRequestDTO.builder()
                                        .alder(5)
                                        .build())
                                .build())))
                .sivilstand(new ArrayList<>(List.of(
                        SivilstandDTO.builder()
                                .type(Sivilstand.GIFT)
                                .relatertVedSivilstand(partnerIdent)
                                .build())))
                .build());

        var result = relasjonerAlderService.fixRelasjonerAlder(bestilling);

        assertThat(result.getPerson().getSivilstand().getFirst().getNyRelatertPerson(), is(nullValue()));
    }

    // --- partner uten giftOrSamboer ignoreres ---

    @Test
    void shouldNotPropagateAlderToUgiftSivilstand() {

        stubClock();

        var bestilling = buildBestilling(PersonDTO.builder()
                .forelderBarnRelasjon(new ArrayList<>(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.FORELDER)
                                .relatertPersonsRolle(Rolle.BARN)
                                .nyRelatertPerson(PersonRequestDTO.builder()
                                        .alder(5)
                                        .build())
                                .build())))
                .sivilstand(new ArrayList<>(List.of(
                        SivilstandDTO.builder()
                                .type(Sivilstand.UGIFT)
                                .build())))
                .build());

        var result = relasjonerAlderService.fixRelasjonerAlder(bestilling);

        assertThat(result.getPerson().getSivilstand().getFirst().getNyRelatertPerson(), is(nullValue()));
    }

    @Test
    void shouldNotPropagateAlderToSkiltSivilstand() {

        stubClock();

        var bestilling = buildBestilling(PersonDTO.builder()
                .forelderBarnRelasjon(new ArrayList<>(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.FORELDER)
                                .relatertPersonsRolle(Rolle.BARN)
                                .nyRelatertPerson(PersonRequestDTO.builder()
                                        .alder(5)
                                        .build())
                                .build())))
                .sivilstand(new ArrayList<>(List.of(
                        SivilstandDTO.builder()
                                .type(Sivilstand.SKILT)
                                .build())))
                .build());

        var result = relasjonerAlderService.fixRelasjonerAlder(bestilling);

        assertThat(result.getPerson().getSivilstand().getFirst().getNyRelatertPerson(), is(nullValue()));
    }

    // --- fixForeldre ---

    @Test
    void shouldSetForelderAlderBasedOnHovedperson() {

        var bestilling = buildBestilling(PersonDTO.builder()
                .forelderBarnRelasjon(new ArrayList<>(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.BARN)
                                .relatertPersonsRolle(Rolle.FORELDER)
                                .build())))
                .build());
        bestilling.setFoedtEtter(NOW.minusYears(20));
        bestilling.setFoedtFoer(NOW.minusYears(19));

        var result = relasjonerAlderService.fixRelasjonerAlder(bestilling);

        var forelder = result.getPerson().getForelderBarnRelasjon().getFirst().getNyRelatertPerson();
        assertThat(forelder, is(notNullValue()));
        assertThat(forelder.getFoedtFoer(), is(equalTo(NOW.minusYears(20).minusYears(18))));
        assertThat(forelder.getFoedtEtter(), is(equalTo(NOW.minusYears(20).minusYears(18).minusYears(18))));
    }

    @Test
    void shouldNotSetForelderAlderWhenHovedpersonHasNoAlder() {

        var bestilling = buildBestilling(PersonDTO.builder()
                .forelderBarnRelasjon(new ArrayList<>(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.BARN)
                                .relatertPersonsRolle(Rolle.FORELDER)
                                .build())))
                .build());

        var result = relasjonerAlderService.fixRelasjonerAlder(bestilling);

        assertThat(result.getPerson().getForelderBarnRelasjon().getFirst().getNyRelatertPerson(),
                is(nullValue()));
    }

    @Test
    void shouldNotOverrideForelderWithExistingIdent() {

        stubClock();

        var forelderIdent = "01016012345";

        var bestilling = buildBestilling(PersonDTO.builder()
                .forelderBarnRelasjon(new ArrayList<>(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.BARN)
                                .relatertPersonsRolle(Rolle.FORELDER)
                                .relatertPerson(forelderIdent)
                                .build())))
                .build());
        bestilling.setFoedtEtter(NOW.minusYears(20));
        bestilling.setFoedtFoer(NOW.minusYears(19));

        var result = relasjonerAlderService.fixRelasjonerAlder(bestilling);

        assertThat(result.getPerson().getForelderBarnRelasjon().getFirst().getNyRelatertPerson(),
                is(nullValue()));
    }

    @Test
    void shouldNotOverrideForelderWithExistingAlder() {

        var bestilling = buildBestilling(PersonDTO.builder()
                .forelderBarnRelasjon(new ArrayList<>(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.BARN)
                                .relatertPersonsRolle(Rolle.FORELDER)
                                .nyRelatertPerson(PersonRequestDTO.builder()
                                        .alder(50)
                                        .build())
                                .build())))
                .build());
        bestilling.setFoedtEtter(NOW.minusYears(20));

        var result = relasjonerAlderService.fixRelasjonerAlder(bestilling);

        assertThat(result.getPerson().getForelderBarnRelasjon().getFirst().getNyRelatertPerson().getAlder(),
                is(equalTo(50)));
    }

    @Test
    void shouldNotOverrideForelderWithRelatertBiPerson() {

        stubClock();

        var bestilling = buildBestilling(PersonDTO.builder()
                .forelderBarnRelasjon(new ArrayList<>(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.BARN)
                                .relatertPersonsRolle(Rolle.FORELDER)
                                .relatertPersonUtenFolkeregisteridentifikator(
                                        RelatertBiPersonDTO.builder()
                                                .foedselsdato(NOW.minusYears(50))
                                                .build())
                                .build())))
                .build());
        bestilling.setFoedtEtter(NOW.minusYears(20));

        var result = relasjonerAlderService.fixRelasjonerAlder(bestilling);

        assertThat(result.getPerson().getForelderBarnRelasjon().getFirst().getNyRelatertPerson(),
                is(nullValue()));
    }

    @Test
    void shouldSetForelderAlderForMultipleForeldre() {

        var bestilling = buildBestilling(PersonDTO.builder()
                .forelderBarnRelasjon(new ArrayList<>(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.BARN)
                                .relatertPersonsRolle(Rolle.MOR)
                                .build(),
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.BARN)
                                .relatertPersonsRolle(Rolle.FAR)
                                .build())))
                .build());
        bestilling.setFoedtEtter(NOW.minusYears(20));
        bestilling.setFoedtFoer(NOW.minusYears(19));

        var result = relasjonerAlderService.fixRelasjonerAlder(bestilling);

        assertThat(result.getPerson().getForelderBarnRelasjon().get(0).getNyRelatertPerson(), is(notNullValue()));
        assertThat(result.getPerson().getForelderBarnRelasjon().get(1).getNyRelatertPerson(), is(notNullValue()));
    }

    // --- barn med nyRelatertPerson: foedtFoer only ---

    @Test
    void shouldCalculateBarnAlderFromFoedtFoerOnly() {

        stubClock();

        var bestilling = buildBestilling(PersonDTO.builder()
                .forelderBarnRelasjon(new ArrayList<>(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.FORELDER)
                                .relatertPersonsRolle(Rolle.BARN)
                                .nyRelatertPerson(PersonRequestDTO.builder()
                                        .foedtFoer(NOW.minusYears(7))
                                        .build())
                                .build())))
                .build());

        var result = relasjonerAlderService.fixRelasjonerAlder(bestilling);

        assertThat(result.getFoedtFoer(), is(notNullValue()));
    }

    // --- barn med nyRelatertPerson: foedtEtter only ---

    @Test
    void shouldCalculateBarnAlderFromFoedtEtterOnly() {

        stubClock();

        var bestilling = buildBestilling(PersonDTO.builder()
                .forelderBarnRelasjon(new ArrayList<>(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.FORELDER)
                                .relatertPersonsRolle(Rolle.BARN)
                                .nyRelatertPerson(PersonRequestDTO.builder()
                                        .foedtEtter(NOW.minusYears(10))
                                        .build())
                                .build())))
                .build());

        var result = relasjonerAlderService.fixRelasjonerAlder(bestilling);

        assertThat(result.getFoedtFoer(), is(notNullValue()));
    }

    // --- barn med nyRelatertPerson: both foedtFoer and foedtEtter ---

    @Test
    void shouldCalculateBarnAlderFromBothFoedtRange() {

        stubClock();

        var bestilling = buildBestilling(PersonDTO.builder()
                .forelderBarnRelasjon(new ArrayList<>(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.FORELDER)
                                .relatertPersonsRolle(Rolle.BARN)
                                .nyRelatertPerson(PersonRequestDTO.builder()
                                        .foedtEtter(NOW.minusYears(10))
                                        .foedtFoer(NOW.minusYears(5))
                                        .build())
                                .build())))
                .build());

        var result = relasjonerAlderService.fixRelasjonerAlder(bestilling);

        assertThat(result.getFoedtFoer(), is(notNullValue()));
    }

    // --- relasjon uten nyRelatertPerson eller ident returnerer null ---

    @Test
    void shouldHandleForeldreRelasjonWithNoRelatedPersonInfo() {

        var bestilling = buildBestilling(PersonDTO.builder()
                .forelderBarnRelasjon(new ArrayList<>(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.FORELDER)
                                .relatertPersonsRolle(Rolle.BARN)
                                .build())))
                .build());

        var result = relasjonerAlderService.fixRelasjonerAlder(bestilling);

        assertThat(result.getFoedtFoer(), is(nullValue()));
        assertThat(result.getFoedtEtter(), is(nullValue()));
    }

    // --- ingen relasjoner ---

    @Test
    void shouldHandleEmptyRelasjoner() {

        var bestilling = buildBestilling(PersonDTO.builder().build());

        var result = relasjonerAlderService.fixRelasjonerAlder(bestilling);

        assertThat(result, is(notNullValue()));
        assertThat(result.getFoedtFoer(), is(nullValue()));
    }

    // --- helhetlig scenario ---

    @Test
    void shouldHandleCompleteScenarioWithBarnPartnerAndForeldre() {

        stubClock();

        var bestilling = buildBestilling(PersonDTO.builder()
                .forelderBarnRelasjon(new ArrayList<>(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.FORELDER)
                                .relatertPersonsRolle(Rolle.BARN)
                                .nyRelatertPerson(PersonRequestDTO.builder()
                                        .alder(8)
                                        .build())
                                .build(),
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.BARN)
                                .relatertPersonsRolle(Rolle.MOR)
                                .build(),
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.BARN)
                                .relatertPersonsRolle(Rolle.FAR)
                                .build())))
                .sivilstand(new ArrayList<>(List.of(
                        SivilstandDTO.builder()
                                .type(Sivilstand.GIFT)
                                .build())))
                .build());

        var result = relasjonerAlderService.fixRelasjonerAlder(bestilling);

        assertThat(result.getFoedtFoer(), is(equalTo(NOW.minusYears(26))));
        assertThat(result.getFoedtEtter(), is(equalTo(NOW.minusYears(44))));

        var partner = result.getPerson().getSivilstand().getFirst().getNyRelatertPerson();
        assertThat(partner, is(notNullValue()));
        assertThat(partner.getFoedtFoer(), is(equalTo(result.getFoedtFoer())));
        assertThat(partner.getFoedtEtter(), is(equalTo(result.getFoedtEtter())));

        var mor = result.getPerson().getForelderBarnRelasjon().get(1).getNyRelatertPerson();
        assertThat(mor, is(notNullValue()));
        assertThat(mor.getFoedtFoer(), is(equalTo(result.getFoedtEtter().minusYears(18))));

        var far = result.getPerson().getForelderBarnRelasjon().get(2).getNyRelatertPerson();
        assertThat(far, is(notNullValue()));
        assertThat(far.getFoedtFoer(), is(equalTo(result.getFoedtEtter().minusYears(18))));
    }

    // --- helpers ---

    private void stubClock() {
        when(clock.instant()).thenReturn(NOW.atZone(ZONE).toInstant());
        when(clock.getZone()).thenReturn(ZONE);
    }

    private BestillingRequestDTO buildBestilling(PersonDTO person) {
        return BestillingRequestDTO.builder()
                .person(person)
                .build();
    }
}