package no.nav.identpool.repository;

import org.springframework.stereotype.Service;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

import no.nav.identpool.domain.Rekvireringsstatus;
import no.nav.identpool.rs.v1.HentIdenterRequest;

@Service
public class IdentPredicateUtil {

    public Predicate lagPredicateFraRequest(HentIdenterRequest hentIdenterRequest) {
        //TODO: Ikke sett denne før, hvordan virker dette?
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
