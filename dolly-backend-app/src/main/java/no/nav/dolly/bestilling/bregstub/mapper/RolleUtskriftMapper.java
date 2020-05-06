package no.nav.dolly.bestilling.bregstub.mapper;

import java.util.Map;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.bregstub.BregstubConsumer;
import no.nav.dolly.bestilling.bregstub.domain.BrregRequestWrapper;
import no.nav.dolly.domain.resultset.breg.RsBregdata;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.service.TpsfPersonCache;

@Service
@RequiredArgsConstructor
public class RolleUtskriftMapper {

    private final MapperFacade mapperFacade;
    private final TpsfPersonCache tpsfPersonCache;
    private final BregstubConsumer bregstubConsumer;

    public BrregRequestWrapper map(RsBregdata bregdata, TpsPerson tpsPerson) {

        tpsfPersonCache.fetchIfEmpty(tpsPerson);
        return mapperFacade.map(new BregPerson(bregdata, tpsPerson, bregstubConsumer.getKodeRoller().getBody().getRoller()), BrregRequestWrapper.class);
    }

    @Getter
    @Service
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BregPerson {

        private RsBregdata bregdata;
        private TpsPerson tpsPerson;
        private Map<String, String> kodeRoller;
    }
}
