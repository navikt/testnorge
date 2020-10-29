package no.nav.registre.testnorge.originalpopulasjon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.originalpopulasjon.consumer.IdentPoolConsumer;
import no.nav.registre.testnorge.originalpopulasjon.consumer.StatiskDataForvalterConsumer;
import no.nav.registre.testnorge.originalpopulasjon.domain.Alderskategori;

@Service
@Slf4j
@RequiredArgsConstructor
public class IdentService {

    private final StatistikkService statistikkService;
    private final IdentPoolConsumer identPoolConsumer;
    private final StatiskDataForvalterConsumer statiskDataForvalterConsumer;

    public List<String> getIdenter(Integer antall) {
        List<Alderskategori> alderskategorier = statistikkService.getAlderskategorier(antall);

        return alderskategorier.stream()
                .map(this::getIdenterByAlderskategori)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<String> getIdenterByAlderskategori(Alderskategori alderskategori) {
        if (alderskategori.getAntall() < 1) return Collections.emptyList();

        int totaltAntall = alderskategori.getAntall();
        List<String> identer = new ArrayList<>();

        while (identer.size() < totaltAntall) {
            Integer antallNye = totaltAntall - identer.size();
            List<String> nyeIdenter = identPoolConsumer.getIdenter(antallNye, alderskategori.getAldersspenn());
            Set<String> alleredeBrukteIdenter = statiskDataForvalterConsumer.getPersoner(nyeIdenter);

            identer.addAll(
                    alleredeBrukteIdenter.isEmpty()
                            ? nyeIdenter
                            : fjerneIdenterIBruk(nyeIdenter, alleredeBrukteIdenter)
            );
        }
        return identer;
    }

    private List<String> fjerneIdenterIBruk(List<String> nyeIdenter, Set<String> alleredeBrukteIdenter) {
        return nyeIdenter
                .stream()
                .filter(ident -> !alleredeBrukteIdenter.contains(ident))
                .collect(Collectors.toList());
    }
}
