package no.nav.dolly.service;

import static java.util.Objects.isNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;

@Service
@RequiredArgsConstructor
public class TpsfPersonCache {

    private final TpsfService tpsfService;

    public void fetchIfEmpty(TpsPerson tpsPerson, List<String> tpsfIdenter){

        AtomicBoolean notFound = new AtomicBoolean(false);
        tpsfIdenter.forEach(ident -> {
            if (isNull(tpsPerson.getPerson(ident))) {
                notFound.set(true);
            }
        });
        if (notFound.get()) {
            tpsPerson.setPersondetaljer(tpsfService.hentTestpersoner(tpsfIdenter));
        }
    }
}
