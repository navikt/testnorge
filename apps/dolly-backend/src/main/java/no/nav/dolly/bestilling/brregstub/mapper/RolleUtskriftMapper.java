package no.nav.dolly.bestilling.brregstub.mapper;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.brregstub.domain.RolleoversiktTo;
import no.nav.dolly.domain.resultset.breg.RsBregdata;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.service.DollyPersonCache;

@Service
@RequiredArgsConstructor
public class RolleUtskriftMapper {

    private final MapperFacade mapperFacade;
    private final DollyPersonCache dollyPersonCache;

    public RolleoversiktTo map(RsBregdata bregdata, DollyPerson dollyPerson) {

        dollyPersonCache.fetchIfEmpty(dollyPerson);
        return mapperFacade.map(new BregPerson(bregdata, dollyPerson), RolleoversiktTo.class);
    }

    @Getter
    @Service
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BregPerson {

        private RsBregdata bregdata;
        private DollyPerson dollyPerson;
    }
}
