package no.nav.dolly.web.provider.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Controller
public class ForwardAndRedirectController {

    @Bean
    public RouterFunction<ServerResponse> htmlRouter(@Value("classpath:/static/index.html") Resource html) {
        HandlerFunction<ServerResponse> indexHandler = request -> ok().contentType(MediaType.TEXT_HTML).bodyValue(html);
        return RouterFunctions
                .route(RequestPredicates.GET("/gruppe/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/minside/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/maler/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/testnorge/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/endringsmelding/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/organisasjoner/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/tenororganisasjoner/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/login/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/bruker/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/team/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/dollysoek/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/tenorpersoner/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/orgtilgang/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/levendearbeidsforhold/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/infostriper/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/nyansettelser/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/identvalidator/**"), indexHandler)
                .andRoute(RequestPredicates.GET("/oversikt/**"), indexHandler);
    }
}
