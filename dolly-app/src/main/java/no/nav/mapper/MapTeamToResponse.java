package no.nav.mapper;

import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import no.nav.api.response.TeamResponse;
import no.nav.jpa.Team;

import java.time.LocalDateTime;

public class MapTeamToResponse {
	
	private static BoundMapperFacade<Team, TeamResponse> mapper = constructMapper();
	
	public static TeamResponse map(Team team) {
		return mapper.map(team);
	}
	
	private static BoundMapperFacade<Team, TeamResponse> constructMapper() {
		MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
		mapperFactory.classMap(Team.class, TeamResponse.class)
				.customize(new TeamCustomMapper()).byDefault().register();
		mapperFactory.getConverterFactory().registerConverter(new PassThroughConverter(LocalDateTime.class));
		return mapperFactory.getMapperFacade(Team.class, TeamResponse.class);
	}
}
