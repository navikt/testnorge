package no.nav.identpool.ident.repository;

import java.time.LocalDate;
import org.springframework.stereotype.Service;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

import no.nav.identpool.ident.domain.Kjoenn;
import no.nav.identpool.ident.domain.Rekvireringsstatus;
import no.nav.identpool.ident.rest.v1.HentIdenterRequest;

@Service
public class IdentPredicateUtil {
    private final String PERSONNUMMER_MANN = "^[0-9]{8}[1,3,5,7,9][0-9]{2}$";
    private final String PERSONNUMMER_KVINNE = "^[0-9]{8}[0,2,4,6,8][0-9]{2}$";

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
            if (hentIdenterRequest.getKjoenn().equals(Kjoenn.MANN)) {
                booleanBuilder.and(queryIdentEntity.personidentifikator.matches(PERSONNUMMER_MANN));
            } else if (hentIdenterRequest.getKjoenn().equals(Kjoenn.KVINNE)) {
                booleanBuilder.and(queryIdentEntity.personidentifikator.matches(PERSONNUMMER_KVINNE));
            }
        }
        return booleanBuilder;
    }

    private LocalDate hentFoedselsdagFraNorskIdent(String ident) { //Kun støtte for fnr og dnr
        String aar = (Integer.parseInt(ident.substring(6, 8)) < 50 ? "19" : "20") + ident.substring(4, 6);
        String maaned = ident.substring(2, 4);
        String dag = ident.charAt(0) < 4 ? ident.substring(0, 2) : String.valueOf(Integer.parseInt(ident.substring(0, 1)) - 4) + ident.charAt(1);
        return LocalDate.parse(aar + "-" + maaned + "-" + dag);
    }

}
