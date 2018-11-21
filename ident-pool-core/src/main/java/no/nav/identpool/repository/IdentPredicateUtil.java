package no.nav.identpool.repository;

import org.springframework.stereotype.Service;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

import no.nav.identpool.domain.Rekvireringsstatus;
import no.nav.identpool.rs.v1.support.HentIdenterRequest;

//TODO Denne hører ikke hjemme i denne pakken
@Service
public class IdentPredicateUtil {

    public Predicate lagPredicateFraRequest(HentIdenterRequest hentIdenterRequest) {
        QIdentEntity queryIdentEntity = QIdentEntity.identEntity;

        BooleanBuilder booleanBuilder = new BooleanBuilder(queryIdentEntity.isNotNull());
        booleanBuilder.and(queryIdentEntity.rekvireringsstatus.eq(Rekvireringsstatus.LEDIG));
        if (hentIdenterRequest.getFoedtFoer() != null) {
            booleanBuilder.and(queryIdentEntity.foedselsdato.before(hentIdenterRequest.getFoedtFoer()));
        }
        if (hentIdenterRequest.getFoedtEtter() != null) {
            booleanBuilder.and(queryIdentEntity.foedselsdato.after(hentIdenterRequest.getFoedtEtter()));
        }
        if (hentIdenterRequest.getIdenttype() != null) {
            booleanBuilder.and(queryIdentEntity.identtype.eq(hentIdenterRequest.getIdenttype()));
        }
        if (hentIdenterRequest.getKjoenn() != null) {
            booleanBuilder.and(queryIdentEntity.kjoenn.eq(hentIdenterRequest.getKjoenn()));
        }
        return booleanBuilder;
    }
}
