package no.nav.organisasjonforvalter.consumer;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.consumer.TpsfAdresseConsumer.GyldigeAdresserResponse.AdresseData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.ProxyProvider;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.nonNull;

@Slf4j
@Service
public class TpsfAdresseConsumer {

    private static final String OK_STATUS = "00";
    private static final int TIMEOUT_MS = 10_000;
    private static final String ADRESSE_URL = "/api/v1/gyldigadresse/tilfeldig?maxAntall=";

    private final WebClient webClient;

    public TpsfAdresseConsumer(
            @Value("${tpsf.url}") String baseUrl) {

        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .tcpConfiguration(tcpClient -> tcpClient
                                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT_MS)
                                        .doOnConnected(connection ->
                                                connection
                                                        .addHandlerLast(new ReadTimeoutHandler(TIMEOUT_MS))
                                                        .addHandlerLast(new WriteTimeoutHandler(TIMEOUT_MS))))))
                .build();
    }

    private static AdresseData getDefaultADresse() {

        return AdresseData.builder()
                .adrnavn("FYRSTIKKALLÉEN")
                .husnrfra("0001")
                .husnrtil("0018")
                .geotilk("030101")
                .knr("0301")
                .pnr("0661")
                .gkode("12133")
                .build();
    }

    public AdresseData getAdresser(String postnr, String kommunenr) {

        long startTime = currentTimeMillis();

        try {
            ResponseEntity<GyldigeAdresserResponse> response = webClient.get()
                    .uri(format("%s%d%s", ADRESSE_URL, 1, getSuffix(postnr, kommunenr)))
                    .header("Nav-Consumer-Id", "Testnorge")
                    .header("Nav-Call-Id", UUID.randomUUID().toString())
                    .retrieve()
                    .toEntity(GyldigeAdresserResponse.class)
                    .block();

            log.info("Adresseoppslag tok {} ms", currentTimeMillis() - startTime);

            if (response.hasBody() && OK_STATUS.equals(response.getBody().getResponse().getStatus().getKode())) {
                return response.getBody().getResponse().getData1().getAdrData().get(0);

            } else {
                log.error("Henting av adresse feilet for postnr {} / kommunenr {} melding {} utfyllende melding {}",
                        postnr, kommunenr, response.getBody().getResponse().getStatus().getMelding(),
                        response.getBody().getResponse().getStatus().getUtfyllendeMelding());

                return getDefaultADresse();
            }

        } catch (RuntimeException e) {

            log.error("Henting av adresse timeout etter {} ms", TIMEOUT_MS, e);
            return getDefaultADresse();
        }
    }

    public String getSuffix(String postnr, String kommunenr) {

        if (nonNull(postnr)) {
            return "&postNr=" + postnr;
        } else if (nonNull(kommunenr)) {
            return "&kommuneNr=" + kommunenr;
        } else {
            return "";
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GyldigeAdresserResponse {

        private String xml;
        private Response response;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Response {

            private DataContainer data1;
            private DataStatus status;
            private Integer antallTotalt;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class DataStatus {

            private String kode;
            private String melding;
            private String utfyllendeMelding;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class DataContainer {

            private Integer antallForekomster;
            private List<AdresseData> adrData;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class AdresseData {

            private String psted;
            private String knavn;
            private String adrnavn;
            private String gkode;
            private String pnr;
            private String geotilk;
            private String husnrtil;
            private String bydel;
            private String knr;
            private String husnrfra;
        }
    }
}
