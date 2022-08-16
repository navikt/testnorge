package no.nav.dolly.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
@RequiredArgsConstructor
public class CounterCustomRegistry {

    private static final String BESTILLING_TAG = "bestillingTag";

    private final MeterRegistry registry;

    public void invoke(String name, List<String> tags) {

        tags.forEach(tag ->
                registry.counter(name, "type", tag).increment()
        );
    }

    public void invoke(RsDollyUtvidetBestilling bestilling) {

        List<String> tags = new ArrayList<>();

        if (nonNull(bestilling.getPdlforvalter())) {
            addTag(tags, nonNull(bestilling.getPdlforvalter().getFalskIdentitet()), "FALSKID");
            addTag(tags, nonNull(bestilling.getPdlforvalter().getKontaktinformasjonForDoedsbo()), "KONTAKTDØDSBO");
            addTag(tags, nonNull(bestilling.getPdlforvalter().getUtenlandskIdentifikasjonsnummer()), "UTENLANDSID");
        }

        if (nonNull(bestilling.getPensjonforvalter())) {
            addTag(tags, nonNull(bestilling.getPensjonforvalter().getInntekt()), "POPPINNTEKT");
            addTag(tags, null != bestilling.getPensjonforvalter().getTp() && !bestilling.getPensjonforvalter().getTp().isEmpty(), "TP-ORDNING");
        }

        addTag(tags, !bestilling.getAareg().isEmpty(), "AAREG");
        addTag(tags, nonNull(bestilling.getArenaforvalter()), "ARENA");
        addTag(tags, !bestilling.getSigrunstub().isEmpty(), "SIGRUNSTUB");
        addTag(tags, !bestilling.getInstdata().isEmpty(), "INST");
        addTag(tags, nonNull(bestilling.getInntektstub()), "INNTKSTUB");
        addTag(tags, nonNull(bestilling.getKrrstub()), "KRRSTUB");
        addTag(tags, nonNull(bestilling.getUdistub()), "UDISTUB");
        addTag(tags, nonNull(bestilling.getInntektsmelding()), "INNTEKTSMELDING");
        addTag(tags, nonNull(bestilling.getDokarkiv()), "DOKARKIV");
        addTag(tags, nonNull(bestilling.getSykemelding()), "SYKEMELDING");
        addTag(tags, nonNull(bestilling.getBrregstub()), "BRREGSTUB");

        addTag(tags, isNotBlank(bestilling.getMalBestillingNavn()), "MALBESTILLING");

        invoke(BESTILLING_TAG, tags);
    }

    private static void addTag(List tags, boolean check, String tag) {
        if (check) {
            tags.add(tag);
        }
    }
}
