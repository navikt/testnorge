package no.nav.dolly.web.provider.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.HtmlUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.charset.StandardCharsets;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Controller
public class ForwardAndRedirectController {

    @Bean
    public RouterFunction<ServerResponse> htmlRouter(
            @Value("classpath:/static/index.html") Resource html,
            @Value("${FRONTEND_TELEMETRY_ENABLED:false}") boolean telemetryEnabled,
            @Value("${NAIS_APP_NAME:dolly-frontend}") String appName,
            @Value("${NAIS_NAMESPACE:dolly}") String namespace,
            @Value("${NAIS_CLUSTER_NAME:local}") String cluster,
            @Value("${NAIS_FRONTEND_TELEMETRY_COLLECTOR_URL:}") String collectorUrl) {
        var metadata = """
                <meta name="dolly-telemetry-enabled" content="%s">
                <meta name="nais-app" content="%s">
                <meta name="nais-team" content="%s">
                <meta name="nais-cluster" content="%s">
                <meta name="nais-telemetry-url" content="%s">
                """.formatted(
                telemetryEnabled,
                HtmlUtils.htmlEscape(appName),
                HtmlUtils.htmlEscape(namespace),
                HtmlUtils.htmlEscape(cluster),
                HtmlUtils.htmlEscape(collectorUrl));
        var index = Mono.fromCallable(() -> html.getContentAsString(StandardCharsets.UTF_8)
                        .replace("</head>", metadata + "</head>"))
                .subscribeOn(Schedulers.boundedElastic())
                .cache();
        HandlerFunction<ServerResponse> indexHandler = _ -> ok()
                .contentType(MediaType.TEXT_HTML)
                .cacheControl(CacheControl.noStore())
                .body(index, String.class);
        return RouterFunctions
                .route(RequestPredicates.GET("/"), indexHandler)
                .andRoute(RequestPredicates.GET("/index.html"), indexHandler)
                .andRoute(RequestPredicates.GET("/gruppe/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/minside/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/maler/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/importer/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/testnorge/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/endringsmelding/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/organisasjoner/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/tenororganisasjoner/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/login/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/bruker/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/team/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/dollysoek/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/dashboard/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/tenorpersoner/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/orgtilgang/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/levendearbeidsforhold/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/infostriper/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/nyansettelser/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/identvalidator/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/oversikt/**"), indexHandler);
    }
}
