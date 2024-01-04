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
		fornavn: 'Testytest',
		etternavn: 'Cafe',
	},
]

export const bestillingFragmentSearchMock = [{ id: 1, navn: 'Testytest' }]

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
		},
	},
	tpsMessaging: {},
	skjerming: {
		egenAnsattDatoFom: '2022-09-06T10:24:03',
	},
}
export const brukerMalerMock = {
	malbestillinger: {
		testbruker: [
			{
				id: 1,
				malNavn: 'Teste Cypress',
				bestilling: malBestilling,
				bruker: gjeldendeBrukerMock,
			},
		],
	},
}

export const brukerMalerEndretMock = [
	{
		id: 1,
		malNavn: 'Nytt navn på mal',
		bestilling: malBestilling,
		bruker: gjeldendeBrukerMock,
	},
]

export const uferdigBestillingMock = {
	id: 2,
	antallIdenter: 1,
	antallLevert: 0,
	ferdig: false,
	sistOppdatert: '2023-04-21T10:38:10.11282',
	bruker: gjeldendeBrukerMock,
	gruppeId: 2,
	stoppet: false,
	bestilling: {
		pdldata: {
			opprettNyPerson: {
				identtype: 'FNR',
				syntetisk: true,
			},
		},
	},
}

export const avbruttBestillingMock = {
	...uferdigBestillingMock,
	stoppet: true,
	ferdig: true,
}

export const uferdigeBestillingerMock = [uferdigBestillingMock]

export const brukerOrganisasjonMalerMock = {
	malbestillinger: {
		testbruker: [
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
		],
	},
}

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
				adresselinjer: ['Teste testings 4'],
				postnr: '9999',
				poststed: 'ØVRE TESTE',
				kommunenr: '8888',
				landkode: 'NO',
				vegadresseId: '123456789',
			},
			{
				id: 3,
				adressetype: 'FADR',
				adresselinjer: ['Teste testings 5'],
				postnr: '9999',
				poststed: 'ØVRE TESTE',
				kommunenr: '8888',
				landkode: 'NO',
				vegadresseId: '123456789',
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
						adresselinjer: ['Teste testes vei 71'],
						postnr: '1234',
						poststed: 'OSLO',
						kommunenr: '1234',
						landkode: 'NO',
						vegadresseId: '123456789',
					},
					{
						id: 3,
						adressetype: 'FADR',
						adresselinjer: ['Teste testings 70'],
						postnr: '1234',
						poststed: 'OSLO',
						kommunenr: '1234',
						landkode: 'NO',
						vegadresseId: '123456789',
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
				id: 1,
				adressetype: 'PADR',
				adresselinjer: ['Teste testes vei 71'],
				postnr: '1234',
				poststed: 'OSLO',
				kommunenr: '1234',
				landkode: 'NO',
				vegadresseId: '123456789',
			},
			{
				id: 2,
				adressetype: 'FADR',
				adresselinjer: ['Teste testes vei 72'],
				postnr: '1234',
				poststed: 'OSLO',
				kommunenr: '1234',
				landkode: 'NO',
				vegadresseId: '123456789',
			},
		],
		underenheter: [
			{
				organisasjonsnummer: '123456789',
				enhetstype: 'BEDR',
				organisasjonsnavn: 'HORISONTAL FEIL',
				adresser: [
					{
						id: 1,
						adressetype: 'PADR',
						adresselinjer: ['Teste testes vei 71'],
						postnr: '1234',
						poststed: 'OSLO',
						kommunenr: '1234',
						landkode: 'NO',
						vegadresseId: '123456789',
					},
					{
						id: 2,
						adressetype: 'FADR',
						adresselinjer: ['Teste testes vei 72'],
						postnr: '1234',
						poststed: 'OSLO',
						kommunenr: '1234',
						landkode: 'NO',
						vegadresseId: '123456789',
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
		navArbeidsforholdId: 3095857,
		arbeidsforholdId: '1',
		arbeidstaker: {
			type: 'Person',
			offentligIdent: '12345678912',
			aktoerId: '2853757016775',
		},
		arbeidsgiver: {
			type: 'Organisasjon',
			organisasjonsnummer: '941559069',
		},
		opplysningspliktig: {
			type: 'Organisasjon',
			organisasjonsnummer: '965059946',
		},
		type: 'ordinaertArbeidsforhold',
		ansettelsesperiode: {
			periode: {
				fom: '2003-08-23',
			},
			bruksperiode: {
				fom: '2023-08-23T10:18:21.814',
			},
			sporingsinformasjon: {
				opprettetTidspunkt: '2023-08-23T10:18:21.814',
				opprettetAv: 'testnav',
				opprettetKilde: 'EDAG',
				opprettetKildereferanse: '7a76e365-5108-49bc-b454-6f0818e7fc27',
				endretTidspunkt: '2023-08-23T10:18:21.814',
				endretAv: 'testnav',
				endretKilde: 'EDAG',
				endretKildereferanse: '7a76e365-5108-49bc-b454-6f0818e7fc27',
			},
		},
		arbeidsavtaler: [
			{
				type: 'Ordinaer',
				arbeidstidsordning: 'ikkeSkift',
				yrke: '3229105',
				stillingsprosent: 100,
				antallTimerPrUke: 37.5,
				beregnetAntallTimerPrUke: 37.5,
				bruksperiode: {
					fom: '2023-08-23T10:18:37.032',
				},
				gyldighetsperiode: {
					fom: '2023-01-01',
				},
				sporingsinformasjon: {
					opprettetTidspunkt: '2023-08-23T10:18:37.033',
					opprettetAv: 'testnav',
					opprettetKilde: 'EDAG',
					opprettetKildereferanse: 'de7ff548-a9c6-4ca1-af78-45ddcd0757ec',
					endretTidspunkt: '2023-08-23T10:18:37.033',
					endretAv: 'testnav',
					endretKilde: 'EDAG',
					endretKildereferanse: 'de7ff548-a9c6-4ca1-af78-45ddcd0757ec',
				},
			},
		],
		innrapportertEtterAOrdningen: true,
		registrert: '2023-08-23T10:18:17.563',
		sistBekreftet: '2023-08-23T10:18:17',
		sporingsinformasjon: {
			opprettetTidspunkt: '2023-08-23T10:18:21.814',
			opprettetAv: 'testnav',
			opprettetKilde: 'EDAG',
			opprettetKildereferanse: '7a76e365-5108-49bc-b454-6f0818e7fc27',
			endretTidspunkt: '2023-08-23T10:18:52.268',
			endretAv: 'testnav',
			endretKilde: 'EDAG',
			endretKildereferanse: '666bfffc-6e0c-449e-9d76-1305cc140c92',
		},
	},
]
export const ameldingMock = [
	{
		kalendermaaned: '2023-05-01',
		opplysningspliktigOrganisajonsnummer: '965059946',
		virksomheter: [
			{
				organisajonsnummer: '941559069',
				personer: [
					{
						ident: '12345678912',
						arbeidsforhold: [
							{
								arbeidsforholdId: '1',
								typeArbeidsforhold: 'ordinaertArbeidsforhold',
								startdato: '2003-08-23',
								sluttdato: null,
								antallTimerPerUke: 37.5,
								yrke: '3229105',
								arbeidstidsordning: 'ikkeSkift',
								stillingsprosent: 100,
								sisteLoennsendringsdato: null,
								permisjoner: [],
								fartoey: null,
								inntekter: [],
								avvik: [],
								historikk: null,
							},
						],
					},
				],
			},
		],
		version: 1,
	},
	{
		kalendermaaned: '2023-04-01',
		opplysningspliktigOrganisajonsnummer: '965059946',
		virksomheter: [
			{
				organisajonsnummer: '941559069',
				personer: [
					{
						ident: '12345678912',
						arbeidsforhold: [
							{
								arbeidsforholdId: '1',
								typeArbeidsforhold: 'ordinaertArbeidsforhold',
								startdato: '2003-08-23',
								sluttdato: null,
								antallTimerPerUke: 37.5,
								yrke: '3229105',
								arbeidstidsordning: 'ikkeSkift',
								stillingsprosent: 100,
								sisteLoennsendringsdato: null,
								permisjoner: [],
								fartoey: null,
								inntekter: [],
								avvik: [],
								historikk: null,
							},
						],
					},
				],
			},
		],
		version: 1,
	},
	{
		kalendermaaned: '2023-06-01',
		opplysningspliktigOrganisajonsnummer: '965059946',
		virksomheter: [
			{
				organisajonsnummer: '941559069',
				personer: [
					{
						ident: '12345678912',
						arbeidsforhold: [
							{
								arbeidsforholdId: '1',
								typeArbeidsforhold: 'ordinaertArbeidsforhold',
								startdato: '2003-08-23',
								sluttdato: null,
								antallTimerPerUke: 37.5,
								yrke: '3229105',
								arbeidstidsordning: 'ikkeSkift',
								stillingsprosent: 100,
								sisteLoennsendringsdato: null,
								permisjoner: [],
								fartoey: null,
								inntekter: [],
								avvik: [],
								historikk: null,
							},
						],
					},
				],
			},
		],
		version: 1,
	},
]

export const arenaMock = {
	fodselsnr: '12345678912',
	registrertDato: '2023-08-06',
	sistInaktivDato: null,
	maalform: 'Bokmål',
	statsborgerLand: 'Norge',
	bosattStatus: 'Bosatt',
	lokalkontor: {
		enhetNr: '4154',
		enhetNavn: 'Nasjonal oppfølgingsenhet',
	},
	hovedmaal: 'Skaffe arbeid',
	formidlingsgruppe: {
		kode: 'ARBS',
		navn: 'Arbeidssøker',
	},
	servicegruppe: {
		kode: 'IKVAL',
		navn: 'Standardinnsats',
	},
	rettighetsgruppe: {
		kode: 'IYT',
		navn: 'Ingen livsoppholdsytelse i Arena',
	},
	meldeplikt: true,
	meldeform: 'Elektronisk',
	meldegruppe: 'Ingen ytelser',
	vedtakListe: [
		{
			sak: {
				kode: 'AA',
				navn: 'Arbeidsavklaringspenger',
				status: 'Aktiv',
				sakNr: '20230264670',
			},
			vedtakNr: 2,
			rettighet: {
				kode: 'AAP',
				navn: 'Arbeidsavklaringspenger',
			},
			aktivitetfase: {
				kode: 'UA',
				navn: 'Under arbeidsavklaring',
			},
			type: {
				kode: 'O',
				navn: 'Ny rettighet',
			},
			status: {
				kode: 'MOTAT',
				navn: 'Mottatt',
			},
			utfall: null,
			fraDato: null,
			tilDato: null,
		},
		{
			sak: {
				kode: 'AA',
				navn: 'Arbeidsavklaringspenger',
				status: 'Aktiv',
				sakNr: '20230264670',
			},
			vedtakNr: 1,
			rettighet: {
				kode: 'AA115',
				navn: '§11-5 nedsatt arbeidsevne',
			},
			aktivitetfase: {
				kode: 'IKKE',
				navn: 'Ikke spesif. aktivitetsfase',
			},
			type: {
				kode: 'O',
				navn: 'Ny rettighet',
			},
			status: {
				kode: 'IVERK',
				navn: 'Iverksatt',
			},
			utfall: 'Ja',
			fraDato: '2023-08-06',
			tilDato: null,
		},
		{
			sak: {
				kode: 'ARBEID',
				navn: 'Oppfølgingssak',
				status: 'Aktiv',
				sakNr: '20230264669',
			},
			vedtakNr: 1,
			rettighet: {
				kode: 'BIST14A',
				navn: 'Bistandsbehov §14a',
			},
			aktivitetfase: {
				kode: 'IKVAL',
				navn: 'Standardinnsats',
			},
			type: {
				kode: 'O',
				navn: 'Ny rettighet',
			},
			status: {
				kode: 'IVERK',
				navn: 'Iverksatt',
			},
			utfall: 'Ja',
			fraDato: '2023-08-06',
			tilDato: null,
		},
	],
}

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
				kommunenr: '9876',
				flyttedato: '1992-01-11T00:00:00',
				postnr: '3697',
				adresse: 'TESTEVEIEN',
				husnummer: '2077',
				gatekode: '01234',
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
			gtVerdi: '0987',
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
			},
		},
		status: 'OK',
	},
	{
		miljoe: 'q2',
		person: {
			ident: '12345678912',
			identtype: 'FNR',
			kjonn: 'K',
			fornavn: 'Cafe',
			etternavn: 'Test',
			forkortetNavn: 'Test Cafe',
			statsborgerskap: {
				statsborgerskap: 'NOR',
				statsborgerskapRegdato: '1956-07-07T00:00:00',
			},
			spesregDato: '2023-05-12T09:15:53.579093117',
			sivilstand: {
				sivilstand: 'ENKE',
				sivilstandRegdato: '2023-01-10T00:00:00',
			},
			midlertidigAdresse: {
				gyldigTom: '2024-05-11T00:00:00',
				adressetype: 'UTAD',
				postLinje1: '1KOLEJOWA 6/5',
				postLinje2: 'CAPITAL WEST 3000',
				postLinje3: '18-500 KOLNO',
				postLand: 'TYSKLAND',
			},
			gtType: 'LAND',
			gtVerdi: 'DEU',
			gtRegel: 'C',
			personStatus: 'UTVA',
			importFra: 'TPS',
			bankkontonrNorsk: {
				kontonummer: '1234.06.12345',
				kontoRegdato: '2023-05-11T00:00:00',
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
		registrertAv: 'Dolly',
	},
]

export const udistubMock = {
	person: {
		aliaser: [],
		arbeidsadgang: {
			harArbeidsAdgang: 'JA',
			typeArbeidsadgang: 'BESTEMT_ARBEIDSGIVER_ELLER_OPPDRAGSGIVER',
			hjemmel: 'testetest',
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
}

export const skjermingMock = {
	endretDato: '2022-10-03 11:59:24',
	etternavn: 'Test',
	fornavn: 'Cafe',
	opprettetDato: '2022-10-03 11:59:24',
	personident: '12345678912',
	skjermetFra: '2022-01-01 11:48:14',
}

export const medlMock = [
	{
		unntakId: 123456789,
		ident: '12345678912',
		fraOgMed: '2023-03-28',
		tilOgMed: '2023-06-07',
		status: 'GYLD',
		dekning: 'DEKNING',
		helsedel: true,
		medlem: true,
		lovvalgsland: 'NOR',
		lovvalg: 'LOVVALG',
		grunnlag: 'GRUNNLAG',
		sporingsinformasjon: {
			versjon: 0,
			registrert: '2023-01-01',
			besluttet: '2023-01-01',
			kilde: 'srvmelosys',
			kildedokument: 'Dokument',
			opprettet: '2023-01-01T10:10:10.111111',
			opprettetAv: 'srvmelosys',
			sistEndret: '2023-01-01T10:10:10.111111',
			sistEndretAv: 'srvmelosys',
		},
	},
]

export const brregstubMock = {
	fnr: '12345678912',
	fodselsdato: '1992-01-11',
	navn: {
		navn1: 'Cafe',
		navn3: 'Test',
	},
	adresse: {
		adresse1: 'Testeveien 2077',
		postnr: '1234',
		poststed: 'UKJENT',
		landKode: 'NO',
		kommunenr: '9876',
	},
	enheter: [
		{
			registreringsdato: '2022-10-03',
			rollebeskrivelse: 'Bostyrer',
			orgNr: 905203975,
			foretaksNavn: {
				navn1: 'Mitt helt eget selskap',
			},
			forretningsAdresse: {
				adresse1: 'Testeveien 51',
				postnr: '1234',
				poststed: 'TESTINGS',
				landKode: 'NO',
				kommunenr: '1234',
			},
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

export const tagsMock = [{ tag: 'DUMMY', beskrivelse: 'Dummy' }]

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
	'<melding xmlns="http://testings.no">\n' +
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

export const varslingerVelkommenResponseMock = [{ varslingId: 'VELKOMMEN_TIL_DOLLY' }]

export const malerMock = { malbestillinger: ['Cypress, Testytest', []] }

export const oppsummeringsdokumentServiceMock = [
	{
		kalendermaaned: '2023-01-01',
		opplysningspliktigOrganisajonsnummer: '806222747',
		virksomheter: [
			{
				organisajonsnummer: '961475457',
				personer: [
					{
						ident: '12345678912',
						arbeidsforhold: [
							{
								arbeidsforholdId: '1',
								typeArbeidsforhold: 'forenkletOppgjoersordning',
								startdato: '2003-04-26',
								antallTimerPerUke: 37.5,
								yrke: '0030320',
								permisjoner: [],
								inntekter: [],
								avvik: [],
							},
						],
					},
				],
			},
		],
		version: 7,
	},
	{
		kalendermaaned: '2023-02-01',
		opplysningspliktigOrganisajonsnummer: '806222747',
		virksomheter: [
			{
				organisajonsnummer: '961475457',
				personer: [
					{
						ident: '12345678912',
						arbeidsforhold: [
							{
								arbeidsforholdId: '1',
								typeArbeidsforhold: 'forenkletOppgjoersordning',
								startdato: '2003-04-26',
								antallTimerPerUke: 37.5,
								yrke: '0030320',
								permisjoner: [],
								inntekter: [],
								avvik: [],
							},
						],
					},
				],
			},
		],
		version: 7,
	},
	{
		kalendermaaned: '2023-03-01',
		opplysningspliktigOrganisajonsnummer: '806222747',
		virksomheter: [
			{
				organisajonsnummer: '961475457',
				personer: [
					{
						ident: '12345678912',
						arbeidsforhold: [
							{
								arbeidsforholdId: '1',
								typeArbeidsforhold: 'forenkletOppgjoersordning',
								startdato: '2003-04-26',
								antallTimerPerUke: 37.5,
								yrke: '0030320',
								permisjoner: [],
								inntekter: [],
								avvik: [],
							},
						],
					},
				],
			},
		],
		version: 5,
	},
]

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
					amelding: [{ temp: '' }],
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
							virksomhetsnummer: '123456789',
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
							adresse1: 'Testeveien 123',
							kommunenr: '1234',
							landKode: 'NO',
							postnr: '4321',
							poststed: 'TESTER',
						},
						orgNr: 987654321,
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
			medl: {},
			sykemelding: {
				syntSykemelding: {
					orgnummer: '987654321',
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
								postnummer: '1234',
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
								organisasjonsnummer: '123456789',
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
export const personFragmentNavigerMock = {
	gruppe: eksisterendeGruppeMock,
	identHovedperson: '12345678912',
	identNavigerTil: '12345678912',
	sidetall: 0,
}

export const bestillingFragmentNavigerMock = {
	gruppe: eksisterendeGruppeMock,
	identHovedperson: null,
	identNavigerTil: null,
	bestillingNavigerTil: 1,
	sidetall: 0,
}
