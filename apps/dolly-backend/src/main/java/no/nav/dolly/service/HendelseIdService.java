package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.repository.BestillingProgressRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class HendelseIdService {

    private final BestillingProgressRepository bestillingProgressRepository;

    public Mono<String> getHendleserForIdent(String ident) {

        return bestillingProgressRepository.findByIdent(ident)
                .switchIfEmpty(Mono.error(new RuntimeException("Ident %s ikke funnet".formatted(ident))))
                .sort(Comparator.comparing(BestillingProgress::getBestillingId).reversed())
                .next()
                .map(BestillingProgress::getPdlForvalterStatus);
    }
}
