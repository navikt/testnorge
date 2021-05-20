package no.nav.pdl.forvalter.consumer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import no.nav.pdl.forvalter.config.credentials.PdlServiceProperties;
import no.nav.pdl.forvalter.consumer.command.PdlCreatePersonCommand;
import no.nav.pdl.forvalter.consumer.command.PdlDeletePersonCommand;
import no.nav.pdl.forvalter.consumer.command.PdlTestdataCommand;
import no.nav.pdl.forvalter.dto.PdlBestillingResponse;
import no.nav.pdl.forvalter.dto.PdlDeleteResponse;
import no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.getBestillingUrl;

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

    public PdlBestillingResponse sendArtifactToPdl(PdlArtifact artifact, String ident, Object body) throws JsonProcessingException {
        var accessToken = accessTokenService.generateToken(properties);
        return new PdlTestdataCommand(webClient, getBestillingUrl().get(artifact), ident, objectMapper.writer(filters).writeValueAsString(body), accessToken.getTokenValue()).call();
    }

    public JsonNode createPerson(String ident) {
        var accessToken = accessTokenService.generateToken(properties);
        return new PdlCreatePersonCommand(webClient, ident, accessToken.getTokenValue()).call();
    }

    public PdlDeleteResponse deletePerson(String ident) {
        var accessToken = accessTokenService.generateToken(properties);
        return new PdlDeletePersonCommand(webClient, ident, accessToken.getTokenValue()).call();
    }
}
