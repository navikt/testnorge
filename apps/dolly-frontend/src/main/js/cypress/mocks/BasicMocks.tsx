export const gjeldendeProfilMock = {
	visningsNavn: 'BeASt, BugTerminator',
	epost: 'BeASt@bugexterminator.no',
	avdeling: '1234 Testytest',
	organisasjon: 'CYPRESS',
	type: 'Testbruker',
}

export const gjeldendeBrukerMock = {
	brukerId: '1234-5678-12',
	brukernavn: 'BeASt, BugTerminator',
	brukertype: 'BASIC',
	epost: 'BeASt@bugexterminator.no',
}

export const personFragmentSearchMock = [
	{
		ident: '12345678912',
		fornavn: 'Teste',
		mellomnavn: null,
		etternavn: 'Testesen',
	},
]

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

const malBestilling = {
	antallIdenter: 1,
	pdldata: {
		opprettNyPerson: {
			identtype: 'FNR',
			foedtEtter: null,
			foedtFoer: null,
			alder: null,
			syntetisk: true,
		},
		person: null,
	},
	tpsMessaging: {},
	skjerming: {
		egenAnsattDatoFom: '2022-09-06T10:24:03',
	},
}
export const brukerMalerMock = [
	{
		id: 1,
		malNavn: 'Teste Cypress',
		bestilling: malBestilling,
		bruker: gjeldendeBrukerMock,
	},
]

export const brukerOrganisasjonMalerMock = [
	{
		id: 1,
		malNavn: 'Organisasjon issues',
	},
	{
		id: 2,
		malNavn: 'Organisasjon issues #2',
	},
	{
		id: 3,
		malNavn: 'Organisasjon issues #3',
	},
]

export const organisasjonerForBrukerMock = [
	{
		id: 1,
		organisasjonsnummer: '123456789',
		enhetstype: 'AS',
		naeringskode: '66.110',
		sektorkode: '3200',
		formaal: 'Teste',
		organisasjonsnavn: 'Lojal Logaritme',
		stiftelsesdato: '2022-12-27',
		telefon: '12345678',
		epost: 'test@test.com',
		nettside: 'testytest.com',
		maalform: 'B',
		adresser: [
			{
				id: 2,
				adressetype: 'PADR',
				adresselinjer: ['Hollendergata 10'],
				postnr: '4616',
				poststed: 'KRISTIANSAND S',
				kommunenr: '4204',
				landkode: 'NO',
				vegadresseId: '219118707',
			},
			{
				id: 3,
				adressetype: 'FADR',
				adresselinjer: ['Bjødnatunvegen 5'],
				postnr: '5784',
				poststed: 'ØVRE EIDFJORD',
				kommunenr: '4619',
				landkode: 'NO',
				vegadresseId: '138078915',
			},
		],
		underenheter: [
			{
				id: 1,
				organisasjonsnummer: '123456789',
				enhetstype: 'BEDR',
				naeringskode: '66.110',
				formaal: 'Testytest',
				organisasjonsnavn: 'Horisontal Feil',
				stiftelsesdato: '2022-12-26',
				telefon: '12345678',
				epost: 'testy@test.com',
				nettside: 'testytest.com',
				maalform: 'B',
				adresser: [
					{
						id: 2,
						adressetype: 'PADR',
						adresselinjer: ['Olaus Fjørtofts vei 71'],
						postnr: '0982',
						poststed: 'OSLO',
						kommunenr: '0301',
						landkode: 'NO',
						vegadresseId: '285747353',
					},
					{
						id: 3,
						adressetype: 'FADR',
						adresselinjer: ['Bjerkebakken 78'],
						postnr: '0757',
						poststed: 'OSLO',
						kommunenr: '0301',
						landkode: 'NO',
						vegadresseId: '285818674',
					},
				],
			},
		],
	},
]
export const organisasjonFraMiljoeMock = {
	q2: {
		organisasjonsnummer: '123456789',
		enhetstype: 'AS',
		organisasjonsnavn: 'LOJAL LOGARITME',
		adresser: [
			{
				id: null,
				adressetype: 'PADR',
				adresselinjer: ['HOLLENDERGATA 10'],
				postnr: '4616',
				poststed: 'KRISTIANSAND S',
				kommunenr: '4204',
				landkode: 'NO',
				vegadresseId: null,
			},
			{
				id: null,
				adressetype: 'FADR',
				adresselinjer: ['BJØDNATUNVEGEN 5'],
				postnr: '5784',
				poststed: 'ØVRE EIDFJORD',
				kommunenr: '4619',
				landkode: 'NO',
				vegadresseId: null,
			},
		],
		underenheter: [
			{
				organisasjonsnummer: '123456789',
				enhetstype: 'BEDR',
				organisasjonsnavn: 'HORISONTAL FEIL',
				adresser: [
					{
						id: null,
						adressetype: 'PADR',
						adresselinjer: ['OLAUS FJØRTOFTS VEI 71'],
						postnr: '0982',
						poststed: 'OSLO',
						kommunenr: '0301',
						landkode: 'NO',
						vegadresseId: null,
					},
					{
						id: null,
						adressetype: 'FADR',
						adresselinjer: ['BJERKEBAKKEN 78'],
						postnr: '0757',
						poststed: 'OSLO',
						kommunenr: '0301',
						landkode: 'NO',
						vegadresseId: null,
					},
				],
			},
		],
	},
}

export const krrstubMock = [
	{
		id: 1,
		gyldigFra: '2023-01-05T15:40:41.696445+01:00',
		personident: '25518548649',
		reservert: false,
		mobil: '',
		mobilOppdatert: '2023-01-05T15:40:41.696458+01:00',
		mobilVerifisert: '2023-01-05T15:40:41.696462+01:00',
		epost: 'daw',
		epostOppdatert: '2023-01-05T15:40:41.696465+01:00',
		epostVerifisert: '2023-01-05T15:40:41.696468+01:00',
		sdpAdresse: '',
		sdpLeverandoer: null,
		registrert: true,
		spraak: '',
		spraakOppdatert: '2023-01-05T15:40:41.696471+01:00',
	},
]

export const backendTransaksjonMock = [
	{
		id: 1,
		bestillingId: 1,
		ident: '12345678912',
		system: 'INNTKMELD',
		miljoe: 'q1',
		transaksjonId: {
			journalpostId: '999999999',
			dokumentInfoId: '888888888',
		},
		datoEndret: '2022-01-01T11:58:58.227916',
	},
	{
		id: 2,
		bestillingId: 1,
		ident: '12345678912',
		system: 'INNTKMELD',
		miljoe: 'q2',
		transaksjonId: {
			journalpostId: '999999999',
			dokumentInfoId: '888888888',
		},
		datoEndret: '2022-01-01T11:58:59.289252',
	},
]

export const sigrunstubMock = {
	responseList: [
		{
			personidentifikator: '12345678912',
			grunnlag: [
				{
					inntektsaar: '2022',
					tjeneste: 'Beregnet skatt',
					grunnlag: 'formuePrimaerbolig',
					verdi: '12345',
					testDataEier: '',
				},
			],
			svalbardGrunnlag: [],
		},
	],
}

export const aaregMock = [
	{
		ansettelsesperiode: {
			periode: {
				fom: '2002-10-03',
			},
		},
		arbeidsavtaler: [{}],
		arbeidsforholdId: '1',
		arbeidsgiver: {
			type: 'ORGANISASJON',
			organisasjonsnummer: '221322132',
		},
		arbeidstaker: {
			type: 'PERSON',
			offentligIdent: '12345678912',
		},
		innrapportertEtterAOrdningen: false,
		permisjonPermitteringer: [],
		type: 'forenkletOppgjoersordning',
	},
]

export const tpsMessagingMock = [
	{
		miljoe: 'q1',
		person: {
			ident: '12345678912',
			identtype: 'FNR',
			kjonn: 'M',
			fornavn: 'Cafe',
			etternavn: 'Test',
			forkortetNavn: 'Test Cafe',
			statsborgerskap: {
				statsborgerskap: 'NOR',
				statsborgerskapRegdato: '1992-01-11T00:00:00',
			},
			spesregDato: '2022-01-01T10:23:34.609245681',
			sivilstand: {
				sivilstand: 'GIFT',
				sivilstandRegdato: '2022-10-03T00:00:00',
			},
			egenAnsattDatoFom: '2022-10-03T00:00:00',
			boadresse: {
				adressetype: 'GATE',
				kommunenr: '3819',
				flyttedato: '1992-01-11T00:00:00',
				postnr: '3697',
				adresse: 'TESTEVEIEN',
				husnummer: '2077',
				gatekode: '01007',
			},
			relasjoner: [
				{
					personRelasjonMed: {
						ident: '09876543210',
						identtype: 'FNR',
						kjonn: 'K',
						fornavn: 'Relasjon',
						etternavn: 'Test',
						forkortetNavn: 'Test Relasjon',
						statsborgerskap: {
							statsborgerskap: 'NOR',
							statsborgerskapRegdato: '1979-02-20T00:00:00',
						},
						spesregDato: '2022-10-06T10:23:34.609135334',
						sivilstand: {
							sivilstand: 'GIFT',
							sivilstandRegdato: '2022-10-03T00:00:00',
						},
						gtRegel: 'A',
						personStatus: 'BOSA',
						importFra: 'TPS',
					},
					relasjonTypeNavn: 'PARTNER',
				},
				{
					personRelasjonMed: {
						ident: '23456789123',
						identtype: 'FNR',
						kjonn: 'K',
						fornavn: 'Barn',
						etternavn: 'Test',
						forkortetNavn: 'Test Barn',
						statsborgerskap: {
							statsborgerskap: 'NOR',
							statsborgerskapRegdato: '2020-01-21T00:00:00',
						},
						spesregDato: '2022-10-06T10:23:34.609153124',
						sivilstand: {
							sivilstand: 'UGIF',
							sivilstandRegdato: '2020-01-21T00:00:00',
						},
						gtRegel: 'A',
						personStatus: 'BOSA',
						importFra: 'TPS',
					},
					relasjonTypeNavn: 'BARN',
				},
			],
			sprakKode: 'AB',
			gtType: 'KNR',
			gtVerdi: '3819',
			gtRegel: 'A',
			personStatus: 'BOSA',
			importFra: 'TPS',
			telefonnumre: [
				{
					telefonnummer: '12345678',
					landkode: '+376',
					telefontype: 'MOBI',
				},
			],
			bankkontonrNorsk: {
				kontonummer: '0043.84.08177',
				kontoRegdato: '2022-10-03T00:00:00',
				tilfeldigKontonummer: null,
			},
		},
		status: 'OK',
	},
]

export const instMock = [
	{
		norskident: '12345678912',
		tssEksternId: '88888888888',
		institusjonstype: 'AS',
		oppholdstype: 'A',
		startdato: '2022-01-01',
		sluttdato: null,
		registrertAv: 'Dolly',
	},
]

export const udistubMock = {
	person: {
		aliaser: [],
		arbeidsadgang: {
			harArbeidsAdgang: 'JA',
			typeArbeidsadgang: 'BESTEMT_ARBEIDSGIVER_ELLER_OPPDRAGSGIVER',
			hjemmel: 'kapplah',
		},
		flyktning: true,
		foedselsDato: '1992-01-11',
		ident: '12345678912',
		navn: {
			etternavn: 'Cafe',
			fornavn: 'Test',
		},
		oppholdStatus: {
			eosEllerEFTAVedtakOmVarigOppholdsrett: 'VARIG',
		},
		soeknadOmBeskyttelseUnderBehandling: 'NEI',
	},
	reason: null,
	status: null,
}

export const skjermingMock = {
	endretDato: '2022-10-03 11:59:24',
	etternavn: 'Test',
	fornavn: 'Cafe',
	opprettetDato: '2022-10-03 11:59:24',
	personident: '12345678912',
	skjermetFra: '2022-01-01 11:48:14',
	skjermetTil: null,
}

export const brregstubMock = {
	fnr: '12345678912',
	fodselsdato: '1992-01-11',
	navn: {
		navn1: 'Cafe',
		navn2: null,
		navn3: 'Test',
	},
	adresse: {
		adresse1: 'Testeveien 2077',
		adresse2: null,
		adresse3: null,
		postnr: '3697',
		poststed: 'UKJENT',
		landKode: 'NO',
		kommunenr: '3819',
	},
	enheter: [
		{
			registreringsdato: '2022-10-03',
			rollebeskrivelse: 'Bostyrer',
			orgNr: 905203975,
			foretaksNavn: {
				navn1: 'Mitt helt eget selskap',
				navn2: null,
				navn3: null,
			},
			forretningsAdresse: {
				adresse1: 'Testeveien 51',
				adresse2: null,
				adresse3: null,
				postnr: '4372',
				poststed: 'EGERSUND',
				landKode: 'NO',
				kommunenr: '1101',
			},
			postAdresse: null,
		},
	],
	hovedstatus: 0,
	understatuser: [0],
}

export const pensjonMock = [
	{
		inntektAar: 2012,
		belop: 10457,
	},
	{
		inntektAar: 2013,
		belop: 10850,
	},
	{
		inntektAar: 2014,
		belop: 11253,
	},
	{
		inntektAar: 2015,
		belop: 11533,
	},
	{
		inntektAar: 2016,
		belop: 11821,
	},
	{
		inntektAar: 2017,
		belop: 12020,
	},
	{
		inntektAar: 2018,
		belop: 12345,
	},
]

export const pensjonTpMock = [{ ordning: '4095' }, { ordning: '3010' }]

export const kontoregisterMock = {
	kontohaver: '12345678912',
	kontonummer: '99999999999',
	gyldigFom: '2022-01-01T11:58:24.030845',
	opprettetAv: 'Dolly',
	kilde: 'testnav-kontoregister-person-proxy-trygdeetaten',
}

export const joarkJournalpostMock = {
	journalpostId: 999999999,
	tittel: 'Syntetisk Inntektsmelding',
	tema: 'Sykepenger',
	avsenderMottaker: {
		type: 'ORGNR',
		id: '947064649',
		navn: 'Dolly Dollesen',
	},
	dokumenter: [
		{
			dokumentInfoId: 888888888,
			tittel: 'Syntetisk Inntektsmelding',
		},
	],
}

export const joarkDokumentMock =
	'<?xml version="1.0" encoding="UTF-8" standalone="yes"?>\n' +
	'<melding xmlns="http://seres.no/xsd/NAV/Inntektsmelding_M/20181211">\n' +
	'    <Skjemainnhold>\n' +
	'        <ytelse>Sykepenger</ytelse>\n' +
	'        <aarsakTilInnsending>Ny</aarsakTilInnsending>\n' +
	'        <arbeidsgiver>\n' +
	'            <virksomhetsnummer>947064649</virksomhetsnummer>\n' +
	'            <kontaktinformasjon>\n' +
	'                <kontaktinformasjonNavn>Dolly Dollesen</kontaktinformasjonNavn>\n' +
	'                <telefonnummer>99999999</telefonnummer>\n' +
	'            </kontaktinformasjon>\n' +
	'        </arbeidsgiver>\n' +
	'        <arbeidstakerFnr>11419217524</arbeidstakerFnr>\n' +
	'        <naerRelasjon>false</naerRelasjon>\n' +
	'        <arbeidsforhold>\n' +
	'            <foersteFravaersdag xsi:nil="true" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"/>\n' +
	'            <beregnetInntekt>\n' +
	'                <beloep>12345.0</beloep>\n' +
	'                <aarsakVedEndring xsi:nil="true" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"/>\n' +
	'            </beregnetInntekt>\n' +
	'            <avtaltFerieListe/>\n' +
	'            <utsettelseAvForeldrepengerListe/>\n' +
	'            <graderingIForeldrepengerListe/>\n' +
	'        </arbeidsforhold>\n' +
	'        <refusjon>\n' +
	'            <refusjonsbeloepPrMnd xsi:nil="true" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"/>\n' +
	'            <refusjonsopphoersdato xsi:nil="true" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"/>\n' +
	'            <endringIRefusjonListe/>\n' +
	'        </refusjon>\n' +
	'        <sykepengerIArbeidsgiverperioden>\n' +
	'            <bruttoUtbetalt xsi:nil="true" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"/>\n' +
	'            <begrunnelseForReduksjonEllerIkkeUtbetalt xsi:nil="true" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"/>\n' +
	'        </sykepengerIArbeidsgiverperioden>\n' +
	'        <startdatoForeldrepengeperiode xsi:nil="true" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"/>\n' +
	'        <opphoerAvNaturalytelseListe/>\n' +
	'        <gjenopptakelseNaturalytelseListe/>\n' +
	'        <avsendersystem>\n' +
	'            <systemnavn>Dolly</systemnavn>\n' +
	'            <systemversjon>2.0</systemversjon>\n' +
	'            <innsendingstidspunkt>2022-10-03T11:48:20</innsendingstidspunkt>\n' +
	'        </avsendersystem>\n' +
	'        <pleiepengerPerioder/>\n' +
	'    </Skjemainnhold>\n' +
	'</melding>\n'

export const varslingerVelkommenResponseMock = [
	{ varslingId: 'VELKOMMEN_TIL_DOLLY', fom: null, tom: null },
]

export const malerMock = { malbestillinger: ['Cypress, Testytest', []] }

export const backendBestillingerMock = [
	{
		id: 1,
		antallIdenter: 1,
		antallLevert: 1,
		ferdig: true,
		sistOppdatert: '2022-01-01T11:59:25.998969',
		bruker: gjeldendeBrukerMock,
		gruppeId: 1,
		stoppet: false,
		environments: ['q1'],
		status: [
			{
				id: 'TPS_MESSAGING',
				navn: 'Meldinger til TPS',
				statuser: [
					{
						melding: 'OK',
						detaljert: [
							{
								miljo: 'q1',
								identer: ['12345678912'],
							},
						],
					},
				],
			},
			{
				id: 'KRRSTUB',
				navn: 'Digital kontaktinformasjon (DKIF)',
				statuser: [
					{
						melding: 'OK',
						identer: ['12345678912'],
					},
				],
			},
			{
				id: 'SIGRUNSTUB',
				navn: 'Skatteinntekt grunnlag (SIGRUN)',
				statuser: [
					{
						melding: 'OK',
						identer: ['12345678912'],
					},
				],
			},
			{
				id: 'AAREG',
				navn: 'Arbeidsregister (AAREG)',
				statuser: [
					{
						melding: 'OK',
						detaljert: [
							{
								miljo: 'q1',
								identer: ['12345678912'],
							},
						],
					},
				],
			},
			{
				id: 'ARENA',
				navn: 'Arena fagsystem',
				statuser: [
					{
						melding: 'OK',
						detaljert: [
							{
								miljo: 'q1',
								identer: ['12345678912'],
							},
						],
					},
					{
						melding: 'OK',
						detaljert: [
							{
								miljo: 'q1',
								identer: ['12345678912'],
							},
						],
					},
				],
			},
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
			{
				id: 'INST2',
				navn: 'Institusjonsopphold (INST2)',
				statuser: [
					{
						melding: 'OK',
						detaljert: [
							{
								miljo: 'q1',
								identer: ['12345678912'],
							},
						],
					},
				],
			},
			{
				id: 'UDISTUB',
				navn: 'Utlendingsdirektoratet (UDI)',
				statuser: [
					{
						melding: 'OK',
						identer: ['12345678912'],
					},
				],
			},
			{
				id: 'INNTK',
				navn: 'Inntektskomponenten (INNTK)',
				statuser: [
					{
						melding: 'Kombinasjonen av feltene i inntekten er ikke gyldig',
						identer: ['12345678912'],
					},
				],
			},
			{
				id: 'PEN_INNTEKT',
				navn: 'Pensjonsopptjening (POPP)',
				statuser: [
					{
						melding: 'OK',
						detaljert: [
							{
								miljo: 'q1',
								identer: ['12345678912'],
							},
						],
					},
				],
			},
			{
				id: 'TP_FORVALTER',
				navn: 'Tjenestepensjon (TP)',
				statuser: [
					{
						melding: 'OK',
						detaljert: [
							{
								miljo: 'q1',
								identer: ['12345678912'],
							},
						],
					},
				],
			},
			{
				id: 'PEN_FORVALTER',
				navn: 'Pensjon (PEN)',
				statuser: [
					{
						melding: 'OK',
						detaljert: [
							{
								miljo: 'q1',
								identer: ['12345678912'],
							},
						],
					},
				],
			},
			{
				id: 'INNTKMELD',
				navn: 'Inntektsmelding (ALTINN/JOARK)',
				statuser: [
					{
						melding: 'OK',
						detaljert: [
							{
								miljo: 'q1',
								identer: ['12345678912'],
							},
						],
					},
				],
			},
			{
				id: 'BRREGSTUB',
				navn: 'Brønnøysundregistrene (BRREGSTUB)',
				statuser: [
					{
						melding: 'OK',
						identer: ['12345678912'],
					},
				],
			},
			{
				id: 'DOKARKIV',
				navn: 'Dokumentarkiv (Joark)',
				statuser: [
					{
						melding: 'OK',
						detaljert: [
							{
								miljo: 'q1',
								identer: ['12345678912'],
							},
						],
					},
				],
			},
			{
				id: 'SYKEMELDING',
				navn: 'Testnorge Sykemelding',
				statuser: [
					{
						melding: 'OK',
						identer: ['12345678912'],
					},
				],
			},
			{
				id: 'SKJERMINGSREGISTER',
				navn: 'Skjermingsregisteret',
				statuser: [
					{
						melding: 'OK',
						identer: ['12345678912'],
					},
				],
			},
			{
				id: 'KONTOREGISTER',
				navn: 'Bankkontoregister',
				statuser: [
					{
						melding: 'OK',
						identer: ['12345678912'],
					},
				],
			},
		],
		bestilling: {
			krrstub: {
				reservert: false,
				mobil: '12345678',
				epost: 'jiodjawsd@jidwa.no',
				registrert: true,
				sdpAdresse: 'sikkerdigipost@superdupersecure.no',
				sdpLeverandoer: 1,
				spraak: 'nb',
			},
			instdata: [
				{
					institusjonstype: 'AS',
					startdato: '2022-09-27T00:00:00',
				},
			],
			aareg: [
				{
					arbeidsforholdstype: 'forenkletOppgjoersordning',
					ansettelsesPeriode: {
						fom: '2002-10-03T00:00:00',
					},
					arbeidsavtale: {
						yrke: '2521106',
					},
					arbeidsgiver: {
						aktoertype: 'ORG',
						orgnummer: '896929119',
					},
				},
			],
			sigrunstub: [
				{
					grunnlag: [
						{
							tekniskNavn: 'formuePrimaerbolig',
							verdi: '12345',
						},
					],
					inntektsaar: '2022',
					svalbardGrunnlag: [],
					tjeneste: 'BEREGNET_SKATT',
				},
			],
			inntektstub: {
				inntektsinformasjon: [
					{
						sisteAarMaaned: '2022-07',
						opplysningspliktig: '963743254',
						virksomhet: '947064649',
						inntektsliste: [
							{
								inntektstype: 'LOENNSINNTEKT',
								beloep: 13245,
								inngaarIGrunnlagForTrekk: false,
								utloeserArbeidsgiveravgift: false,
								fordel: 'naturalytelse',
								beskrivelse: 'losji',
								antall: 2,
							},
						],
					},
				],
			},
			arenaforvalter: {
				arenaBrukertype: 'MED_SERVICEBEHOV',
				kvalifiseringsgruppe: 'IKVAL',
				aap115: [
					{
						fraDato: '2022-09-26T00:00:00',
					},
				],
				aap: [
					{
						fraDato: '2022-09-28T00:00:00',
						tilDato: '2022-10-03T00:00:00',
					},
				],
				dagpenger: [
					{
						rettighetKode: 'DAGO',
						fraDato: '2022-08-09T00:00:00',
						tilDato: '2022-08-18T00:00:00',
					},
				],
			},
			udistub: {
				aliaser: [
					{
						nyIdent: false,
					},
				],
				arbeidsadgang: {
					harArbeidsAdgang: 'JA',
					periode: {},
					typeArbeidsadgang: 'BESTEMT_ARBEIDSGIVER_ELLER_OPPDRAGSGIVER',
					hjemmel: 'kapplah',
				},
				oppholdStatus: {
					eosEllerEFTAVedtakOmVarigOppholdsrett: 'VARIG',
					eosEllerEFTAVedtakOmVarigOppholdsrettPeriode: {},
				},
				flyktning: true,
				soeknadOmBeskyttelseUnderBehandling: 'NEI',
			},
			pensjonforvalter: {
				inntekt: {
					fomAar: 2012,
					tomAar: 2018,
					belop: 12345,
					redusertMedGrunnbelop: true,
				},
				tp: [
					{
						ordning: '3010',
						ytelser: [
							{
								type: 'ALDER',
								datoInnmeldtYtelseFom: '2022-09-03',
								datoYtelseIverksattFom: '2022-09-03',
							},
						],
					},
				],
			},
			inntektsmelding: {
				inntekter: [
					{
						aarsakTilInnsending: 'NY',
						arbeidsforhold: {
							arbeidsforholdId: '',
							beregnetInntekt: {
								beloep: 12345,
							},
						},
						arbeidsgiver: {
							virksomhetsnummer: '947064649',
						},
						avsendersystem: {
							innsendingstidspunkt: '2022-10-03T11:48:20',
						},
						naerRelasjon: false,
						refusjon: {},
						sykepengerIArbeidsgiverperioden: {},
						ytelse: 'SYKEPENGER',
					},
				],
				joarkMetadata: {
					tema: 'SYK',
				},
			},
			brregstub: {
				enheter: [
					{
						foretaksNavn: {
							navn1: 'Mitt helt eget selskap',
						},
						forretningsAdresse: {
							adresse1: 'Sokndalsveien 51',
							kommunenr: '1101',
							landKode: 'NO',
							postnr: '4372',
							poststed: 'EGERSUND',
						},
						orgNr: 905203975,
						registreringsdato: '2022-10-03T11:48:27',
						rolle: 'BOBE',
						personroller: [],
					},
				],
				understatuser: [0],
			},
			dokarkiv: {
				tittel: 'Anke',
				tema: 'AGR',
				kanal: 'NAV_NO',
				avsenderMottaker: {
					id: '12345678912',
					idType: 'FNR',
				},
				dokumenter: [
					{
						tittel: 'Anke',
						brevkode: 'NAV 90-00.08 A',
					},
				],
			},
			histark: {},
			sykemelding: {
				syntSykemelding: {
					orgnummer: '947064649',
					startDato: '2022-10-03T11:48:22',
				},
			},
			pdldata: {
				opprettNyPerson: {
					identtype: 'FNR',
					alder: 30,
				},
				person: {
					navn: [
						{
							kilde: 'Dolly',
							master: 'FREG',
							hasMellomnavn: false,
						},
					],
					forelderBarnRelasjon: [
						{
							kilde: 'Dolly',
							master: 'FREG',
							minRolleForPerson: 'FORELDER',
							relatertPersonsRolle: 'BARN',
							partnerErIkkeForelder: false,
						},
					],
					sivilstand: [
						{
							kilde: 'Dolly',
							master: 'FREG',
							type: 'GIFT',
							borIkkeSammen: false,
							nyRelatertPerson: {
								syntetisk: true,
								nyttNavn: {
									hasMellomnavn: false,
								},
							},
						},
					],
					doedsfall: [
						{
							kilde: 'Dolly',
							master: 'PDL',
							doedsdato: '2022-09-29T11:46:47',
						},
					],
					bostedsadresse: [
						{
							kilde: 'Dolly',
							master: 'FREG',
							gyldigFraOgMed: '2022-09-26T01:00:00',
							vegadresse: {
								postnummer: '7318',
							},
						},
					],
					kontaktadresse: [
						{
							kilde: 'Dolly',
							master: 'FREG',
						},
					],
					kjoenn: [
						{
							kilde: 'Dolly',
							master: 'FREG',
							kjoenn: 'MANN',
						},
					],
					oppholdsadresse: [
						{
							kilde: 'Dolly',
							master: 'FREG',
						},
					],
					innflytting: [
						{
							kilde: 'Dolly',
							master: 'FREG',
							fraflyttingsland: 'AGO',
							fraflyttingsstedIUtlandet: '',
							innflyttingsdato: '2004-10-06T00:00:00',
						},
					],
					utflytting: [
						{
							kilde: 'Dolly',
							master: 'FREG',
							tilflyttingsland: 'BTN',
							tilflyttingsstedIUtlandet: '',
							utflyttingsdato: '2019-10-17T00:00:00',
						},
					],
					foreldreansvar: [
						{
							kilde: 'Dolly',
							master: 'FREG',
							ansvar: 'ANDRE',
						},
					],
					kontaktinformasjonForDoedsbo: [
						{
							kilde: 'Dolly',
							master: 'FREG',
							skifteform: 'OFFENTLIG',
							attestutstedelsesdato: '2022-09-07T00:00:00',
							adresse: {
								adresselinje1: '',
								adresselinje2: '',
								postnummer: '',
								poststedsnavn: '',
								landkode: '',
							},
							organisasjonSomKontakt: {
								kontaktperson: {
									etternavn: 'KATALYSATOR',
									fornavn: 'NONFIGURATIV',
									mellomnavn: 'REFLEKTERENDE',
								},
								organisasjonsnavn: 'Sjokkerende elektriker',
								organisasjonsnummer: '947064649',
							},
						},
					],
					utenlandskIdentifikasjonsnummer: [
						{
							kilde: 'Dolly',
							master: 'FREG',
							identifikasjonsnummer: '12345',
							opphoert: false,
							utstederland: 'AND',
						},
					],
					falskIdentitet: [
						{
							kilde: 'Dolly',
							master: 'FREG',
							erFalsk: true,
						},
					],
					adressebeskyttelse: [
						{
							kilde: 'Dolly',
							master: 'FREG',
							gradering: 'FORTROLIG',
						},
					],
					tilrettelagtKommunikasjon: [
						{
							kilde: 'Dolly',
							master: 'PDL',
							spraakForTaletolk: 'AZ',
							spraakForTegnspraakTolk: 'ES',
						},
					],
					statsborgerskap: [
						{
							kilde: 'Dolly',
							master: 'FREG',
							landkode: 'DZA',
						},
					],
					opphold: [
						{
							type: 'OPPLYSNING_MANGLER',
						},
					],
					telefonnummer: [
						{
							kilde: 'Dolly',
							master: 'PDL',
							landskode: '+376',
							nummer: '12345678',
							prioritet: 1,
						},
					],
					fullmakt: [
						{
							kilde: 'Dolly',
							master: 'PDL',
							nyFullmektig: {
								nyttNavn: {
									hasMellomnavn: false,
								},
							},
							gyldigFraOgMed: '2022-09-26T00:00:00',
							gyldigTilOgMed: '2022-10-28T00:00:00',
							omraader: ['AAR'],
						},
					],
					vergemaal: [
						{
							kilde: 'Dolly',
							master: 'FREG',
							vergemaalEmbete: 'FMIN',
							sakType: 'ANN',
							nyVergeIdent: {
								syntetisk: true,
								nyttNavn: {
									hasMellomnavn: false,
								},
							},
						},
					],
					sikkerhetstiltak: [
						{
							kilde: 'Dolly',
							master: 'PDL',
							tiltakstype: 'TFUS',
							beskrivelse: 'Telefonisk utestengelse',
							kontaktperson: {
								personident: 'Z577742',
								enhet: '0211',
							},
							gyldigFraOgMed: '2022-10-03T00:00:00',
							gyldigTilOgMed: '2022-11-16T00:00:00',
						},
					],
					nyident: [
						{
							kilde: 'Dolly',
							master: 'FREG',
							syntetisk: true,
							nyttNavn: {
								hasMellomnavn: false,
							},
						},
					],
					doedfoedtBarn: [
						{
							kilde: 'Dolly',
							master: 'FREG',
							dato: '2022-01-01T11:46:47',
						},
					],
				},
			},
			tpsMessaging: {
				spraakKode: 'AB',
			},
			bankkonto: {
				norskBankkonto: {
					kontonummer: '99999999999',
					tilfeldigKontonummer: false,
				},
			},
			skjerming: {
				egenAnsattDatoFom: '2022-10-03T11:48:14',
			},
		},
	},
	{
		id: 2,
		antallIdenter: 1,
		antallLevert: 1,
		ferdig: true,
		sistOppdatert: '2022-01-01T15:45:39.696068',
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
						identer: ['01418221999'],
					},
				],
			},
			{
				id: 'INNTK',
				navn: 'Inntektskomponenten (INNTK)',
				statuser: [
					{
						melding: 'Kombinasjonen av feltene i inntekten er ikke gyldig',
						identer: ['01418221999'],
					},
				],
			},
			{
				id: 'PEN_FORVALTER',
				navn: 'Pensjon (PEN)',
				statuser: [
					{
						melding: 'OK',
						detaljert: [
							{
								miljo: 'q1',
								identer: ['01418221999'],
							},
						],
					},
				],
			},
		],
		bestilling: {},
	},
	{
		id: 3,
		antallIdenter: 1,
		antallLevert: 1,
		ferdig: true,
		sistOppdatert: '2022-01-01T15:45:39.696068',
		bruker: gjeldendeBrukerMock,
		gruppeId: 1,
		stoppet: false,
		environments: [''],
		opprettetFraId: 1,
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

export const testidentMock = {
	ident: '12345678912',
	beskrivelse: 'Litta kommentar også',
	bestillingId: [1],
	bestillinger: backendBestillingerMock,
	master: 'PDLF',
	ibruk: false,
}

export const nyGruppeMock = {
	id: 2,
	navn: 'Cypress testing',
	hensikt: 'Saftig testing med cypress..',
	opprettetAv: gjeldendeBrukerMock,
	sistEndretAv: gjeldendeBrukerMock,
	datoEndret: '1990-01-12',
	antallIdenter: 0,
	antallIBruk: 0,
	erEierAvGruppe: false,
	favorittIGruppen: true,
	erLaast: false,
	identer: [],
	tags: [],
}

export const eksisterendeGruppeMock = {
	id: 1,
	navn: 'Testytest',
	hensikt: 'Testing av testytest',
	opprettetAv: gjeldendeBrukerMock,
	sistEndretAv: gjeldendeBrukerMock,
	datoEndret: '1980-01-12',
	antallIdenter: 1,
	antallBestillinger: 3,
	antallIBruk: 0,
	erEierAvGruppe: true,
	favorittIGruppen: false,
	erLaast: false,
	identer: [testidentMock],
	tags: [],
}

export const histarkMock = {
	antallSider: 1,
	enhetsNr: '1234',
	enhetsNavn: 'Testy',
	temaKodeSet: ['ABC'],
	fnrEllerOrgnr: '123456789012',
	startaar: 2021,
	sluttaar: 2023,
	skanningstidspunkt: '2023-04-14T14:11:32',
	filnavn: 'small-test.pdf',
	skanner: 'Skanner',
	skannerSted: 'Teste',
	inneholderKlage: false,
}

export const paginerteGrupperMock = {
	contents: [eksisterendeGruppeMock],
	favoritter: [nyGruppeMock],
}

export const miljoeMock = '["q1","q2","q4","q5","t3"]'
