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

export const varslingerVelkommenResponseMock = [
	{ varslingId: 'VELKOMMEN_TIL_DOLLY', fom: null, tom: null },
]

export const varslingerRequestMock = ['VELKOMMEN_TIL_DOLLY']

export const malerMock = { malbestillinger: ['Cafe, Test', []] }

export const testidentMock = {
	ident: '12345678912',
	beskrivelse: 'Litta kommentar også',
	bestillingId: [1],
	master: 'PDLF',
	ibruk: false,
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
	antallIdenter: 1,
	antallIBruk: 0,
	erEierAvGruppe: true,
	favorittIGruppen: false,
	erLaast: false,
	identer: [testidentMock],
	tags: [],
}

export const backendBestillingerMock = [
	{
		id: 1,
		antallIdenter: 1,
		antallLevert: 0,
		ferdig: true,
		sistOppdatert: '2022-01-01T15:36:35.474418',
		bruker: gjeldendeBrukerMock,
		gruppeId: 1,
		stoppet: false,
		environments: [''],
		status: [
			{
				id: 'PDL_FORVALTER',
				navn: 'Persondataløsningen (PDL)',
				statuser: [
					{
						melding: 'OK',
						identer: ['12345678912'],
					},
				],
			},
		],
		bestilling: {
			pdldata: {
				opprettNyPerson: {
					identtype: 'FNR',
					foedtEtter: null,
					foedtFoer: null,
					alder: null,
					syntetisk: true,
				},
			},
		},
	},
]

export const miljoeMock = '["q1","q2","q4","q5","qx","t0","t1","t13","t2","t3","t4","t5","t6","u5"]'
