package no.nav.registre.sdforvalter.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.sdforvalter.adapter.TpsIdenterAdapter;
import no.nav.registre.sdforvalter.consumer.rs.HodejegerenConsumer;
import no.nav.registre.sdforvalter.consumer.rs.IdentPoolConsumer;
import no.nav.registre.sdforvalter.consumer.rs.SkdConsumer;
import no.nav.registre.sdforvalter.consumer.rs.TpConsumer;
import no.nav.registre.sdforvalter.domain.TpsIdent;
import no.nav.registre.sdforvalter.domain.TpsIdentListe;

@Service
public class IdentService {
    private final TpsIdenterAdapter tpsIdenterAdapter;
    private final HodejegerenConsumer hodejegerenConsumer;
    private final SkdConsumer skdConsumer;
    private final TpConsumer tpConsumer;
    private final IdentPoolConsumer identPoolConsumer;
    private final Long staticDataPlaygroup;

    public IdentService(
            TpsIdenterAdapter tpsIdenterAdapter,
            HodejegerenConsumer hodejegerenConsumer,
            SkdConsumer skdConsumer,
            IdentPoolConsumer identPoolConsumer,
            TpConsumer tpConsumer, @Value("${tps.statisk.avspillergruppeId}") Long staticDataPlaygroup
    ) {
        this.tpsIdenterAdapter = tpsIdenterAdapter;
        this.hodejegerenConsumer = hodejegerenConsumer;
        this.skdConsumer = skdConsumer;
        this.tpConsumer = tpConsumer;
        this.identPoolConsumer = identPoolConsumer;
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

    public TpsIdentListe save(TpsIdentListe liste, Boolean genererManglendeNavn) {
        var tpsIdentListe = genererManglendeNavn ? leggTilNavn(liste) : liste;

        return tpsIdenterAdapter.save(tpsIdentListe);
    }

    private TpsIdentListe leggTilNavn(TpsIdentListe liste) {
        var tpsIdentListe = liste.stream().map(ident -> {
            if (ident.getLastName() == null || ident.getFirstName() == null) {
                var navn = identPoolConsumer.getFiktivtNavn();
                return TpsIdent.builder()
                        .fnr(ident.getFnr())
                        .firstName(navn.getFornavn())
                        .lastName(navn.getEtternavn())
                        .address(ident.getAddress())
                        .postNr(ident.getPostNr())
                        .city(ident.getCity())
                        .gruppe(ident.getGruppe())
                        .opprinnelse(ident.getOpprinnelse())
                        .build();
            } else {
                return ident;
            }
        }).collect(Collectors.toList());
        return new TpsIdentListe(tpsIdentListe);
    }
}
