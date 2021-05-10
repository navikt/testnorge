package no.nav.pdl.forvalter.service.command;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.domain.Identtype;

import java.util.concurrent.Callable;

import static java.lang.Integer.parseInt;
import static no.nav.pdl.forvalter.domain.Identtype.BOST;
import static no.nav.pdl.forvalter.domain.Identtype.DNR;
import static no.nav.pdl.forvalter.domain.Identtype.FDAT;
import static no.nav.pdl.forvalter.domain.Identtype.FNR;

@RequiredArgsConstructor
public class IdenttypeFraIdentCommand implements Callable<Identtype> {

    private final String ident;

    @Override
    public Identtype call() {

        if (parseInt(ident.substring(6, 10)) == 0) {
            return FDAT;
        } else if ((parseInt(ident.substring(2, 4)) > 20 && parseInt(ident.substring(2, 4)) < 33) ||
                (parseInt(ident.substring(2, 4)) > 60 && parseInt(ident.substring(2, 4)) < 73)) {
            return BOST;
        } else if (parseInt(ident.substring(0, 1)) >= 4) {
            return DNR;
        } else {
            return FNR;
        }
    }
}