package no.nav.dolly.bestilling.udistub.util;

import static java.util.Objects.isNull;

import java.util.List;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.bestilling.udistub.domain.RsAliasRequest;
import no.nav.dolly.bestilling.udistub.domain.RsAliasResponse;
import no.nav.dolly.bestilling.udistub.domain.UdiPerson;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonResponse;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonWrapper;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonWrapper.Status;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiAlias;
import no.nav.dolly.domain.resultset.udistub.model.UdiPersonNavn;
import no.nav.dolly.service.TpsfPersonCache;

@Slf4j
@Service
@RequiredArgsConstructor
public class UdiMergeService {

    private final TpsfPersonCache tpsfPersonCache;
    private final TpsfService tpsfService;
    private final MapperFacade mapperFacade;

    public UdiPersonWrapper merge(RsDollyUtvidetBestilling bestilling, UdiPersonResponse eksisterendeUdiPerson,
            boolean isLeggTil, TpsPerson tpsPerson) {

        UdiPerson udiPerson = mapperFacade.map(bestilling.getUdistub(), UdiPerson.class);

        if (isNull(eksisterendeUdiPerson)) {
            return appendAttributes(udiPerson, bestilling, Status.NEW, tpsPerson);
        }
        return null;
    }

    private UdiPersonWrapper appendAttributes(UdiPerson udiPerson, RsDollyUtvidetBestilling bestilling,
            Status status, TpsPerson tpsPerson) {

        tpsfPersonCache.fetchIfEmpty(tpsPerson);

        udiPerson.setIdent(tpsPerson.getHovedperson());
        udiPerson.setNavn(mapperFacade.map(tpsPerson.getPerson(tpsPerson.getHovedperson()), UdiPersonNavn.class));
        udiPerson.setFoedselsDato(tpsPerson.getPerson(tpsPerson.getHovedperson()).getFoedselsdato().toLocalDate());

        if (Status.NEW == status || !bestilling.getUdistub().getAliaser().isEmpty())) {
            createAndSetAliases(udiPerson, bestilling, tpsPerson.getHovedperson());
        }

        return UdiPersonWrapper.builder()
                .udiPerson(udiPerson)
                .status(status)
                .build();
    }

    private void createAndSetAliases(UdiPerson person, RsDollyBestilling bestilling, String ident) {

        try {
            RsAliasResponse aliases = createAliases(ident, bestilling.getUdistub().getAliaser(), bestilling.getEnvironments());
            person.setAliaser(mapperFacade.mapAsList(aliases.getAliaser(), UdiPerson.UdiAlias.class));

        } catch (RuntimeException e) {
            log.error("Feilet å opprette aliaser i TPSF {}", e.getMessage(), e);
        }
    }

    private RsAliasResponse createAliases(String ident, List<RsUdiAlias> aliases, List<String> environments) {
        RsAliasRequest aliasRequest = RsAliasRequest.builder()
                .ident(ident)
                .aliaser(mapperFacade.mapAsList(aliases, RsAliasRequest.AliasSpesification.class))
                .environments(environments)
                .build();

        return tpsfService.createAliases(aliasRequest).getBody();
    }
}
