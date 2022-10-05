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

export const kodeverkMock = {
	name: 'Tema',
	koder: [
		{
			label: 'Testytest',
			value: 'test',
			gyldigFra: '2010-01-01',
			gyldigTil: '9999-12-31',
		},
	],
}

export const varslingerResponseMock = [{ varslingId: 'VELKOMMEN_TIL_DOLLY', fom: null, tom: null }]

export const varslingerRequestMock = ['VELKOMMEN_TIL_DOLLY']

export const malerMock = { malbestillinger: ['Cafe, Test', []] }

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

export const miljoeMock = '["q1","q2","q4","q5","qx","t0","t1","t13","t2","t3","t4","t5","t6","u5"]'
