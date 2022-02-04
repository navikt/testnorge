package no.nav.testnav.apps.syntvedtakshistorikkservice.service.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceUtils {

    public static final String BEGRUNNELSE = "Syntetisert rettighet";

    public static final int MINIMUM_ALDER = 16;
    public static final int MAKSIMUM_ALDER = 67;

    public static final int MIN_ALDER_AAP = 18;
    public static final int MAX_ALDER_AAP = 67;

    public static final int MIN_ALDER_UNG_UFOER = 18;
    public static final int MAX_ALDER_UNG_UFOER = 36;

    public static final String EIER = "ORKESTRATOREN";

    public static final int SYKEPENGEERSTATNING_MAKS_PERIODE = 6;
    public static final LocalDate ARENA_AAP_UNG_UFOER_DATE_LIMIT = LocalDate.of(2020, 1, 31);
    public static final String AKTIVITETSFASE_SYKEPENGEERSTATNING = "SPE";

    private final Random rand = new Random();

    public KodeMedSannsynlighet velgKodeBasertPaaSannsynlighet(List<KodeMedSannsynlighet> koder) {
        var totalSum = 0;
        for (var a : koder) {
            totalSum += a.getSannsynlighet();
        }

        var index = rand.nextInt(totalSum);
        var sum = 0;
        var i = 0;
        while (sum < index) {
            sum += koder.get(i++).getSannsynlighet();
        }

        return koder.get(Math.max(0, i - 1));
    }

}
