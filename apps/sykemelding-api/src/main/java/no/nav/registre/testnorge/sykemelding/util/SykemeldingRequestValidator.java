package no.nav.registre.testnorge.sykemelding.util;

import no.nav.testnav.libs.dto.sykemelding.v1.SykemeldingDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static java.util.Objects.isNull;

public class SykemeldingRequestValidator {

    public static void validate(SykemeldingDTO dto) {
        if (isNull(dto.getHovedDiagnose())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mangler feltet 'hovedDiagnose'");
        }
        if (isNull(dto.getBiDiagnoser())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mangler feltet 'biDiagnoser'");
        }
        if (isNull(dto.getArbeidsgiver())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mangler feltet 'arbeidsgiver'");
        }
        if (isNull(dto.getPerioder())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mangler feltet 'perioder'");
        }
        if (isNull(dto.getUtdypendeOpplysninger())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mangler feltet 'utdypendeOpplysninger'");
        }
        if (isNull(dto.getHelsepersonell())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mangler feltet 'helsepersonell'");
        }
        if (isNull(dto.getSender())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mangler feltet 'sender'");
        }
    }
}