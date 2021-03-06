package no.nav.registre.sdforvalter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.sdforvalter.adapter.TpsIdenterAdapter;
import no.nav.registre.sdforvalter.consumer.rs.HodejegerenConsumer;
import no.nav.registre.sdforvalter.consumer.rs.IdentPoolConsumer;
import no.nav.registre.sdforvalter.consumer.rs.PersonConsumer;
import no.nav.registre.sdforvalter.consumer.rs.SkdConsumer;
import no.nav.registre.sdforvalter.consumer.rs.TpConsumer;
import no.nav.registre.sdforvalter.domain.TpsIdent;
import no.nav.registre.sdforvalter.domain.TpsIdentListe;

@Service
@Slf4j
public class IdentService {
    private final TpsIdenterAdapter tpsIdenterAdapter;
    private final HodejegerenConsumer hodejegerenConsumer;
    private final SkdConsumer skdConsumer;
    private final TpConsumer tpConsumer;
    private final IdentPoolConsumer identPoolConsumer;
    private final PersonConsumer personConsumer;
    private final Long staticDataPlaygroup;

    public IdentService(
            TpsIdenterAdapter tpsIdenterAdapter,
            HodejegerenConsumer hodejegerenConsumer,
            SkdConsumer skdConsumer,
            IdentPoolConsumer identPoolConsumer,
            PersonConsumer personConsumer,
            TpConsumer tpConsumer,  @Value("${tps.statisk.avspillergruppeId}") Long staticDataPlaygroup
    ) {
        this.tpsIdenterAdapter = tpsIdenterAdapter;
        this.hodejegerenConsumer = hodejegerenConsumer;
        this.skdConsumer = skdConsumer;
        this.tpConsumer = tpConsumer;
        this.identPoolConsumer = identPoolConsumer;
        this.personConsumer = personConsumer;
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

        //Person får ikke aktør-id automatisk ved opprettelse i TPS lenger. Må opprettes i PDL også for å få aktør-id
        opprettPersonerIPdl(identer);

        /*
          Order of method calls are important to ensure that the values exist in the databases.
          Some methods may fail if at the very least TPS-ident (SKD) have not been created.
          TP and SAM are also critical databases for the fag applications
        */
        Set<String> livingFnrs = hodejegerenConsumer.getLivingFnrs(staticDataPlaygroup, environment);
        tpConsumer.send(livingFnrs, environment);
    }

    public void opprettPersonerIPdl(TpsIdentListe tpsIdentListe) {
        try {
            personConsumer.opprettPersoner(tpsIdentListe);
        } catch (Exception e) {
            log.info("En eller flere personer ble ikke opprettet i PDL, og fikk dermed ikke aktør-id");
        }
    }

    public TpsIdentListe save(TpsIdentListe liste, Boolean genererManglendeNavn) {
        var tpsIdentListe = genererManglendeNavn ? leggTilNavn(liste) : liste;

        return tpsIdenterAdapter.save(tpsIdentListe);
    }

    private TpsIdentListe leggTilNavn(TpsIdentListe liste) {
        var tpsIdentListe = liste.stream().map(ident -> {
            if (ident.getLastName() == null || ident.getFirstName() == null) {
                var navn = identPoolConsumer.getFiktivtNavn();
                return new TpsIdent(ident.getFnr(),
                        navn.getFornavn(),
                        navn.getEtternavn(),
                        ident.getAddress(),
                        ident.getPostNr(),
                        ident.getCity(),
                        ident.getGruppe(),
                        ident.getOpprinnelse(),
                        ident.getTags());
            } else {
                return ident;
            }
        }).collect(Collectors.toList());
        return new TpsIdentListe(tpsIdentListe);
    }
}
