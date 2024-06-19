package no.nav.pdl.forvalter.service;

import no.nav.testnav.libs.data.pdlforvalter.v1.BestillingRequestDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.FoedselDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.SivilstandDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class RelasjonerAlderServiceTest {

    private final static LocalDate LOCAL_DATE = LocalDate.of(2023, 6, 8);
    @Mock
    private Clock clock;

    private Clock fixedClock;

    @InjectMocks
    private RelasjonerAlderService relasjonerAlderService;

    @BeforeEach
    void setup() {
        fixedClock = Clock.fixed(LOCAL_DATE.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
    }

    @Test
    @Disabled("Denne feiler av og til og trenger oppdatering")
    void personAlderBarn23Forelder41() {

        doReturn(fixedClock.instant()).when(clock).instant();
        doReturn(fixedClock.getZone()).when(clock).getZone();

        var bestilling = BestillingRequestDTO.builder()
                .person(PersonDTO.builder()
                        .forelderBarnRelasjon(List.of(
                                ForelderBarnRelasjonDTO.builder()
                                        .minRolleForPerson(ForelderBarnRelasjonDTO.Rolle.FORELDER)
                                        .nyRelatertPerson(PersonRequestDTO.builder()
                                                .foedtFoer(LocalDateTime.of(LocalDate.now(clock).getYear() - 23,
                                                        LocalDate.now(clock).getMonthValue(), LocalDate.now(clock).getDayOfMonth(), 1, 1))
                                                .build())
                                        .build()
                        ))
                        .sivilstand(List.of(
                                SivilstandDTO.builder()
                                        .type(SivilstandDTO.Sivilstand.GIFT)
                                        .build()
                        ))
                        .build())
                .build();
        var oppdatertBestilling = relasjonerAlderService.fixRelasjonerAlder(bestilling);
        assertThat(oppdatertBestilling.getFoedtFoer().toLocalDate(),
                is(equalTo(LocalDate.now(clock).minusYears(18 + 23))));
        assertThat(oppdatertBestilling.getPerson().getSivilstand().getFirst().getNyRelatertPerson().getFoedtFoer().toLocalDate(),
                is(equalTo(LocalDate.now(clock).minusYears(18 + 23))));
    }

    @Test
    @Disabled("Denne feiler av og til og trenger oppdatering")
    void personAlderBarn23Besteforeldre() {

        doReturn(fixedClock.instant()).when(clock).instant();
        doReturn(fixedClock.getZone()).when(clock).getZone();

        var bestilling = BestillingRequestDTO.builder()
                .person(PersonDTO.builder()
                        .forelderBarnRelasjon(List.of(
                                ForelderBarnRelasjonDTO.builder()
                                        .minRolleForPerson(ForelderBarnRelasjonDTO.Rolle.FORELDER)
                                        .nyRelatertPerson(PersonRequestDTO.builder()
                                                .foedtFoer(LocalDateTime.of(LocalDate.now(clock).getYear() - 23,
                                                        LocalDate.now(clock).getMonthValue(), LocalDate.now(clock).getDayOfMonth(), 1, 1))
                                                .build())
                                        .build(),
                                ForelderBarnRelasjonDTO.builder()
                                        .minRolleForPerson(ForelderBarnRelasjonDTO.Rolle.BARN)
                                        .build()
                        ))
                        .build())
                .build();
        var oppdatertBestilling = relasjonerAlderService.fixRelasjonerAlder(bestilling);
        assertThat(oppdatertBestilling.getFoedtFoer().toLocalDate(),
                is(equalTo(LocalDate.now(clock).minusYears(18 + 23))));
        assertThat(oppdatertBestilling.getPerson().getForelderBarnRelasjon().get(1).getNyRelatertPerson().getFoedtFoer().toLocalDate(),
                is(equalTo(LocalDate.now(clock).minusYears(18 + 23 + 36))));
    }

    @Test
    void personMedFoedselsdag() {

        var bestilling = BestillingRequestDTO.builder()
                .person(PersonDTO.builder()
                        .foedsel(List.of(FoedselDTO.builder()
                                .foedselsdato(LocalDateTime.of(1985, 1, 1, 1, 1))
                                .build()))
                        .build())
                .build();
        var oppdatertBestilling = relasjonerAlderService.fixRelasjonerAlder(bestilling);
        assertThat(oppdatertBestilling.getFoedtEtter(), is(equalTo(LocalDateTime.of(1984, 12, 31, 1, 1))));
        assertThat(oppdatertBestilling.getFoedtFoer(), is(equalTo(LocalDateTime.of(1985, 1, 2, 1, 1))));
    }

    @Test
    void personMedFoedselsaar() {

        var bestilling = BestillingRequestDTO.builder()
                .person(PersonDTO.builder()
                        .foedsel(List.of(FoedselDTO.builder()
                                .foedselsaar(1985)
                                .build()))
                        .build())
                .build();
        var oppdatertBestilling = relasjonerAlderService.fixRelasjonerAlder(bestilling);
        assertThat(oppdatertBestilling.getFoedtEtter(), is(equalTo(LocalDateTime.of(1984, 12, 31, 23, 59))));
        assertThat(oppdatertBestilling.getFoedtFoer(), is(equalTo(LocalDateTime.of(1986, 1, 1, 0, 0))));
    }
}