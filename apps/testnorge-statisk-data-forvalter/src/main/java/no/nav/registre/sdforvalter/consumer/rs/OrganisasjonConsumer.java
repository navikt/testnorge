package no.nav.registre.sdforvalter.consumer.rs;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import no.nav.registre.sdforvalter.domain.Ereg;
import no.nav.registre.sdforvalter.domain.EregListe;
import no.nav.registre.sdforvalter.domain.status.ereg.Organisasjon;
import no.nav.registre.testnorge.libs.common.command.GetOrganisasjonCommand;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

@Slf4j
@Component
public class OrganisasjonConsumer {
    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final Executor executor;
    private final OrganisasjonProducers organisasjonProducers;
    private final String clientId;

    public OrganisasjonConsumer(
            @Value("${organsisasjon.api.url}") String url,
            @Value("${organsisasjon.api.client_id}") String clientId,
            AccessTokenService accessTokenService,
            @Value("${organsisasjon.api.threads}") Integer threads,
            OrganisasjonProducers organisasjonProducers
    ) {
        this.clientId = clientId;
        this.accessTokenService = accessTokenService;
        this.executor = Executors.newFixedThreadPool(threads);
        this.webClient = WebClient
                .builder()
                .baseUrl(url)
                .build();
        this.organisasjonProducers = organisasjonProducers;
    }

    private CompletableFuture<Organisasjon> getOrganisasjon(String orgnummer, String miljo, Executor executor) {
        AccessToken accessToken = accessTokenService.generateToken(clientId);
        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        return new Organisasjon(new GetOrganisasjonCommand(webClient, accessToken.getTokenValue(), orgnummer, miljo).call());
                    } catch (Exception e) {
                        log.warn("Klarer ikke å hente organsisasjon {}", orgnummer, e);
                        return null;
                    }
                },
                executor
        );
    }

    public Map<String, Organisasjon> getOrganisasjoner(List<String> orgnummerList, String miljo) {
        log.info("Henter ut {} fra ereg", String.join(", ", orgnummerList));

        AsyncOrganisasjonMap asyncMap = new AsyncOrganisasjonMap();
        orgnummerList.forEach(orgnummer -> asyncMap.put(orgnummer, getOrganisasjon(orgnummer, miljo, executor)));

        return asyncMap.getMap();
    }

    @SneakyThrows
    private Organisasjon getOrganisasjon(String orgnummer, String miljo) {
        log.info("Henter ut {} fra EREG ({}).", orgnummer, miljo);
        AccessToken accessToken = accessTokenService.generateToken(clientId);
        var dto = new GetOrganisasjonCommand(webClient, accessToken.getTokenValue(), orgnummer, miljo).call();
        return dto != null ? new Organisasjon(dto) : null;
    }


    public void opprett(EregListe liste, String miljo, boolean update) {
        var ordereId = UUID.randomUUID();
        var list = liste.getListe();
        list.forEach(createEregConsumer(ordereId, miljo, update));
        list.stream()
                .filter(ereg -> ereg.getJuridiskEnhet() != null)
                .forEach(ereg -> organisasjonProducers.setJuridiskEnhet(ordereId, ereg, miljo));
    }

    public Consumer<Ereg> createEregConsumer(UUID ordereId, String miljo, boolean update) {
        return ereg -> {
            if (update) {
                organisasjonProducers.setNavn(ordereId, ereg, miljo);
            } else {
                if (getOrganisasjon(ereg.getOrgnr(), miljo) == null) {
                    organisasjonProducers.opprettOrganiasjon(ordereId, ereg, miljo);
                } else {
                    log.warn("Fant {} i EREG ({}). Oppretter ikke på nytt.", ereg.getOrgnr(), miljo);
                }
            }
            organisasjonProducers.setNearingskode(ordereId, ereg, miljo);
            if (ereg.getPostadresse() != null) {
                organisasjonProducers.setPostadresse(ordereId, ereg, miljo);
            }
            if (ereg.getForretningsAdresse() != null) {
                organisasjonProducers.setForretningsadresse(ordereId, ereg, miljo);
            }
            if (ereg.getEpost() != null) {
                organisasjonProducers.setEpost(ordereId, ereg, miljo);
            }
            if (ereg.getInternetAdresse() != null) {
                organisasjonProducers.setInternetAdresse(ordereId, ereg, miljo);
            }
        };
    }

}
