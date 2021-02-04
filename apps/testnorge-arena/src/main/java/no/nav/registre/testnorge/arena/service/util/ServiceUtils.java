package no.nav.registre.testnorge.arena.service.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.testnorge.consumers.hodejegeren.response.KontoinfoResponse;
import no.nav.registre.testnorge.consumers.hodejegeren.response.internal.DataRequest;
import no.nav.registre.testnorge.consumers.hodejegeren.response.internal.HistorikkRequest;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.forvalter.Adresse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.forvalter.Forvalter;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.forvalter.Konto;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;

@Slf4j
@Service
@RequiredArgsConstructor
@DependencyOn("testnorge-hodejegeren")
public class ServiceUtils {

    public static final String BEGRUNNELSE = "Syntetisert rettighet";
    public static final int MIN_ALDER_AAP = 18;
    public static final int MAX_ALDER_AAP = 67;
    public static final int MIN_ALDER_UNG_UFOER = 18;
    public static final int MAX_ALDER_UNG_UFOER = 36;
    private static final String KILDE_ARENA = "arena";

    public static final String AKTIVITETSFASE_SYKEPENGEERSTATNING = "SPE";

    private final HodejegerenConsumer hodejegerenConsumer;
    private final Random rand;


    public static Forvalter buildForvalter(KontoinfoResponse identMedKontoinfo) {
        Konto konto = Konto.builder()
                .kontonr(identMedKontoinfo.getKontonummer())
                .build();
        Adresse adresse = Adresse.builder()
                .adresseLinje1(identMedKontoinfo.getAdresseLinje1())
                .adresseLinje2(identMedKontoinfo.getAdresseLinje2())
                .adresseLinje3(identMedKontoinfo.getAdresseLinje3())
                .fodselsnr(identMedKontoinfo.getFnr())
                .landkode(identMedKontoinfo.getLandkode())
                .navn(identMedKontoinfo.getLandkode())
                .postnr(identMedKontoinfo.getPostnr())
                .build();
        return Forvalter.builder()
                .gjeldendeKontonr(konto)
                .utbetalingsadresse(adresse)
                .build();
    }

    public KodeMedSannsynlighet velgKodeBasertPaaSannsynlighet(List<KodeMedSannsynlighet> koder) {
        int totalSum = 0;
        for (var a : koder) {
            totalSum += a.getSannsynlighet();
        }

        int index = rand.nextInt(totalSum);
        int sum = 0;
        int i = 0;
        while (sum < index) {
            sum += koder.get(i++).getSannsynlighet();
        }

        return koder.get(Math.max(0, i - 1));
    }

    public void lagreIHodejegeren(Map<String, List<NyttVedtakResponse>> identerMedOpprettedeRettigheter) {
        List<DataRequest> identMedData = new ArrayList<>();
        for (var identMedRettigheter : identerMedOpprettedeRettigheter.entrySet()) {
            var rettigheterSomObject = new ArrayList<>();
            for (var nyttVedtakResponse : identMedRettigheter.getValue()) {
                var nyeRettigheterAap = nyttVedtakResponse.getNyeRettigheterAap();
                if (nyeRettigheterAap != null) {
                    rettigheterSomObject.addAll(nyeRettigheterAap);
                }
            }
            if (!rettigheterSomObject.isEmpty()) {
                var dataRequest = new DataRequest();
                dataRequest.setId(identMedRettigheter.getKey());
                dataRequest.setData(rettigheterSomObject);
                identMedData.add(dataRequest);
            } else {
                log.warn("Kunne ikke opprette historikk i hodejegeren på ident {}", identMedRettigheter.getKey());
            }
        }
        if (!identMedData.isEmpty()) {
            var historikkRequest = new HistorikkRequest();
            historikkRequest.setKilde(KILDE_ARENA);
            historikkRequest.setIdentMedData(identMedData);
            hodejegerenConsumer.saveHistory(historikkRequest);
        }
    }

}
