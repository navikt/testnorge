package no.nav.dolly.bestilling.brregstub.mapper;

import java.util.Map;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.brregstub.BrregstubConsumer;
import no.nav.dolly.bestilling.brregstub.domain.BrregRequestWrapper;
import no.nav.dolly.domain.resultset.breg.RsBregdata;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.service.TpsfPersonCache;

@Service
@RequiredArgsConstructor
public class RolleUtskriftMapper {

    private final MapperFacade mapperFacade;
    private final TpsfPersonCache tpsfPersonCache;
    private final BrregstubConsumer brregstubConsumer;

    public BrregRequestWrapper map(RsBregdata bregdata, TpsPerson tpsPerson) {

        tpsfPersonCache.fetchIfEmpty(tpsPerson);
        Map<String, String> koderoller = brregstubConsumer.getKodeRoller().getBody();
        return mapperFacade.map(new BregPerson(bregdata, tpsPerson, koderoller), BrregRequestWrapper.class);
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
