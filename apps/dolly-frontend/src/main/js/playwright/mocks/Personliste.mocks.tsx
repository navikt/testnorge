export const mockedBruker = {
	brukerId: '12321312112',
	brukernavn: 'Testbruker',
	brukertype: 'AZURE',
	epost: 'testytest@test.tst',
}
export const mockedPdlForvalter = [
	{
		id: 1,
		person: {
			ident: '12811212312',
			identtype: 'FNR',
			foedselsdato: [
				{
					id: 1,
					kilde: 'Dolly',
					master: 'PDL',
					opprettet: '2024-11-14T14:20:45.905560875Z',
					folkeregistermetadata: {
						ajourholdstidspunkt: '1934-03-19T00:00:00',
						gjeldende: null,
						gyldighetstidspunkt: '1934-03-19T00:00:00',
						opphoerstidspunkt: null,
					},
					foedselsaar: 1934,
					foedselsdato: null,
				},
			],
		},
		relasjoner: [],
		sistOppdatert: '2024-11-14T15:20:46.469824',
	},
]
export const mockedPersonService = {
	data: {
		hentIdenterBolk: [
			{
				ident: '12811212312',
				identer: [
					{
						ident: '12811212312',
						gruppe: 'FOLKEREGISTERIDENT',
						historisk: false,
					},
					{
						ident: '2343423423423',
						gruppe: 'AKTORID',
						historisk: false,
					},
				],
				code: 'ok',
			},
		],
		hentGeografiskTilknytningBolk: [
			{
				ident: '12811212312',
				geografiskTilknytning: {
					gtType: 'KOMMUNE',
					gtLand: null,
					gtKommune: '4648',
					gtBydel: null,
					regel: '2',
				},
				code: 'ok',
			},
		],
		hentPersonBolk: [
			{
				ident: '12811212312',
				person: {
					foedselsdato: [
						{
							foedselsaar: 1934,
							foedselsdato: null,
							folkeregistermetadata: null,
						},
						{
							foedselsaar: 1981,
							foedselsdato: '1981-03-19',
							folkeregistermetadata: {
								aarsak: null,
								ajourholdstidspunkt: '2022-02-09T13:04:48',
								gyldighetstidspunkt: '1981-03-19T13:04:48',
								kilde: 'Synutopia',
								opphoerstidspunkt: null,
								sekvens: null,
							},
						},
					],
					kjoenn: [
						{
							kjoenn: 'MANN',
							folkeregistermetadata: {
								aarsak: null,
								ajourholdstidspunkt: null,
								gyldighetstidspunkt: null,
								kilde: 'KILDE_DSF',
								opphoerstidspunkt: null,
								sekvens: null,
							},
						},
					],
					navn: [
						{
							fornavn: 'UTMERKET',
							mellomnavn: null,
							etternavn: 'RØYSKATT',
							forkortetNavn: null,
							originaltNavn: null,
							gyldigFraOgMed: '2022-02-09',
							folkeregistermetadata: {
								aarsak: 'Patch',
								ajourholdstidspunkt: '2022-02-09T13:04:48',
								gyldighetstidspunkt: '2022-02-09T13:04:48',
								kilde: 'Synutopia',
								opphoerstidspunkt: null,
								sekvens: null,
							},
						},
					],
					folkeregisteridentifikator: [
						{
							identifikasjonsnummer: '12811212312',
							status: 'I_BRUK',
							type: 'FNR',
							folkeregistermetadata: {
								aarsak: null,
								ajourholdstidspunkt: '2020-12-22T17:09:14',
								gyldighetstidspunkt: null,
								kilde: 'KILDE_DSF',
								opphoerstidspunkt: null,
								sekvens: null,
							},
						},
					],
				},
				code: 'ok',
			},
		],
	},
	extensions: {
		warnings: [],
	},
}
export const mockedGruppeBestilling = {
	id: 1,
	antallIdenter: 1,
	antallLevert: 1,
	ferdig: true,
	sistOppdatert: '2024-11-14T15:20:50.531625',
	bruker: mockedBruker,
	gruppeId: 1,
	stoppet: false,
	environments: [],
	status: [
		{
			id: 'PDL_PERSONSTATUS',
			navn: 'Person finnes i PDL',
			statuser: [{ melding: 'OK', identer: ['12811212312'] }],
		},
	],
	bestilling: {
		pdldata: {
			opprettNyPerson: null,
			person: {
				foedselsdato: [
					{
						id: null,
						kilde: 'Dolly',
						master: 'PDL',
						opprettet: null,
						folkeregistermetadata: null,
						hendelseId: null,
						foedselsaar: 1934,
						foedselsdato: null,
					},
				],
			},
		},
	},
}
export const mockedGruppe = {
	id: 1,
	navn: 'Testgruppe',
	hensikt: 'Skal teste',
	opprettetAv: mockedBruker,
	sistEndretAv: mockedBruker,
	tags: [],
	datoEndret: '2023-02-10',
	antallIdenter: 1,
	antallBestillinger: 1,
	bestillinger: [mockedGruppeBestilling],
	antallIBruk: 0,
	erEierAvGruppe: false,
	favorittIGruppen: false,
	erLaast: false,
	identer: [
		{
			ident: '12811212312',
			bestillingId: [1],
			bestillinger: [],
			master: 'PDL',
			ibruk: false,
		},
	],
}
