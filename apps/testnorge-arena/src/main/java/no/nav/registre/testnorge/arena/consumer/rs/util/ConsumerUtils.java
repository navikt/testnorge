package no.nav.registre.testnorge.arena.consumer.rs.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetSyntRequest;

@Service
@RequiredArgsConstructor
public class ConsumerUtils {

    private final Random rand;

    public static final String VEDTAK_TYPE_KODE_O = "O";
    public static final String UTFALL_JA = "JA";
    public static final String EIER = "ORKESTRATOREN";

    public List<RettighetSyntRequest> createSyntRequest(int antallMeldinger) {
        List<RettighetSyntRequest> requester = new ArrayList<>(antallMeldinger);
        for (int i = 0; i < antallMeldinger; i++) {
            LocalDate startDato = LocalDate.now().minusMonths(rand.nextInt(12));
            opprettRequest(startDato, requester);
        }
        return requester;
    }

    public List<RettighetSyntRequest> createSyntRequest(int antallMeldinger, LocalDate startDatoLimit) {
        List<RettighetSyntRequest> requester = new ArrayList<>(antallMeldinger);
        for (int i = 0; i < antallMeldinger; i++) {
            LocalDate startDato = startDatoLimit.minusMonths(rand.nextInt(12));
            opprettRequest(startDato, requester);
        }
        return requester;
    }

    public List<RettighetSyntRequest> createSyntRequest(LocalDate startDato, LocalDate sluttDato) {
        return new ArrayList<>(Collections.singletonList(
                RettighetSyntRequest.builder()
                        .fraDato(startDato.toString())
                        .tilDato(sluttDato.toString())
                        .utfall(UTFALL_JA)
                        .vedtakTypeKode(VEDTAK_TYPE_KODE_O)
                        .vedtakDato(startDato.toString())
                        .build()
        ));
    }

    private void opprettRequest(LocalDate startDato, List<RettighetSyntRequest> requester) {
        LocalDate sluttDato = startDato.plusDays(rand.nextInt(365 / 2) + (365 / (long) 2));
        requester.add(RettighetSyntRequest.builder()
                .fraDato(startDato.toString())
                .tilDato(sluttDato.toString())
                .utfall(UTFALL_JA)
                .vedtakTypeKode(VEDTAK_TYPE_KODE_O)
                .vedtakDato(startDato.toString())
                .build());
    }
}
