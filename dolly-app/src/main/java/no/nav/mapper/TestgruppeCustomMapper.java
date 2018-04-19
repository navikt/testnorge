package no.nav.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import no.nav.api.response.TestgruppeResponse;
import no.nav.jpa.Testgruppe;
import no.nav.jpa.Testident;

import java.util.stream.Collectors;

public class TestgruppeCustomMapper extends CustomMapper<Testgruppe,TestgruppeResponse>{
	@Override
	public void mapAtoB(Testgruppe a, TestgruppeResponse b, MappingContext context) {
		if ( a.getTestidenter()!=null && a.getTestidenter().size() > 0) {
			b.setTestidenterID(a.getTestidenter().stream().map(Testident::getIdent).collect(Collectors.toSet()));
		}
	}
	@Override
	public void mapBtoA(TestgruppeResponse b, Testgruppe a, MappingContext context) {	}
}
