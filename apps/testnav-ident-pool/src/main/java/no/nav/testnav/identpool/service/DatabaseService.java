package no.nav.testnav.identpool.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.identpool.domain.Ident;
import no.nav.testnav.identpool.domain.Rekvireringsstatus;
import no.nav.testnav.identpool.providers.v1.support.HentIdenterRequest;
import no.nav.testnav.identpool.repository.IdentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
@RequiredArgsConstructor
public class DatabaseService {

    private final IdentRepository identRepository;
    private final MapperFacade mapperFacade;

    public Set<Ident> hentLedigeIdenterFraDatabase(HentIdenterRequest request) {
        Set<Ident> identEntities = new HashSet<>();

        HentIdenterRequest availableIdentsRequest = mapperFacade.map(request, HentIdenterRequest.class);

        var firstPage = findPage(availableIdentsRequest, Rekvireringsstatus.LEDIG, 0);
        var pageCache = new HashMap<Integer, Page<Ident>>();
        pageCache.put(0, firstPage);

        int totalPages = firstPage.getTotalPages();
        if (totalPages > 0) {
            List<String> usedIdents = new ArrayList<>();
            SecureRandom rand = new SecureRandom();
            for (var i = 0; i < request.getAntall(); i++) {
                var randomPageNumber = rand.nextInt(totalPages);
                pageCache.computeIfAbsent(randomPageNumber, k ->
                        findPage(availableIdentsRequest, Rekvireringsstatus.LEDIG, randomPageNumber));

                List<Ident> content = pageCache.get(randomPageNumber).getContent();
                for (Ident ident : content) {
                    if (!usedIdents.contains(ident.getPersonidentifikator())) {
                        usedIdents.add(ident.getPersonidentifikator());
                        identEntities.add(ident);
                        break;
                    }
                }
            }
        }
        return identEntities;
    }

    private Page<Ident> findPage(HentIdenterRequest request, Rekvireringsstatus rekvireringsstatus, int page) {

        return identRepository.findAll(
                rekvireringsstatus, request.getIdenttype(), request.getKjoenn(), request.getFoedtFoer(),
                request.getFoedtEtter(), isTrue(request.getSyntetisk()), PageRequest.of(page, request.getAntall()));
    }

    private int getAntall(HentIdenterRequest request,
                          Rekvireringsstatus rekvireringsstatus) {

        return nonNull(request.getKjoenn()) ?

                identRepository.countAllByRekvireringsstatusAndIdenttypeAndSyntetiskAndKjoennAndFoedselsdatoBetween(
                        rekvireringsstatus, request.getIdenttype(),
                        isTrue(request.getSyntetisk()), request.getKjoenn(),
                        request.getFoedtEtter(), request.getFoedtFoer()) :

                identRepository.countAllByRekvireringsstatusAndIdenttypeAndSyntetiskAndFoedselsdatoBetween(
                        rekvireringsstatus, request.getIdenttype(),
                        isTrue(request.getSyntetisk()),
                                request.getFoedtEtter(), request.getFoedtFoer());
    }

    private Page<Ident> findPage(HentIdenterRequest request,
                                 Rekvireringsstatus rekvireringsstatus,
                                 Pageable page) {

        return nonNull(request.getKjoenn()) ?

                identRepository.findAllByRekvireringsstatusAndIdenttypeAndSyntetiskAndKjoennAndFoedselsdatoBetween(
                        rekvireringsstatus, request.getIdenttype(),
                        isTrue(request.getSyntetisk()), request.getKjoenn(),
                        request.getFoedtEtter(), request.getFoedtFoer(), page
                ) :

                identRepository.findAllByRekvireringsstatusAndIdenttypeAndSyntetiskAndFoedselsdatoBetween(
                        rekvireringsstatus, request.getIdenttype(),
                        isTrue(request.getSyntetisk()),
                        request.getFoedtEtter(), request.getFoedtFoer(), page
                );
    }
}
