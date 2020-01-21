package no.nav.registre.arena.core.consumer.rs;

import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import no.nav.registre.arena.core.consumer.rs.request.RettighetSyntRequest;

@Service
@RequiredArgsConstructor
public class ConsumerUtils {

    private final Random rand;

    public static final String VEDTAK_TYPE_KODE_O = "O";
    public static final String UTFALL_JA = "JA";
    public static final String NAV_CALL_ID = "ORKESTRATOREN";
    public static final String NAV_CONSUMER_ID = "ORKESTRATOREN";

    public RequestEntity createPostRequest(UriTemplate uri, int antallMeldinger) {
        List<RettighetSyntRequest> requester = new ArrayList<>(antallMeldinger);
        for (int i = 0; i < antallMeldinger; i++) {
            LocalDate startDato = LocalDate.now().minusMonths(rand.nextInt(12));
            LocalDate sluttDato = startDato.plusDays(rand.nextInt(365));
            requester.add(RettighetSyntRequest.builder()
                    .fraDato(startDato)
                    .tilDato(sluttDato)
                    .utfall(UTFALL_JA)
                    .vedtakTypeKode(VEDTAK_TYPE_KODE_O)
                    .vedtakDato(startDato)
                    .build());
        }
        return RequestEntity
                .post(uri.expand())
                .body(requester);
    }
}
