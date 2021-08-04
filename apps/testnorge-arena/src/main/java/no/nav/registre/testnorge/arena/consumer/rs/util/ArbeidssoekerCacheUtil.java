package no.nav.registre.testnorge.arena.consumer.rs.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Arbeidsoeker;

@Component
public class ArbeidssoekerCacheUtil {

    private static final int OPPDATERINGSINTERVALL_I_SEKUNDER = 3600;

    private final Map<String, ArbeidssoekerCacheElement> eksisterendeArbeidssoekereCache;

    public ArbeidssoekerCacheUtil() {
        this.eksisterendeArbeidssoekereCache = new HashMap<>();
    }

    public List<Arbeidsoeker> hentArbeidssoekere(String key) {
        var element = eksisterendeArbeidssoekereCache.get(key);
        if (element != null && ChronoUnit.SECONDS.between(element.getSistOppdatert(), LocalDateTime.now()) <= OPPDATERINGSINTERVALL_I_SEKUNDER) {
            return new ArrayList<>(element.getArbeidssoekere());
        }
        return Collections.emptyList();
    }

    public void oppdaterCache(
            String key,
            List<Arbeidsoeker> arbeidssoekere
    ) {
        eksisterendeArbeidssoekereCache.put(key, new ArbeidssoekerCacheElement(arbeidssoekere));
    }
}
