package no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TenorConsumer {
    /*
    private static final String TENOR_DOMAIN = "https://testnav-tenor-search-service.intern.dev.nav.no";
    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;
    private final ObjectMapper objectMapper;


    public TenorConsumer(
            TokenExchange tokenExchange,
            Consumers consumers,
            ObjectMapper objectMapper) {

        this.serverProperties = consumers.getTenorSearchService();
        this.tokenExchange = tokenExchange;
        this.objectMapper = objectMapper;

        ExchangeStrategies jacksonStrategy = ExchangeStrategies
                .builder()
                .codecs(
                        config -> {
                            config
                                    .defaultCodecs()
                                    .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                            config
                                    .defaultCodecs()
                                    .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                        })
                .build();

        this.webClient = WebClient
                .builder()
                .exchangeStrategies(jacksonStrategy)
                .baseUrl(TENOR_DOMAIN)
                .build();
    }

    public void consume() throws JsonProcessingException {
        System.out.println("Kjører consume");
        var accessToken = tokenExchange.exchange(serverProperties).block();
        System.out.println("Har hentet ut token");

        if (nonNull(accessToken)) {
            var token = accessToken.getTokenValue();
            HentPersonerCommand commander = new HentPersonerCommand(token, webClient);
            JsonNode data = commander.hentPersonData();
            var rawResponse = objectMapper.readValue(data.toString(), TenorRawResponse.class);
            System.out.println(rawResponse.getDokumentListe().getFirst().getBostedsadresse().toString());
        }
    }

     */
}