package no.nav.dolly.proxy.route;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.proxy.auth.AuthenticationFilterService;
import no.nav.dolly.proxy.service.DokarkivUploadService;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Component
@RequiredArgsConstructor
public class Dokarkiv {

    private static final String CLUSTER = "dev-fss";
    private static final String NAMESPACE = "teamdokumenthandtering";
    private static final String NAME = "dokarkiv%s"; // Note replacement pattern.

    private final Targets targets;
    private final AuthenticationFilterService authenticationFilterService;
    private final DokarkivUploadService uploadService;
    private final ObjectMapper objectMapper;

    Function<PredicateSpec, Buildable<Route>> build(@NonNull SpecialCase env) {

        var url = targets.dokarkiv.formatted(env.code);
        var authenticationFilter = authenticationFilterService
                .getTrygdeetatenAuthenticationFilter(CLUSTER, NAMESPACE, env.name, url);

        return spec -> spec
                .path("/dokarkiv/api/%s/**".formatted(env.code))
                .filters(f -> f
                        .stripPrefix(1)
                        .rewritePath("/api/%s/(?<segment>.*)".formatted(env.code), "/rest/journalpostapi/${segment}")
                        .modifyRequestBody(String.class, String.class, (exchange, body) -> resolveUploadReferences(body))
                        .setResponseHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8")
                        .filter(authenticationFilter))
                .uri(url);

    }

    @SuppressWarnings("unchecked")
    private Mono<String> resolveUploadReferences(String body) {
        if (isBlank(body)) {
            return Mono.justOrEmpty(body);
        }
        try {
            var request = objectMapper.readValue(body, new TypeReference<Map<String, Object>>() {});
            var dokumenter = (List<Map<String, Object>>) request.get("dokumenter");
            if (nonNull(dokumenter)) {
                var resolved = false;
                for (var dokument : dokumenter) {
                    var varianter = (List<Map<String, Object>>) dokument.get("dokumentvarianter");
                    if (nonNull(varianter)) {
                        for (var variant : varianter) {
                            var uploadRef = (String) variant.get("uploadReferanse");
                            if (isNotBlank(uploadRef)) {
                                var content = uploadService.resolveUpload(uploadRef);
                                variant.put("fysiskDokument", content);
                                variant.remove("uploadReferanse");
                                resolved = true;
                            }
                        }
                    }
                }
                if (resolved) {
                    return Mono.just(objectMapper.writeValueAsString(request));
                }
            }
        } catch (JsonProcessingException e) {
            log.warn("Kunne ikke parse dokarkiv request for upload-resolving, sender videre umodifisert", e);
        } catch (IllegalArgumentException e) {
            return Mono.error(new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Feilet ved resolving av uploadReferanse: " + e.getMessage()));
        }
        return Mono.just(body);
    }

    @RequiredArgsConstructor
    public enum SpecialCase {
        Q1("q1", "dokarkiv-q1"),
        Q2("q2", "dokarkiv"),
        Q4("q4", "dokarkiv-q4");

        @Getter
        private final String code;
        private final String name;
    }

}
