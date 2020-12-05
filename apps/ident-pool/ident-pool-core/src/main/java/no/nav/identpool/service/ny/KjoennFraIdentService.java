package no.nav.identpool.service.ny;

import static java.lang.Integer.parseInt;
import static no.nav.identpool.domain.Kjoenn.KVINNE;
import static no.nav.identpool.domain.Kjoenn.MANN;

import java.security.SecureRandom;
import org.springframework.stereotype.Service;

import no.nav.identpool.domain.Kjoenn;

@Service
public class KjoennFraIdentService {

    private static SecureRandom secureRandom = new SecureRandom();

    public Kjoenn getKjoenn(String ident){

        if (parseInt(ident.substring(6,10)) == 0) {
            return secureRandom.nextBoolean() ? MANN : KVINNE;
        }

        int kjoennNummer = parseInt(ident.substring(8,9));
        return kjoennNummer % 2 == 0 ? KVINNE : MANN;
    }
}