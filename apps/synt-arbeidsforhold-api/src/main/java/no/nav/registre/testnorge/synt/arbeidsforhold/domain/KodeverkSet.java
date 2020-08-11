package no.nav.registre.testnorge.synt.arbeidsforhold.domain;

import java.util.Random;
import java.util.Set;

import no.nav.registre.testnorge.synt.arbeidsforhold.consumer.dto.KodeverkDTO;

public class KodeverkSet {
    private final Set<String> koder;
    private final Random random;

    public KodeverkSet(KodeverkDTO dto) {
        this.koder = dto.getBetydninger().keySet();
        random = new Random();
    }

    public boolean contains(String kode) {
        return koder.contains(kode);
    }

    public String getRandomKode() {
        String[] array = koder.toArray(new String[0]);
        return array[random.nextInt(koder.size())];
    }
}
