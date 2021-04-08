package no.nav.registre.sdforvalter.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Supplier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.sdforvalter.config.credentials.OrganisasjonFasteDataServiceProperties;
import no.nav.registre.sdforvalter.consumer.rs.commnad.SaveOrganisasjonFasteDataCommand;
import no.nav.registre.sdforvalter.consumer.rs.domain.OrgTree;
import no.nav.registre.sdforvalter.consumer.rs.domain.OrgTreeList;
import no.nav.registre.sdforvalter.domain.EregListe;
import no.nav.registre.testnorge.libs.dto.generernavnservice.v1.NavnDTO;
import no.nav.registre.testnorge.libs.dto.organisasjonfastedataservice.v1.Gruppe;
import no.nav.registre.testnorge.libs.dto.organisasjonfastedataservice.v1.OrganisasjonDTO;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

@Slf4j
@Component
public class OrganisasjonFasteDataConsumer {
    private final WebClient webClient;
    private final OrganisasjonFasteDataServiceProperties serverProperties;
    private final AccessTokenService accessTokenService;
    private final GenererNavnConsumer genererNavnConsumer;

    public OrganisasjonFasteDataConsumer(
            OrganisasjonFasteDataServiceProperties serverProperties,
            AccessTokenService accessTokenService,
            GenererNavnConsumer genererNavnConsumer) {
        this.serverProperties = serverProperties;
        this.accessTokenService = accessTokenService;
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
        log.info("Oppretter {} organisasjoner...", eregListe.size());
        var treeList = new OrgTreeList(eregListe);
        treeList.getList().forEach(this::opprett);
        log.info("{} organisasjoner opprettet.", eregListe.size());
    }

    private void opprett(OrgTree orgTree) {
        var organisasjon = orgTree.getOrganisasjon();
        log.info("Oppretter organisajon med orgnummer {}.", organisasjon.getOrgnr());
        var accessToken = accessTokenService.generateToken(serverProperties);
        var dto = organisasjon.toDTOv2(() -> genererNavn(organisasjon.getEnhetstype()));

        new SaveOrganisasjonFasteDataCommand(
                webClient,
                accessToken.getTokenValue(),
                dto,
                organisasjon.getGruppe() == null ? Gruppe.ANDRE : Gruppe.valueOf(organisasjon.getGruppe())
        ).run();
        orgTree.getUnderorganisasjon().forEach(this::opprett);
    }
}
