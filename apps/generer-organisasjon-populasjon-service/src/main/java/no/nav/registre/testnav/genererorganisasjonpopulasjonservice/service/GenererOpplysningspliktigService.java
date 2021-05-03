package no.nav.registre.testnav.genererorganisasjonpopulasjonservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import no.nav.registre.testnav.genererorganisasjonpopulasjonservice.consumer.GenererNavnConsumer;
import no.nav.registre.testnav.genererorganisasjonpopulasjonservice.consumer.OrgnummerConsumer;
import no.nav.registre.testnav.genererorganisasjonpopulasjonservice.domain.Organisasjon;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenererOpplysningspliktigService {
    private final GenererNavnConsumer genererNavnConsumer;
    private final OrgnummerConsumer orgnummerConsumer;
    private final Executor executor;

    public List<Organisasjon> generer(Integer antall) {
        var futures = new ArrayList<CompletableFuture<Organisasjon>>(antall);
        for (int index = 0; index < antall; index++) {
            futures.add(generer());
        }

        var organisasjoner = new ArrayList<Organisasjon>();
        for (int index = 0; index < futures.size(); index++) {
            try {
                var organisasjon = futures.get(index).get(12, TimeUnit.MINUTES);
                log.info("{}/{} organisasjoner opprettet.", index + 1, futures.size());
                organisasjoner.add(organisasjon);
            } catch (Exception e) {
                log.warn("Klarer ikke å opprette organisasjon.", e);
            }
        }
        if (futures.size() > organisasjoner.size()) {
            log.warn("Bare {}/{} organisasjoner opprettet.", organisasjoner.size(), futures.size());
        }


        if (organisasjoner.isEmpty()) {
            throw new RuntimeException("Klarer ikke å opprette organisasjoner.");
        }

        return organisasjoner;
    }

    private CompletableFuture<Organisasjon> generer() {
        var random = new Random();
        return CompletableFuture.supplyAsync(() -> {
            var opplysningspliktig = generer("AS");
            for (int index = 0; index <= random.nextInt(5); index++) {
                opplysningspliktig.getVirksomheter().add(generer("BEDR"));
            }
            return opplysningspliktig;
        }, executor);
    }


    private Organisasjon generer(String enhetstype) {
        var navn = genererNavnConsumer.genereNavn();
        var orgnummer = orgnummerConsumer.getOrgnummer();
        var organisasjon = new Organisasjon();
        organisasjon.setName(navn.getAdjektiv() + " " + navn.getSubstantiv() + (enhetstype.equals("AS") ? " AS" : ""));
        organisasjon.setOrgnummer(orgnummer);
        organisasjon.setEnhetstype(enhetstype);
        return organisasjon;
    }

}
