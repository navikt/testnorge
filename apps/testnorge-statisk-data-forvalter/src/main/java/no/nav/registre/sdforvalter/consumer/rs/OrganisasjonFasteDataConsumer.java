package no.nav.registre.sdforvalter.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.atomic.AtomicInteger;

import no.nav.registre.sdforvalter.config.credentials.OrganisasjonFasteDataServiceProperties;
import no.nav.registre.sdforvalter.consumer.rs.commnad.SaveOrganisasjonFasteDataCommand;
import no.nav.registre.sdforvalter.consumer.rs.domain.OrgTree;
import no.nav.registre.sdforvalter.consumer.rs.domain.OrgTreeList;
import no.nav.registre.sdforvalter.domain.EregListe;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.Gruppe;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Slf4j
@Component
public class OrganisasjonFasteDataConsumer {
    private final WebClient webClient;
    private final OrganisasjonFasteDataServiceProperties serverProperties;
    private final TokenExchange tokenExchange;
    private final GenererNavnConsumer genererNavnConsumer;

    public OrganisasjonFasteDataConsumer(
            OrganisasjonFasteDataServiceProperties serverProperties,
            TokenExchange tokenExchange,
            GenererNavnConsumer genererNavnConsumer) {
        this.serverProperties = serverProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient
                .builder()
                .baseUrl(serverProperties.getUrl())
                .build();
        this.genererNavnConsumer = genererNavnConsumer;
    }


    private String genererNavn(String enhetstype) {
        NavnDTO navn = genererNavnConsumer.genereNavn();
        return navn.getAdjektiv() + " " + navn.getSubstantiv() + (enhetstype.equals("AS") ? " AS" : "");
    }

    public void opprett(EregListe eregListe) {
        var count = new AtomicInteger();
        log.info("Oppretter {} organisasjoner...", eregListe.size());
        var treeList = new OrgTreeList(eregListe);
        treeList.getList().forEach(tree -> {
            opprett(tree, count);
            log.info("{}/{} organisasjoner opprettet...", count.get(), eregListe.size());
        });
        log.info("{} organisasjoner opprettet.", eregListe.size());
    }

    private void opprett(OrgTree orgTree, AtomicInteger count) {
        var organisasjon = orgTree.getOrganisasjon();
        var accessToken = tokenExchange.exchange(serverProperties).block();
        new SaveOrganisasjonFasteDataCommand(
                webClient,
                accessToken.getTokenValue(),
                organisasjon.toDTOv2(() -> genererNavn(organisasjon.getEnhetstype())),
                organisasjon.getGruppe() == null ? Gruppe.ANDRE : Gruppe.valueOf(organisasjon.getGruppe())
        ).run();
        count.incrementAndGet();
        orgTree.getUnderorganisasjon().forEach(tree -> opprett(tree, count));
    }
}
