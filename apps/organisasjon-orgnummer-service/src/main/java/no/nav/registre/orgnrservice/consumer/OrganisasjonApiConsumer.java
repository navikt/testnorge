package no.nav.registre.orgnrservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import no.nav.registre.testnorge.libs.common.command.GetOrganisasjonCommand;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

@Slf4j
@Component
@DependencyOn("testnorge-organisasjon-api")
public class OrganisasjonApiConsumer {

    private final WebClient webClient;
    private final String clientId;
    private final AccessTokenService accessTokenService;
    private final ExecutorService executorService;

    OrganisasjonApiConsumer (@Value("${consumers.organisasjon-api.url}") String url,
                             @Value("${consumers.organisasjon-api.client_id}") String clientId,
                             @Value("${consumers.organisasjon-api.threads}") Integer threads,
                             AccessTokenService accessTokenService) {
        this.clientId = clientId;
        this.accessTokenService = accessTokenService;
        this.executorService = Executors.newFixedThreadPool(threads);
        this.webClient = WebClient.builder()
                .baseUrl(url)
                .build();
    }

//    CompletableFuture<OrganisasjonDTO> getOrgnrFraMiljoe (String orgnummer, String miljoe, String token) {
//        return CompletableFuture.supplyAsync(
//                () -> new GetOrganisasjonCommand(webClient, token, orgnummer, miljoe).call(),
//                executorService
//        );
//    }
//
//    public boolean finnesOrgnrIEreg(String orgnummer) {
//        List<String> miljoer = Arrays.asList("q1"); //TODO: Legg til flere miljø
//
//        String token = accessTokenService.generateToken(clientId).getTokenValue();
//
//        var futures = miljoer.stream()
//                .map( enkeltmiljoe -> getOrgnrFraMiljoe(orgnummer, enkeltmiljoe, token))
//                .collect(Collectors.toList());
//
//        List<OrganisasjonDTO> miljoeListe = new ArrayList<>();
//        for (CompletableFuture<OrganisasjonDTO> future : futures) {
//            try {
//                OrganisasjonDTO e = future.get();
//                log.info("Organisasjon hentet: {}", e == null ? "ingen": e);
//                miljoeListe.add(e);
//            } catch ( Exception e) {
//                throw new RuntimeException("Klarte ikke hente organisasjon fra et miljø");
//            }
//        }
//        long antallMiljoerOrgnrFinnes = miljoeListe.stream().filter(Objects::nonNull).count();
//        return antallMiljoerOrgnrFinnes > 0;
//    }

    OrganisasjonDTO getOrgnrFraMiljoe (String orgnummer, String miljoe, String token) {
        try {
            OrganisasjonDTO call = new GetOrganisasjonCommand(webClient, token, orgnummer, miljoe).call();
            return call;
        } catch (Exception e) {
            throw new RuntimeException("Kunne ikke hente organisasjon " + orgnummer + " fra miljoe " + miljoe);
        }
    }
    public OrganisasjonDTO getOrgnr (String orgnummer) {
        //evt loop gjennom miljø. Lage tråder
        String miljoe = "q1";

        String token = accessTokenService.generateToken(clientId).getTokenValue();
        return getOrgnrFraMiljoe(orgnummer, miljoe, token);
    }
}
