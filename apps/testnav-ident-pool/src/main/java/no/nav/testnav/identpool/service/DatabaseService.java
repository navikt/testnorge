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
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
@RequiredArgsConstructor
public class DatabaseService {

    private static final Random RANDOM = new SecureRandom();

    private final IdentRepository identRepository;
    private final MapperFacade mapperFacade;

    public Set<Ident> hentLedigeIdenterFraDatabase(HentIdenterRequest request) {

        var availableIdentsRequest = mapperFacade.map(request, HentIdenterRequest.class);

        var antall = getAntall(availableIdentsRequest);

        if (antall == 0) {
            return new HashSet<>();
        }

        if (antall > request.getAntall()) {
            var resultat = getPage(request, PageRequest.of(RANDOM.nextInt(antall/request.getAntall()), request.getAntall()));
            return new HashSet<>(resultat.getContent());
        }

        return new HashSet<>(
                getPage(request, PageRequest.of(0, request.getAntall()))
                .getContent());
    }

    private int getAntall(HentIdenterRequest request) {

        return nonNull(request.getKjoenn()) ?

                identRepository.countAllByRekvireringsstatusAndIdenttypeAndSyntetiskAndKjoennAndFoedselsdatoBetween(
                        Rekvireringsstatus.LEDIG, request.getIdenttype(),
                        isTrue(request.getSyntetisk()), request.getKjoenn(),
                        request.getFoedtEtter(), request.getFoedtFoer()) :

                identRepository.countAllByRekvireringsstatusAndIdenttypeAndSyntetiskAndFoedselsdatoBetween(
                        Rekvireringsstatus.LEDIG, request.getIdenttype(),
                        isTrue(request.getSyntetisk()),
                                request.getFoedtEtter(), request.getFoedtFoer());
    }

    private Page<Ident> getPage(HentIdenterRequest request, Pageable page) {

        return nonNull(request.getKjoenn()) ?

                identRepository.findAllByRekvireringsstatusAndIdenttypeAndSyntetiskAndKjoennAndFoedselsdatoBetween(
                        Rekvireringsstatus.LEDIG, request.getIdenttype(),
                        isTrue(request.getSyntetisk()), request.getKjoenn(),
                        request.getFoedtEtter(), request.getFoedtFoer(), page
                ) :

                identRepository.findAllByRekvireringsstatusAndIdenttypeAndSyntetiskAndFoedselsdatoBetween(
                        Rekvireringsstatus.LEDIG, request.getIdenttype(),
                        isTrue(request.getSyntetisk()),
                        request.getFoedtEtter(), request.getFoedtFoer(), page
                );
    }
}
