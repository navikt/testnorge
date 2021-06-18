package no.nav.pdl.forvalter.consumer;

import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlStatus.FEIL;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.getBestillingUrl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

import no.nav.pdl.forvalter.config.credentials.PdlServiceProperties;
import no.nav.pdl.forvalter.consumer.command.pdl.PdlTestdataCommand;
import no.nav.pdl.forvalter.consumer.command.pdl.DeletePersonCommand;
import no.nav.pdl.forvalter.domain.ArtifactValues;
import no.nav.pdl.forvalter.dto.PdlOrdreResponse;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

@Slf4j
@Service
public class PdlTestdataConsumer {

    private static final PropertyFilter removeIdFilter = new SimpleBeanPropertyFilter() {
        @Override
        public void serializeAsField
                (Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer)
                throws Exception {
            if (include(writer)) {
                if (!writer.getName().equals("id")) {
                    writer.serializeAsField(pojo, jgen, provider);
                }
            } else if (!jgen.canOmitFields()) {
                writer.serializeAsOmittedField(pojo, jgen, provider);
            }
        }

        @Override
        protected boolean include(BeanPropertyWriter writer) {
            return true;
        }

        @Override
        protected boolean include(PropertyWriter writer) {
            return true;
        }
    };

    private static final FilterProvider filters = new SimpleFilterProvider().addFilter("idFilter", removeIdFilter);

    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final NaisServerProperties properties;
    private final ObjectMapper objectMapper;

    public PdlTestdataConsumer(AccessTokenService accessTokenService,
                               PdlServiceProperties properties,
                               ObjectMapper objectMapper) {

        this.accessTokenService = accessTokenService;
        this.properties = properties;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .build();
        this.objectMapper = objectMapper;
    }

    public Flux<PdlOrdreResponse.Hendelse> send(List<ArtifactValues> artifactList) {
        return accessTokenService
                .generateToken(properties)
                .flatMapMany(accessToken -> artifactList
                        .stream()
                        .map(values -> send(values, accessToken))
                        .reduce(Flux.empty(), Flux::concat)
                );

    }


    private Flux<PdlOrdreResponse.Hendelse> send(ArtifactValues values, AccessToken accessToken) {
        String body;
        try {
            body = objectMapper.writer(filters).writeValueAsString(values.getBody());
        } catch (JsonProcessingException e) {
            return Flux.from(Mono.just(
                    PdlOrdreResponse.Hendelse.builder()
                            .id(values.getBody().getId())
                            .status(FEIL)
                            .error(e.getMessage())
                            .build()
            ));
        }

        return Flux.from(
                new PdlTestdataCommand(
                        webClient,
                        getBestillingUrl().get(values.getArtifact()),
                        values.getIdent(),
                        body,
                        accessToken.getTokenValue(),
                        values.getBody().getId()
                ).call()
        );
    }

    public void delete(List<String> identer) {
        accessTokenService
                .generateToken(properties)
                .flatMapMany(accessToken -> identer
                        .stream()
                        .map(ident -> Flux.from(new DeletePersonCommand(webClient, ident, accessToken.getTokenValue(), null).call()))
                        .reduce(Flux.empty(), Flux::concat)
                )
                .collectList()
                .block();
    }
}
