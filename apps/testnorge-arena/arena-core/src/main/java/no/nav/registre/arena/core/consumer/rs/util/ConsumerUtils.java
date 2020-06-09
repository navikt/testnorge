package no.nav.registre.arena.core.consumer.rs.util;

import static java.lang.Integer.parseInt;
import static no.nav.registre.arena.core.consumer.rs.util.Identtype.BOST;
import static no.nav.registre.arena.core.consumer.rs.util.Identtype.DNR;
import static no.nav.registre.arena.core.consumer.rs.util.Identtype.FNR;

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

    public RequestEntity createPostRequest(
            UriTemplate uri,
            int antallMeldinger
    ) {
        List<RettighetSyntRequest> requester = new ArrayList<>(antallMeldinger);
        for (int i = 0; i < antallMeldinger; i++) {
            LocalDate startDato = LocalDate.now().minusMonths(rand.nextInt(12));
            opprettRequest(startDato, requester);
        }
        return RequestEntity
                .post(uri.expand())
                .body(requester);
    }

    public RequestEntity createPostRequest(
            UriTemplate uri,
            int antallMeldinger,
            LocalDate startDatoLimit
    ) {
        List<RettighetSyntRequest> requester = new ArrayList<>(antallMeldinger);
        for (int i = 0; i < antallMeldinger; i++) {
            LocalDate startDato = startDatoLimit.minusMonths(rand.nextInt(12));
            opprettRequest(startDato, requester);
        }

        return RequestEntity
                .post(uri.expand())
                .body(requester);
    }

    private void opprettRequest(LocalDate startDato, List<RettighetSyntRequest> requester) {
        LocalDate sluttDato = startDato.plusDays(rand.nextInt(365 / 2) + (365 / (long) 2));
        requester.add(RettighetSyntRequest.builder()
                .fraDato(startDato)
                .tilDato(sluttDato)
                .utfall(UTFALL_JA)
                .vedtakTypeKode(VEDTAK_TYPE_KODE_O)
                .vedtakDato(startDato)
                .build());
    }


    public static LocalDate getFoedselsdatoFraFnr(String ident) {
        var year = getFullYear(ident);
        var month = parseInt(ident.substring(2, 4));
        var day = parseInt(ident.substring(0, 2));

        if (DNR.equals(getIdentType(ident))) {
            day = day - 40;
        }
        if (BOST.equals(getIdentType(ident))) {
            month = month - 20;
        }

        return LocalDate.of(year, month, day);
    }

    private static Identtype getIdentType(String ident) {
        if (parseInt(ident.substring(0, 1)) > 3) {
            return DNR;
        } else if (parseInt(ident.substring(2, 3)) > 1) {
            return BOST;
        }
        return FNR;
    }

    /**
     * INDIVID(POS 7-9) 500-749 OG ÅR > 54 => ÅRHUNDRE = 1800
     * INDIVID(POS 7-9) 000-499            => ÅRHUNDRE = 1900
     * INDIVID(POS 7-9) 900-999 OG ÅR > 39 => ÅRHUNDRE = 1900
     * INDIVID(POS 7-9) 500-999 OG ÅR < 40 => ÅRHUNDRE = 2000
     */
    private static int getFullYear(String ident) {
        var year = parseInt(ident.substring(4, 6));
        var individ = parseInt(ident.substring(6, 9));

        // Find century
        int century;
        if (individ < 500 || (individ >= 900 && year > 39)) {
            century = 1900;
        } else if (year < 40) {
            century = 2000;
        } else if (individ < 750 && year > 54) {
            century = 1800;
        } else {
            century = 2000;
        }

        return LocalDate.of(century + year, 1, 1).getYear();
    }
}
