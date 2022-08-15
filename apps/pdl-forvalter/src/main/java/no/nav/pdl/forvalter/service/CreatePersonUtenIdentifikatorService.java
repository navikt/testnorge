package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.GeografiskeKodeverkConsumer;
import no.nav.pdl.forvalter.dto.PersonUtenIdentifikatorRequest;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonnavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelatertBiPersonDTO;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle.BARN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO.Kjoenn.KVINNE;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO.Kjoenn.MANN;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreatePersonUtenIdentifikatorService {

    private final GeografiskeKodeverkConsumer geografiskeKodeverkConsumer;
    private final MapperFacade mapperFacade;
    private final NavnService navnService;
    private final SecureRandom secureRandom = new SecureRandom();

    public RelatertBiPersonDTO execute(PersonUtenIdentifikatorRequest request) {

        var relatertPerson = mapperFacade.map(request, RelatertBiPersonDTO.class);


        if (isNull(request.getNavn())) {

            request.setNavn(new PersonnavnDTO());
        }

        if (isBlank(request.getNavn().getEtternavn()) || isBlank(request.getNavn().getFornavn())) {

            var navn = mapperFacade.map(request.getNavn(), NavnDTO.class);
            navnService.handle(navn);
            relatertPerson.setNavn(mapperFacade.map(navn, PersonnavnDTO.class));
        }

        if (isBlank(request.getStatsborgerskap())) {

            relatertPerson.setStatsborgerskap(isNotBlank(request.getRelatertStatsborgerskap()) ?
                    request.getRelatertStatsborgerskap() :
                    geografiskeKodeverkConsumer.getTilfeldigLand());
        }

        if (isNull(request.getKjoenn())) {

            relatertPerson.setKjoenn(secureRandom.nextBoolean() ? MANN : KVINNE);
        }

        if (isNull(request.getFoedselsdato())) {

            relatertPerson.setFoedselsdato(getFoedselsdato(request.getMinRolle()));
        }

        return relatertPerson;
    }

    private LocalDateTime getFoedselsdato(ForelderBarnRelasjonDTO.Rolle minRolle) {

        return minRolle == BARN ?
                LocalDateTime.now().minusYears(70).minusYears(secureRandom.nextInt(30)) :
                LocalDateTime.now().minusYears(secureRandom.nextInt(18));
    }
}
