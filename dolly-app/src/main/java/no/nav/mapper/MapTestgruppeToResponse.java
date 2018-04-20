package no.nav.mapper;

import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import no.nav.api.response.TestgruppeResponse;
import no.nav.jpa.Testgruppe;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
public class MapTestgruppeToResponse {
	private BoundMapperFacade<Testgruppe, TestgruppeResponse> mapper = constructMapper();
	
	public TestgruppeResponse map(Testgruppe testgruppe) {
		return mapper.map(testgruppe);
	}
	
	private BoundMapperFacade<Testgruppe, TestgruppeResponse> constructMapper() {
		MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
		mapperFactory.classMap(Testgruppe.class, TestgruppeResponse.class)
				.field("opprettetAv.navIdent","opprettetAvNavIdent")
				.field("sistEndretAv.navIdent","sistEndretAvNavIdent")
				.field("teamtilhoerighet.navn","tilhoererTeamnavn")
				.customize(new TestgruppeCustomMapper())
				.byDefault().register();
		mapperFactory.getConverterFactory().registerConverter(new PassThroughConverter(LocalDateTime.class));
		return mapperFactory.getMapperFacade(Testgruppe.class, TestgruppeResponse.class);
	}
}
