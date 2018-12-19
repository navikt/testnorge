package no.nav.identpool.domain;

import org.springframework.stereotype.Service;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

import no.nav.identpool.rs.v1.support.HentIdenterRequest;

//TODO Denne hører ikke hjemme i denne pakken
@Service
public class IdentPredicateUtil {

    public Predicate lagPredicateFraRequest(HentIdenterRequest hentIdenterRequest) {
        QIdent qIdent = QIdent.ident;

        BooleanBuilder booleanBuilder = new BooleanBuilder(qIdent.isNotNull());
        booleanBuilder.and(qIdent.rekvireringsstatus.eq(Rekvireringsstatus.LEDIG));
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
