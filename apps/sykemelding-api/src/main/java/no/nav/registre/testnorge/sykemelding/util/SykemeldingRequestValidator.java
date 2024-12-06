package no.nav.registre.testnorge.sykemelding.util;

import no.nav.testnav.libs.dto.sykemelding.v1.SykemeldingDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;

import static java.util.Objects.isNull;

public class SykemeldingRequestValidator {

    public static void validate(SykemeldingDTO dto) {
        var missingFields = new ArrayList<String>();

        if (isNull(dto.getHovedDiagnose())) {
            missingFields.add("hovedDiagnose");
        }
        if (isNull(dto.getBiDiagnoser())) {
            missingFields.add("biDiagnoser");
        }
        if (isNull(dto.getArbeidsgiver())) {
            missingFields.add("arbeidsgiver");
        }
        if (isNull(dto.getPerioder())) {
            missingFields.add("perioder");
        }
        if (isNull(dto.getHelsepersonell())) {
            missingFields.add("helsepersonell");
        }

        if (!missingFields.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.join(", ", missingFields));
        }
    }
}