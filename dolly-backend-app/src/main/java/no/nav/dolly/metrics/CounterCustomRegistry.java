package no.nav.dolly.metrics;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;
import org.springframework.stereotype.Component;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;

@Component
@RequiredArgsConstructor
public class CounterCustomRegistry {

    private final CollectorRegistry registry;

    public void invoke(String name, String[] tags) {

        Counter.build()
                .name(name)
                .labelNames(tags)
                .register(registry);
    }

    public void invoke(String name, RsDollyUtvidetBestilling bestilling) {

        List<String> labels = newArrayList();

        if (nonNull(bestilling.getTpsf())) {
            addLabel(labels, nonNull(bestilling.getTpsf().getEgenAnsattDatoFom()) &&
                    isNull(bestilling.getTpsf().getEgenAnsattDatoTom()), "EGENANSATT");

            addLabel(labels, "SPSF".equals(bestilling.getTpsf().getSpesreg()), "KODE6");
            addLabel(labels, "SPFO".equals(bestilling.getTpsf().getSpesreg()), "KODE7");

            addLabel(labels, isNotBlank(bestilling.getTpsf().getSpesreg()) &&
                    !"SPSF".equals(bestilling.getTpsf().getSpesreg()) &&
                    !"SPFO".equals(bestilling.getTpsf().getSpesreg()), "SPESREG");

            addLabel(labels, isTrue(bestilling.getTpsf().getHarMellomnavn()), "MELLOMNAVN");
            addLabel(labels, isTrue(bestilling.getTpsf().getHarBankkontonr()), "BANKKONTONUMMER");
            addLabel(labels, isNotBlank(bestilling.getTpsf().getTelefonnummer_1()), "TELEFONNUMMER");
            addLabel(labels, isNotBlank(bestilling.getTpsf().getStatsborgerskap()), "STATSBORGERSKAP");
            addLabel(labels, isNotBlank(bestilling.getTpsf().getInnvandretFraLand()), "INNVANDRET");
            addLabel(labels, isNotBlank(bestilling.getTpsf().getUtvandretTilLand()), "UTVANDRET");
            addLabel(labels, isTrue(bestilling.getTpsf().getErForsvunnet()), "FORSVUNNET");
            addLabel(labels, isTrue(bestilling.getTpsf().getUtenFastBopel()), "UTENFASTBOPEL");
            addLabel(labels, nonNull(bestilling.getTpsf().getDoedsdato()), "DØDSDATO");

            if (nonNull(bestilling.getTpsf().getRelasjoner())) {
                addLabel(labels, !bestilling.getTpsf().getRelasjoner().getPartnere().isEmpty(), "PARTNER");
                addLabel(labels, !bestilling.getTpsf().getRelasjoner().getBarn().isEmpty(), "BARN");
            }
        }
        if (nonNull(bestilling.getPdlforvalter())) {
            addLabel(labels, nonNull(bestilling.getPdlforvalter().getFalskIdentitet()), "FALSKID");
            addLabel(labels, nonNull(bestilling.getPdlforvalter().getKontaktinformasjonForDoedsbo()), "KONTAKTDØDSBO");
            addLabel(labels, nonNull(bestilling.getPdlforvalter().getUtenlandskIdentifikasjonsnummer()), "UTENLANDSID");
        }

        if (nonNull(bestilling.getPensjonforvalter())) {
            addLabel(labels, nonNull(bestilling.getPensjonforvalter().getInntekt()), "POPPINNTEKT");
        }

        addLabel(labels, !bestilling.getAareg().isEmpty(), "AAREG");
        addLabel(labels, nonNull(bestilling.getArenaforvalter()), "ARENA");
        addLabel(labels, !bestilling.getSigrunstub().isEmpty(), "SIGRUNSTUB");
        addLabel(labels, !bestilling.getInstdata().isEmpty(), "INST");
        addLabel(labels, nonNull(bestilling.getInntektstub()), "INNTKSTUB");
        addLabel(labels, nonNull(bestilling.getKrrstub()), "KRRSTUB");
        addLabel(labels, nonNull(bestilling.getUdistub()), "UDISTUB");

        addLabel(labels, isNotBlank(bestilling.getMalBestillingNavn()), "MALBESTILLING");

        invoke(name, labels.toArray(new String[labels.size()]));
    }

    private static void addLabel(List labels, boolean check, String label) {
        if (check) {
            labels.add(label);
        }
    }
}
