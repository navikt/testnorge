package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.KodeverkConsumer;
import no.nav.pdl.forvalter.dto.PersonUtenIdentifikatorRequest;
import no.nav.pdl.forvalter.utils.KjoennUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonnavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelatertBiPersonDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import static java.lang.Boolean.TRUE;
import static java.util.Objects.isNull;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle.BARN;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreatePersonUtenIdentifikatorService {

    private final KodeverkConsumer kodeverkConsumer;
    private final MapperFacade mapperFacade;
    private final NavnService navnService;
    private final SecureRandom secureRandom = new SecureRandom();

    public Mono<RelatertBiPersonDTO> execute(PersonUtenIdentifikatorRequest request) {

        var relatertPerson = mapperFacade.map(request, RelatertBiPersonDTO.class);


        if (isNull(request.getNavn())) {

            request.setNavn(new PersonnavnDTO());
        }

        return Mono.just(TRUE)
                .flatMap(type -> {
                    if (isBlank(request.getNavn().getEtternavn()) || isBlank(request.getNavn().getFornavn())) {

                        var navn = mapperFacade.map(request.getNavn(), NavnDTO.class);
                        return navnService.handle(navn)
                                .doOnNext(navn1 ->
                                        relatertPerson.setNavn(mapperFacade.map(navn1, PersonnavnDTO.class)))
                                .thenReturn(relatertPerson);
                    }
                    return Mono.just(relatertPerson);
                })
                .flatMap(type -> {

                    if (isBlank(request.getStatsborgerskap())) {
                        return (isNotBlank(request.getRelatertStatsborgerskap()) ?
                                Mono.just(request.getRelatertStatsborgerskap()) :
                                kodeverkConsumer.getTilfeldigLand())
                                .doOnNext(relatertPerson::setStatsborgerskap)
                                .thenReturn(relatertPerson);
                    }
                    return Mono.just(relatertPerson);
                })
                .doOnNext(type -> {

                    if (isNull(request.getKjoenn())) {
                        relatertPerson.setKjoenn(KjoennUtility.getKjoenn());
                    }

                    if (isNull(request.getFoedselsdato())) {
                        relatertPerson.setFoedselsdato(getFoedselsdato(request.getMinRolle()));
                    }
                });
    }

    private LocalDateTime getFoedselsdato(ForelderBarnRelasjonDTO.Rolle minRolle) {

        return minRolle == BARN ?
                LocalDateTime.now().minusYears(70).minusYears(secureRandom.nextInt(30)) :
                LocalDateTime.now().minusYears(secureRandom.nextInt(18));
    }
}
