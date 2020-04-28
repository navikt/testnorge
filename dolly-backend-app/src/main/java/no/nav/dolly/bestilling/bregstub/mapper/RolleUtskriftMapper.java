package no.nav.dolly.bestilling.bregstub.mapper;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.bregstub.domain.RolleoversiktTo;
import no.nav.dolly.domain.resultset.breg.RsBregdata;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.service.TpsfPersonCache;

@Service
@RequiredArgsConstructor
public class RolleUtskriftMapper {

    private final MapperFacade mapperFacade;
    private final TpsfPersonCache tpsfPersonCache;

    public RolleoversiktTo map(RsBregdata bregdata, TpsPerson tpsPerson) {

        tpsfPersonCache.fetchIfEmpty(tpsPerson);
        return mapperFacade.map(new BregPerson(bregdata, tpsPerson), RolleoversiktTo.class);
    }

    @Getter
    @Service
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BregPerson {

        private RsBregdata bregdata;
        private TpsPerson tpsPerson;
    }
}
