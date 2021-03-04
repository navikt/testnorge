package no.nav.identpool.service;

import no.nav.identpool.domain.Kjoenn;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

import static java.lang.Integer.parseInt;
import static no.nav.identpool.domain.Kjoenn.KVINNE;
import static no.nav.identpool.domain.Kjoenn.MANN;

@Service
public class KjoennFraIdentService {

    private static final SecureRandom secureRandom = new SecureRandom();

    public Kjoenn getKjoenn(String ident){

        if (parseInt(ident.substring(6,10)) == 0) {
            return secureRandom.nextBoolean() ? MANN : KVINNE;
        }

        int kjoennNummer = parseInt(ident.substring(8,9));
        return kjoennNummer % 2 == 0 ? KVINNE : MANN;
    }
}