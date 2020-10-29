package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain;

import lombok.RequiredArgsConstructor;

import java.util.Random;
import java.util.Set;

import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.dto.KodeverkDTO;

@RequiredArgsConstructor
public class Kodeverk {
    private final KodeverkDTO dto;
    private final Random random = new Random();

    public String getRandomKode() {
        Set<String> koder = dto.getBetydninger().keySet();
        String[] array = koder.toArray(new String[0]);
        return array[random.nextInt(koder.size())];
    }
}
