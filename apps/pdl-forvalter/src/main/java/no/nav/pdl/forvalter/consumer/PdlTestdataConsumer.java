package no.nav.pdl.forvalter.consumer;

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
import no.nav.pdl.forvalter.config.credentials.PdlServiceProperties;
import no.nav.pdl.forvalter.consumer.command.PdlDeleteCommand;
import no.nav.pdl.forvalter.consumer.command.PdlOpprettPersonCommand;
import no.nav.pdl.forvalter.consumer.command.PdlTestdataCommand;
import no.nav.pdl.forvalter.dto.HistoriskIdent;
import no.nav.pdl.forvalter.dto.PdlBestillingResponse;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.PdlArtifact;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Objects.isNull;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.getBestillingUrl;

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
    private static String token;
    private static LocalDateTime timestamp;
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

    public PdlBestillingResponse send(PdlArtifact artifact, String ident, Object body) throws JsonProcessingException {

        switch (artifact) {
            case PDL_SLETTING:
                return new PdlDeleteCommand(webClient, getBestillingUrl().get(artifact), ident, getToken()).call();

            case PDL_OPPRETT_PERSON:
                return new PdlOpprettPersonCommand(webClient, getBestillingUrl().get(artifact), ident,
                        (HistoriskIdent) body, getToken()).call();
            default:
                return new PdlTestdataCommand(webClient, getBestillingUrl().get(artifact), ident,
                        objectMapper.writer(filters).writeValueAsString(body), getToken()).call();
        }
    }

    public void delete(List<String> identer) {

        identer.forEach(ident -> {
            try {
                new PdlDeleteCommand(webClient, getBestillingUrl().get(PdlArtifact.PDL_SLETTING),
                        ident, getToken()).call();

            } catch (WebClientResponseException e) {
                if (!e.getResponseBodyAsString().contains("Finner ikke forespurt ident i pdl-api")) {
                    log.error("Sletting av person feilet mot pdl {} ", e.getResponseBodyAsString(), e);
                }
            }
        });
    }

    private String getToken() {

        if (isNull(timestamp) || timestamp.plusMinutes(10).isBefore(LocalDateTime.now())) {
            token = accessTokenService.generateToken(properties).block().getTokenValue();
            timestamp = LocalDateTime.now();
        }
        return token;
    }
}
