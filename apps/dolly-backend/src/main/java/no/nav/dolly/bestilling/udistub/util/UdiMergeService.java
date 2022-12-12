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

@Slf4j
@Service
@RequiredArgsConstructor
public class UdiMergeService {

    private final MapperFacade mapperFacade;

    public UdiPersonWrapper merge(RsUdiPerson nyUdiPerson, UdiPersonResponse eksisterendeUdiPerson,
                                 PdlPersonBolk.PersonBolk personBolk) {

        log.info("UdiStub Hentet UdiPerson {}", eksisterendeUdiPerson);
        UdiPerson udiPerson = mapperFacade.map(nyUdiPerson, UdiPerson.class);

        udiPerson.setIdent(personBolk.getIdent());
        udiPerson.setNavn(mapperFacade.map(personBolk.getPerson().getNavn().stream()
                .findFirst().orElse(new PdlPerson.Navn()), UdiPersonNavn.class));
        udiPerson.setFoedselsDato(personBolk.getPerson().getFoedsel().stream().map(PdlPerson.Foedsel::getFoedselsdato)
                .findFirst().orElse(null));

        udiPerson.setAliaser(personBolk.getPerson().getFolkeregisteridentifikator().stream()
                .filter(PdlPerson.Folkeregisteridentifikator::isOpphoert)
                .map(PdlPerson.Folkeregisteridentifikator::getIdentifikasjonsnummer)
                .filter(ident -> HttpStatus.NOT_FOUND.equals(eksisterendeUdiPerson.getStatus()) ||
                        eksisterendeUdiPerson.getPerson().getAliaser().stream()
                                .map(UdiAlias::getFnr)
                                .noneMatch(alias -> alias.equals(ident)))
                .map(alias -> UdiAlias.builder()
                        .fnr(alias)
                        .navn(mapperFacade.map(personBolk.getPerson().getNavn().stream()
                                .findFirst().orElse(new PdlPerson.Navn()), UdiPersonNavn.class))
                        .build())
                .toList());

        return UdiPersonWrapper.builder()
                .udiPerson(udiPerson)
                .status(HttpStatus.NOT_FOUND.equals(eksisterendeUdiPerson.getStatus()) ? Status.NEW : Status.UPDATE)
                .build();
    }
}
