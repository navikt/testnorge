package no.nav.pdl.forvalter.utils;

import no.nav.testnav.libs.dto.pdlforvalter.v1.BestillingRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class RelasjonerAlderTest {

    @Test
    void personAlderBarn23Forelder41() {

        var bestilling = BestillingRequestDTO.builder()
                .person(PersonDTO.builder()
                        .forelderBarnRelasjon(List.of(
                                ForelderBarnRelasjonDTO.builder()
                                        .minRolleForPerson(ForelderBarnRelasjonDTO.Rolle.FORELDER)
                                        .nyRelatertPerson(PersonRequestDTO.builder()
                                                .foedtFoer(LocalDateTime.of(LocalDate.now().getYear() - 23,
                                                        LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth(), 1, 1))
                                                .build())
                                        .build()
                        ))
                        .sivilstand(List.of(
                                SivilstandDTO.builder()
                                        .type(SivilstandDTO.Sivilstand.GIFT)
                                        .nyRelatertPerson(PersonRequestDTO.builder()
                                                .foedtFoer(LocalDateTime.of(LocalDate.now().getYear() - 30,
                                                        LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth(), 1, 1))
                                                .build())
                                        .build()
                        ))
                        .build())
                .build();
        var oppdatertBestilling = RelasjonerAlder.fixRelasjonerAlder(bestilling);
        assertThat(oppdatertBestilling.getAlder(), is(equalTo(18 + 23)));
        assertThat(oppdatertBestilling.getPerson().getSivilstand().get(0).getNyRelatertPerson().getAlder(), is(equalTo(18 + 23)));
    }
}