export const gjeldendeProfilMock = {
	visningsNavn: 'Cafe, Test',
	epost: 'Testcafe@nav.no',
	avdeling: '1234 Testytest',
	organisasjon: 'TESTCAFE',
	type: 'Testbruker',
}

export const gjeldendeBrukerMock = {
	brukerId: '1234-5678-12',
	brukernavn: 'Cafe, Test',
	brukertype: 'BASIC',
	epost: 'testcafe@nav.no',
}

export const nyGruppeMock = {
	id: 2,
	navn: 'Testcafe testing',
	hensikt: 'Saftig testing med testcafe..',
	opprettetAv: gjeldendeBrukerMock,
	sistEndretAv: gjeldendeBrukerMock,
	datoEndret: '1990-01-12',
	antallIdenter: 0,
	antallIBruk: 0,
	erEierAvGruppe: true,
	favorittIGruppen: false,
	erLaast: false,
	identer: [],
	tags: [],
}

export const gjeldendeGruppeMock = {
	id: 1,
	navn: 'Testytest',
	hensikt: 'Testing av testytest',
	opprettetAv: gjeldendeBrukerMock,
	sistEndretAv: gjeldendeBrukerMock,
	datoEndret: '1980-01-12',
	antallIdenter: 0,
	antallIBruk: 0,
	erEierAvGruppe: true,
	favorittIGruppen: false,
	erLaast: false,
	identer: [],
	tags: [],
}
