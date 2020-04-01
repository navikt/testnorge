package no.nav.dolly.metrics;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;

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

        List<String> tags = newArrayList();

        if (nonNull(bestilling.getTpsf())) {
            addTag(tags, nonNull(bestilling.getTpsf().getEgenAnsattDatoFom()) &&
                    isNull(bestilling.getTpsf().getEgenAnsattDatoTom()), "EGENANSATT");

            addTag(tags, "SPSF".equals(bestilling.getTpsf().getSpesreg()), "KODE6");
            addTag(tags, "SPFO".equals(bestilling.getTpsf().getSpesreg()), "KODE7");

            addTag(tags, isNotBlank(bestilling.getTpsf().getSpesreg()) &&
                    !"SPSF".equals(bestilling.getTpsf().getSpesreg()) &&
                    !"SPFO".equals(bestilling.getTpsf().getSpesreg()), "SPESREG");

            addTag(tags, isTrue(bestilling.getTpsf().getHarMellomnavn()), "MELLOMNAVN");
            addTag(tags, isTrue(bestilling.getTpsf().getHarBankkontonr()), "BANKKONTONUMMER");
            addTag(tags, isNotBlank(bestilling.getTpsf().getTelefonnummer_1()), "TELEFONNUMMER");
            addTag(tags, isNotBlank(bestilling.getTpsf().getStatsborgerskap()), "STATSBORGERSKAP");
            addTag(tags, isNotBlank(bestilling.getTpsf().getInnvandretFraLand()), "INNVANDRET");
            addTag(tags, isNotBlank(bestilling.getTpsf().getUtvandretTilLand()), "UTVANDRET");
            addTag(tags, isTrue(bestilling.getTpsf().getErForsvunnet()), "FORSVUNNET");
            addTag(tags, isTrue(bestilling.getTpsf().getUtenFastBopel()), "UTENFASTBOPEL");
            addTag(tags, isNotBlank(bestilling.getTpsf().getSprakKode()), "SPRÅK");
            addTag(tags, nonNull(bestilling.getTpsf().getDoedsdato()), "DØDSDATO");

            if (nonNull(bestilling.getTpsf().getRelasjoner())) {
                addTag(tags, !bestilling.getTpsf().getRelasjoner().getPartnere().isEmpty(), "PARTNER");
                addTag(tags, !bestilling.getTpsf().getRelasjoner().getBarn().isEmpty(), "BARN");
            }
        }
        if (nonNull(bestilling.getPdlforvalter())) {
            addTag(tags, nonNull(bestilling.getPdlforvalter().getFalskIdentitet()), "FALSKID");
            addTag(tags, nonNull(bestilling.getPdlforvalter().getKontaktinformasjonForDoedsbo()), "KONTAKTDØDSBO");
            addTag(tags, nonNull(bestilling.getPdlforvalter().getUtenlandskIdentifikasjonsnummer()), "UTENLANDSID");
        }

        if (nonNull(bestilling.getPensjonforvalter())) {
            addTag(tags, nonNull(bestilling.getPensjonforvalter().getInntekt()), "POPPINNTEKT");
        }

        addTag(tags, !bestilling.getAareg().isEmpty(), "AAREG");
        addTag(tags, nonNull(bestilling.getArenaforvalter()), "ARENA");
        addTag(tags, !bestilling.getSigrunstub().isEmpty(), "SIGRUNSTUB");
        addTag(tags, !bestilling.getInstdata().isEmpty(), "INST");
        addTag(tags, nonNull(bestilling.getInntektstub()), "INNTKSTUB");
        addTag(tags, nonNull(bestilling.getKrrstub()), "KRRSTUB");
        addTag(tags, nonNull(bestilling.getUdistub()), "UDISTUB");

        addTag(tags, isNotBlank(bestilling.getMalBestillingNavn()), "MALBESTILLING");

        invoke(BESTILLING_TAG, tags);
    }

    private static void addTag(List tags, boolean check, String tag) {
        if (check) {
            tags.add(tag);
        }
    }
}
