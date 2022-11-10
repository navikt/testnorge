package no.nav.registre.sdforvalter.service;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sdforvalter.adapter.TpsIdenterAdapter;
import no.nav.registre.sdforvalter.consumer.rs.navn.GenererNavnConsumer;
import no.nav.registre.sdforvalter.consumer.rs.hodejegeren.HodejegerenConsumer;
import no.nav.registre.sdforvalter.consumer.rs.person.PersonConsumer;
import no.nav.registre.sdforvalter.consumer.rs.tp.TpConsumer;
import no.nav.registre.sdforvalter.consumer.rs.tpsf.TpsfConsumer;
import no.nav.registre.sdforvalter.domain.TpsIdent;
import no.nav.registre.sdforvalter.domain.TpsIdentListe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@Slf4j
public class IdentService {
    private final TpsIdenterAdapter tpsIdenterAdapter;
    private final HodejegerenConsumer hodejegerenConsumer;
    private final TpConsumer tpConsumer;
    private final GenererNavnConsumer genererNavnConsumer;
    private final PersonConsumer personConsumer;
    private final TpsfConsumer tpsfConsumer;
    private final Long staticDataPlaygroup;

    public IdentService(
            TpsIdenterAdapter tpsIdenterAdapter,
            HodejegerenConsumer hodejegerenConsumer,
            GenererNavnConsumer genererNavnConsumer,
            PersonConsumer personConsumer,
            TpConsumer tpConsumer,
            TpsfConsumer tpsfConsumer,
            @Value("${tps.statisk.avspillergruppeId}") Long staticDataPlaygroup
    ) {
        this.tpsIdenterAdapter = tpsIdenterAdapter;
        this.hodejegerenConsumer = hodejegerenConsumer;
        this.tpConsumer = tpConsumer;
        this.genererNavnConsumer = genererNavnConsumer;
        this.personConsumer = personConsumer;
        this.tpsfConsumer = tpsfConsumer;
        this.staticDataPlaygroup = staticDataPlaygroup;
    }

    public void send(String environment, String gruppe) {
        TpsIdentListe liste = tpsIdenterAdapter.fetchBy(gruppe);
        var exisistingFnrs = hodejegerenConsumer.getPlaygroupFnrs(staticDataPlaygroup);
        var identer = new TpsIdentListe(liste.stream()
                .filter(item -> !exisistingFnrs.contains(item.getFnr()))
                .toList()
        );
        sendTilTps(staticDataPlaygroup, environment);

        //Person får ikke aktør-id automatisk ved opprettelse i TPS lenger. Må opprettes i PDL også for å få aktør-id
        opprettPersonerIPdl(identer);

        /*
          Order of method calls are important to ensure that the values exist in the databases.
          Some methods may fail if at the very least TPS-ident (SKD) have not been created.
          TP and SAM are also critical databases for the fag applications
        */
        var livingFnrs = hodejegerenConsumer.getLivingFnrs(staticDataPlaygroup, environment);
        tpConsumer.send(livingFnrs, environment);
    }

    private void sendTilTps(Long playgroup, String environment) {
        tpsfConsumer.sendTilTps(playgroup, environment).subscribe(response -> {
            if (isNull(response) || response.getAntallFeilet() != 0) {
                log.warn("Fikk ikke opprettet alle identene i TPS, burde bli manuelt sjekket for overlapp. " +
                        "Kan også være mulig at man prøver å initialisere et miljø som er allerede initialisert");
            }
            if (nonNull(response)) {
                response.getFailedStatus().forEach(s -> log.error("Status på feilende melding: {}", s));
            }
        });
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
                var navn = genererNavnConsumer.genereNavn();
                return new TpsIdent(ident.getFnr(),
                        navn.getAdjektiv(),
                        navn.getSubstantiv(),
                        ident.getAddress(),
                        ident.getPostNr(),
                        ident.getCity(),
                        ident.getGruppe(),
                        ident.getOpprinnelse(),
                        ident.getTags());
            } else {
                return ident;
            }
        }).toList();
        return new TpsIdentListe(tpsIdentListe);
    }
}
