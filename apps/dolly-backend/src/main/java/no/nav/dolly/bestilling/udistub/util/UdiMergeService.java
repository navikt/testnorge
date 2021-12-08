package no.nav.dolly.bestilling.udistub.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.bestilling.udistub.domain.RsAliasRequest;
import no.nav.dolly.bestilling.udistub.domain.UdiPerson;
import no.nav.dolly.bestilling.udistub.domain.UdiPerson.UdiAlias;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonResponse;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonWrapper;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonWrapper.Status;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiAlias;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiPerson;
import no.nav.dolly.domain.resultset.udistub.model.UdiPersonNavn;
import no.nav.dolly.service.DollyPersonCache;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class UdiMergeService {

    private final DollyPersonCache dollyPersonCache;
    private final TpsfService tpsfService;
    private final MapperFacade mapperFacade;

    public UdiPersonWrapper merge(RsUdiPerson nyUdiPerson, UdiPersonResponse eksisterendeUdiPerson,
            boolean isLeggTil, DollyPerson dollyPerson) {

        UdiPerson udiPerson = mapperFacade.map(nyUdiPerson, UdiPerson.class);

        if (isNull(eksisterendeUdiPerson)) {
            return appendAttributes(udiPerson, nyUdiPerson.getAliaser(), Status.NEW, dollyPerson);
        }

        return appendAttributes(udiPerson, isLeggTil ? nyUdiPerson.getAliaser() : emptyList(), Status.UPDATE, dollyPerson);
    }

    public List<UdiAlias> getAliaser(RsAliasRequest request, List<String> environments, DollyPerson dollyPerson) {

        if (dollyPerson.isPdlMaster() && !dollyPerson.getIdenthistorikk().isEmpty()) {
            return dollyPerson.getIdenthistorikk().stream()
                   .map(historikk -> UdiAlias.builder().fnr(historikk).build())
                    .collect(Collectors.toList());
        }
        else if (dollyPerson.isTpsfMaster() && nonNull(request)) {
            request.setEnvironments(environments);
            var response = tpsfService.createAliases(request);
            return nonNull(response) ?
                    mapperFacade.mapAsList(response.getAliaser(), UdiAlias.class) :
                    emptyList();
        } else {
            return emptyList();
        }
    }

    private UdiPersonWrapper appendAttributes(UdiPerson udiPerson, List<RsUdiAlias> aliaser, Status status, DollyPerson dollyPerson) {

        dollyPersonCache.fetchIfEmpty(dollyPerson);

        udiPerson.setIdent(dollyPerson.getHovedperson());
        udiPerson.setNavn(mapperFacade.map(dollyPerson.getPerson(dollyPerson.getHovedperson()), UdiPersonNavn.class));
        udiPerson.setFoedselsDato(dollyPerson.getPerson(dollyPerson.getHovedperson()).getFoedselsdato().toLocalDate());

        return UdiPersonWrapper.builder()
                .udiPerson(udiPerson)
                .status(status)
                .aliasRequest(!aliaser.isEmpty() ? RsAliasRequest.builder()
                        .ident(dollyPerson.getHovedperson())
                        .aliaser(mapperFacade.mapAsList(aliaser, RsAliasRequest.AliasSpesification.class))
                        .build() : null)
                .build();
    }
}
