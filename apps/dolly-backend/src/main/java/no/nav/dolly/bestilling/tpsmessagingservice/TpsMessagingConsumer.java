package no.nav.dolly.bestilling.tpsmessagingservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.tpsmessagingservice.command.DeleteEgenansattCommand;
import no.nav.dolly.bestilling.tpsmessagingservice.command.DeleteSikkerhetstiltakCommand;
import no.nav.dolly.bestilling.tpsmessagingservice.command.DeleteTelefonnummerCommand;
import no.nav.dolly.bestilling.tpsmessagingservice.command.HentIdenterCommand;
import no.nav.dolly.bestilling.tpsmessagingservice.command.SendEgenansattCommand;
import no.nav.dolly.bestilling.tpsmessagingservice.command.SendTpsMessagingCommand;
import no.nav.dolly.config.credentials.TpsMessagingServiceProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsIdentStatusDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsMeldingResponseDTO;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class TpsMessagingConsumer {

    private static final String BASE_URL = "/api/v1/personer/{ident}";
    private static final String UTENLANDSK_BANKKONTO_URL = BASE_URL + "/bankkonto-utenlandsk";
    private static final String NORSK_BANKKONTO_URL = BASE_URL + "/bankkonto-norsk";
    private static final String SIKKERHETSTILTAK_URL = BASE_URL + "/sikkerhetstiltak";
    private static final String SPRAAKKODE_URL = BASE_URL + "/spraakkode";
    private static final String EGENANSATT_URL = BASE_URL + "/egenansatt";
    private static final String TELEFONNUMMER_URL = BASE_URL + "/telefonnumre";
    private static final String ADRESSE_UTLAND_URL = BASE_URL + "/adresse-utland";

    private static final List<String> TELEFONTYPER_LISTE = Arrays.asList("ARBT", "HJET", "MOBI");

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final NaisServerProperties serviceProperties;

    public TpsMessagingConsumer(TokenExchange tokenService, TpsMessagingServiceProperties serverProperties, ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.serviceProperties = serverProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "tps_messaging_createUtenlandskBankkonto" })
    public List<TpsMeldingResponseDTO> sendUtenlandskBankkontoRequest(String ident, List<String> miljoer, Object body) {

        return new SendTpsMessagingCommand(webClient, ident, miljoer, body, UTENLANDSK_BANKKONTO_URL, serviceProperties.getAccessToken(tokenService)).call();
    }


    @Timed(name = "providers", tags = { "operation", "tps_messaging_createNorskBankkonto" })
    public List<TpsMeldingResponseDTO> sendNorskBankkontoRequest(String ident, List<String> miljoer, Object body) {

        return new SendTpsMessagingCommand(webClient, ident, miljoer, body, NORSK_BANKKONTO_URL, serviceProperties.getAccessToken(tokenService)).call();
    }

    @Timed(name = "providers", tags = { "operation", "tps_messaging_deleteSikkerhetstiltak" })
    public List<TpsMeldingResponseDTO> deleteSikkerhetstiltakRequest(String ident, List<String> miljoer) {

        return new DeleteSikkerhetstiltakCommand(webClient, ident, miljoer, SIKKERHETSTILTAK_URL, serviceProperties.getAccessToken(tokenService)).call();
    }

    @Timed(name = "providers", tags = { "operation", "tps_messaging_createSikkerhetstiltak" })
    public List<TpsMeldingResponseDTO> sendSikkerhetstiltakRequest(String ident, List<String> miljoer, Object body) {

        return new SendTpsMessagingCommand(webClient, ident, miljoer, body, SIKKERHETSTILTAK_URL, serviceProperties.getAccessToken(tokenService)).call();
    }

    @Timed(name = "providers", tags = { "operation", "tps_messaging_createSkjerming" })
    public List<TpsMeldingResponseDTO> sendEgenansattRequest(String ident, List<String> miljoer, LocalDate fraOgMed) {

        return new SendEgenansattCommand(webClient, ident, miljoer, fraOgMed, EGENANSATT_URL, serviceProperties.getAccessToken(tokenService)).call();
    }

    @Timed(name = "providers", tags = { "operation", "tps_messaging_deleteSkjerming" })
    public List<TpsMeldingResponseDTO> deleteEgenansattRequest(String ident, List<String> miljoer) {

        return new DeleteEgenansattCommand(webClient, ident, miljoer, EGENANSATT_URL, serviceProperties.getAccessToken(tokenService)).call();
    }

    @Timed(name = "providers", tags = {"operation", "tps_messaging_createTelefonnummer"})
    public List<TpsMeldingResponseDTO> sendTelefonnummerRequest(String ident, List<String> miljoer, Object body) {

        return new SendTpsMessagingCommand(webClient, ident, miljoer, body, TELEFONNUMMER_URL, serviceProperties.getAccessToken(tokenService)).call();
    }

    @Timed(name = "providers", tags = {"operation", "tps_messaging_createAdresseUtland"})
    public List<TpsMeldingResponseDTO> sendAdresseUtlandRequest(String ident, List<String> miljoer, Object body) {

        return new SendTpsMessagingCommand(webClient, ident, miljoer, body, ADRESSE_UTLAND_URL, serviceProperties.getAccessToken(tokenService)).call();
    }

    @Timed(name = "providers", tags = {"operation", "tps_messaging_deleteTelefonnummer"})
    public List<TpsMeldingResponseDTO> deleteTelefonnummerRequest(String ident, List<String> miljoer) {

        return new DeleteTelefonnummerCommand(webClient, ident, miljoer, TELEFONTYPER_LISTE, TELEFONNUMMER_URL, serviceProperties.getAccessToken(tokenService)).call();
    }

    @Timed(name = "providers", tags = { "operation", "tps_messaging_createSpraakkode" })
    public List<TpsMeldingResponseDTO> sendSpraakkodeRequest(String ident, List<String> miljoer, Object body) {

        return new SendTpsMessagingCommand(webClient, ident, miljoer, body, SPRAAKKODE_URL, serviceProperties.getAccessToken(tokenService)).call();
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

    @Timed(name = "providers", tags = { "operation", "tps_messaging_getIdenter" })
    public List<TpsIdentStatusDTO> getIdenter(List<String> identer, List<String> miljoer) {

        return new HentIdenterCommand(webClient, miljoer, identer, serviceProperties.getAccessToken(tokenService)).call();
    }
}