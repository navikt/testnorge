package no.nav.identpool.service.ny;

import static no.nav.identpool.domain.Rekvireringsstatus.LEDIG;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.identpool.domain.Rekvireringsstatus;
import no.nav.identpool.domain.Ident;
import no.nav.identpool.repository.IdentRepository;
import no.nav.identpool.rs.v1.support.HentIdenterRequest;

@Service
@RequiredArgsConstructor
public class DatabaseService {

    private final IdentRepository identRepository;
    private final MapperFacade mapperFacade;

    public Set<Ident> hentLedigeIdenterFraDatabase(HentIdenterRequest request) {
        Set<Ident> identEntities = new HashSet<>();

        HentIdenterRequest availableIdentsRequest = mapperFacade.map(request, HentIdenterRequest.class);

        Page<Ident> firstPage = findPage(availableIdentsRequest, LEDIG, 0);
        Map<Integer, Page<Ident>> pageCache = new HashMap<>();
        pageCache.put(0, firstPage);

        int totalPages = firstPage.getTotalPages();
        if (totalPages > 0) {
            List<String> usedIdents = new ArrayList<>();
            SecureRandom rand = new SecureRandom();
            for (int i = 0; i < request.getAntall(); i++) {
                int randomPageNumber = rand.nextInt(totalPages);
                if (!pageCache.containsKey(randomPageNumber)) {
                    pageCache.put(randomPageNumber, findPage(availableIdentsRequest, LEDIG, randomPageNumber));
                }

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
}
