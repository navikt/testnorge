package no.nav.identpool.ident.repository;

import org.springframework.stereotype.Service;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

import no.nav.identpool.ident.domain.Kjoenn;

import no.nav.identpool.ident.domain.Rekvireringsstatus;
import no.nav.identpool.ident.rest.v1.HentIdenterRequest;

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
