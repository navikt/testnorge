package no.nav.dolly.bestilling.udistub.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.udistub.domain.UdiPerson;
import no.nav.dolly.bestilling.udistub.domain.UdiPerson.UdiAlias;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonResponse;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonWrapper;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonWrapper.Status;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiPerson;
import no.nav.dolly.domain.resultset.udistub.model.UdiPersonNavn;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Collections.emptyList;

@Slf4j
@Service
@RequiredArgsConstructor
public class UdiMergeService {

    private final MapperFacade mapperFacade;

    public UdiPersonWrapper merge(RsUdiPerson nyUdiPerson, UdiPersonResponse eksisterendeUdiPerson,
                                  boolean isLeggTil, PdlPersonBolk.PersonBolk personBolk,
                                  List<PdlPersonBolk.PersonBolk> aliaser) {

        UdiPerson udiPerson = mapperFacade.map(nyUdiPerson, UdiPerson.class);

        return (HttpStatus.NOT_FOUND.equals(eksisterendeUdiPerson.getStatus())) ?
                appendAttributes(udiPerson, aliaser, Status.NEW, personBolk) :
                appendAttributes(udiPerson, isLeggTil ? aliaser : emptyList(), Status.UPDATE, personBolk);
    }

    private UdiPersonWrapper appendAttributes(UdiPerson udiPerson, List<PdlPersonBolk.PersonBolk> aliaser,
                                              Status status, PdlPersonBolk.PersonBolk personBolk) {

        udiPerson.setIdent(personBolk.getIdent());
        udiPerson.setNavn(mapperFacade.map(personBolk.getPerson().getNavn().stream()
                .findFirst().orElse(new PdlPerson.Navn()), UdiPersonNavn.class));
        udiPerson.setFoedselsDato(personBolk.getPerson().getFoedsel().stream().map(PdlPerson.Foedsel::getFoedselsdato)
                .findFirst().orElse(null));

        udiPerson.setAliaser(aliaser.stream()
                .map(alias -> UdiAlias.builder()
                        .fnr(alias.getIdent())
                        .navn(mapperFacade.map(alias.getPerson().getNavn().stream()
                                .findFirst().orElse(new PdlPerson.Navn()), UdiPersonNavn.class))
                        .build())
                .toList());

        return UdiPersonWrapper.builder()
                .udiPerson(udiPerson)
                .status(status)
                .build();
    }
}
