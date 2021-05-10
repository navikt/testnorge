package no.nav.pdl.forvalter.service.command;

import lombok.RequiredArgsConstructor;

import java.security.SecureRandom;
import java.util.concurrent.Callable;

import static java.lang.Integer.parseInt;
import static no.nav.pdl.forvalter.domain.PdlKjoenn.Kjoenn;
import static no.nav.pdl.forvalter.domain.PdlKjoenn.Kjoenn.KVINNE;
import static no.nav.pdl.forvalter.domain.PdlKjoenn.Kjoenn.MANN;

@RequiredArgsConstructor
public class KjoennFraIdentCommand implements Callable<Kjoenn> {

    private static final SecureRandom secureRandom = new SecureRandom();

    private final String ident;

    @Override
    public Kjoenn call() {

        if (parseInt(ident.substring(6, 10)) == 0) {
            return secureRandom.nextBoolean() ? MANN : KVINNE;
        }

        int kjoennNummer = parseInt(ident.substring(8, 9));
        return kjoennNummer % 2 == 0 ? KVINNE : MANN;
    }
}