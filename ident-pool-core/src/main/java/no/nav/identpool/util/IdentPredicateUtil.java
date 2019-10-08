package no.nav.identpool.util;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

import no.nav.identpool.domain.QIdent;
import no.nav.identpool.domain.Rekvireringsstatus;
import no.nav.identpool.rs.v1.support.HentIdenterRequest;

public final class IdentPredicateUtil {

    private IdentPredicateUtil() {
    }

    public static Predicate lagPredicateFraRequest(HentIdenterRequest hentIdenterRequest, Rekvireringsstatus rekvireringsstatus) {
        QIdent qIdent = QIdent.ident;

        BooleanBuilder booleanBuilder = new BooleanBuilder(qIdent.isNotNull());
        booleanBuilder.and(qIdent.rekvireringsstatus.eq(rekvireringsstatus));
        if (hentIdenterRequest.getFoedtFoer() != null) {
            booleanBuilder.and(qIdent.foedselsdato.before(hentIdenterRequest.getFoedtFoer()));
        }
        if (hentIdenterRequest.getFoedtEtter() != null) {
            booleanBuilder.and(qIdent.foedselsdato.after(hentIdenterRequest.getFoedtEtter()));
        }
        if (hentIdenterRequest.getIdenttype() != null) {
            booleanBuilder.and(qIdent.identtype.eq(hentIdenterRequest.getIdenttype()));
        }
        if (hentIdenterRequest.getKjoenn() != null) {
            booleanBuilder.and(qIdent.kjoenn.eq(hentIdenterRequest.getKjoenn()));
        }
        return booleanBuilder;
    }

}
