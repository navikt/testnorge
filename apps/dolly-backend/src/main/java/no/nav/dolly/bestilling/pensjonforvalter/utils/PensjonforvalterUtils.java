package no.nav.dolly.bestilling.pensjonforvalter.utils;

import lombok.experimental.UtilityClass;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonVedtakResponse;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.domain.PdlPersonBolk;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.apache.poi.util.StringUtil.isNotBlank;

@UtilityClass
public class PensjonforvalterUtils {

    public static final String SEP = "$";
    public static final String IDENT = "ident";
    public static final String MILJOER = "miljoer";
    public static final String NAV_ENHET = "navEnhet";
    public static final String SYSTEM = "PESYS";
    public static final String PENSJON_FORVALTER = "PensjonForvalter#";
    public static final String SAMBOER_REGISTER = "Samboer#";
    public static final String POPP_INNTEKTSREGISTER = "PoppInntekt#";
    public static final String TP_FORHOLD = "TpForhold#";
    public static final String PEN_ALDERSPENSJON = "AP#";
    public static final String PEN_UFORETRYGD = "Ufoer#";
    public static final String PEN_PENSJONSAVTALE = "Pensjonsavtale#";
    public static final String PEN_AFP_OFFENTLIG = "AfpOffentlig#";
    public static final String ANNET = "Annet#";
    public static final String PERIODE = "/periode/";

    public static boolean hasVedtak(List<PensjonVedtakResponse> pensjonsvedtak, PensjonVedtakResponse.SakType type) {

        return pensjonsvedtak.stream().anyMatch(entry -> entry.getSakType() == type &&
                entry.getSisteOppdatering().contains("opprettet"));
    }

    public static String getPeriodeId(String lenke) {
        return lenke.substring(lenke.indexOf(PERIODE) + PERIODE.length())
                .replace("/annuller", "");
    }

    public static Flux<PensjonforvalterResponse> getStatus(String miljoe, Integer status, String reasonPhrase) {

        return Flux.just(PensjonforvalterResponse.builder()
                .status(List.of(PensjonforvalterResponse.ResponseEnvironment.builder()
                        .miljo(miljoe)
                        .response(PensjonforvalterResponse.Response.builder()
                                .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                        .status(status)
                                        .reasonPhrase(reasonPhrase)
                                        .build())
                                .message(reasonPhrase)
                                .build())
                        .build()))
                .build());
    }

    public static String getGeografiskTilknytning(PdlPersonBolk.GeografiskTilknytning tilknytning) {

        if (isNotBlank(tilknytning.getGtKommune())) {
            return tilknytning.getGtKommune();

        } else if (isNotBlank(tilknytning.getGtBydel())) {
            return tilknytning.getGtBydel();

        } else {
            return "030102";
        }
    }

}
