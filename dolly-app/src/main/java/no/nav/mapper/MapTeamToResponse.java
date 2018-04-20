package no.nav.mapper;

import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import no.nav.api.response.TeamResponse;
import no.nav.jpa.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
public class MapTeamToResponse {

	private TeamCustomMapper teamCustomMapper;
	private BoundMapperFacade<Team, TeamResponse> mapper;
	
	@Autowired
	public MapTeamToResponse(TeamCustomMapper teamCustomMapper) {
		this.teamCustomMapper = teamCustomMapper;
		this.mapper = constructMapper();
	}
	
	public TeamResponse map(Team team) {
		return mapper.map(team);
	}
	
	private BoundMapperFacade<Team, TeamResponse> constructMapper() {
		MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
		mapperFactory.classMap(Team.class, TeamResponse.class)
				.field("eier.navIdent","eierensNavIdent")
				.customize(teamCustomMapper).byDefault().register();
		mapperFactory.getConverterFactory().registerConverter(new PassThroughConverter(LocalDateTime.class));
		return mapperFactory.getMapperFacade(Team.class, TeamResponse.class);
	}
}
