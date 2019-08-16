package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.MapperFactory;
import no.nav.dolly.domain.resultset.udistub.RsUdiForholdData;
import no.nav.dolly.domain.resultset.udistub.UdiForholdRequest;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

@Component
public class UdiForholdMappingStrategy implements MappingStrategy {

	@Override
	public void register(MapperFactory factory) {
		factory.classMap(RsUdiForholdData.class, UdiForholdRequest.class)
				.byDefault()
				.register();
	}
}
