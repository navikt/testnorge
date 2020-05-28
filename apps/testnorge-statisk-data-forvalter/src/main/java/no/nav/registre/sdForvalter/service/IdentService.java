package no.nav.registre.sdForvalter.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.sdForvalter.adapter.TpsIdenterAdapter;
import no.nav.registre.sdForvalter.consumer.rs.HodejegerenConsumer;
import no.nav.registre.sdForvalter.consumer.rs.SkdConsumer;
import no.nav.registre.sdForvalter.consumer.rs.TpConsumer;
import no.nav.registre.sdForvalter.domain.TpsIdentListe;

@Service
public class IdentService {
    private final TpsIdenterAdapter tpsIdenterAdapter;
    private final HodejegerenConsumer hodejegerenConsumer;
    private final SkdConsumer skdConsumer;
    private final TpConsumer tpConsumer;
    private final Long staticDataPlaygroup;

    public IdentService(
            TpsIdenterAdapter tpsIdenterAdapter,
            HodejegerenConsumer hodejegerenConsumer,
            SkdConsumer skdConsumer,
            TpConsumer tpConsumer, @Value("${tps.statisk.avspillergruppeId}") Long staticDataPlaygroup
    ) {
        this.tpsIdenterAdapter = tpsIdenterAdapter;
        this.hodejegerenConsumer = hodejegerenConsumer;
        this.skdConsumer = skdConsumer;
        this.tpConsumer = tpConsumer;
        this.staticDataPlaygroup = staticDataPlaygroup;
    }

    public void send(String environment, String gruppe) {
        TpsIdentListe liste = tpsIdenterAdapter.fetchBy(gruppe);
        Set<String> exisistingFnrs = hodejegerenConsumer.getPlaygroupFnrs(staticDataPlaygroup);
        TpsIdentListe identer = new TpsIdentListe(liste
                .stream()
                .filter(item -> !exisistingFnrs.contains(item.getFnr()))
                .collect(Collectors.toList())
        );
        skdConsumer.createTpsIdenterMessagesInGroup(identer, staticDataPlaygroup);
        skdConsumer.send(staticDataPlaygroup, environment);

        /*
          Order of method calls are important to ensure that the values exist in the databases.
          Some methods may fail if at the very least TPS-ident (SKD) have not been created.
          TP and SAM are also critical databases for the fag applications
        */
        Set<String> livingFnrs = hodejegerenConsumer.getLivingFnrs(staticDataPlaygroup, environment);
        tpConsumer.send(livingFnrs, environment);
    }
}
