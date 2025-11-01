export const gjeldendeProfilMock = {
	visningsNavn: 'BeASt, BugTerminator',
	epost: 'BeASt@bugexterminator.no',
	avdeling: '1234 Testytest',
	organisasjon: 'PLAYWRIGHT',
	type: 'Testbruker',
}

export const gjeldendeAzureBrukerMock = {
	brukerId: '1234-5678-12',
	brukernavn: 'BeASt, BugTerminator',
	brukertype: 'AZURE',
	epost: 'BeASt@bugexterminator.no',
}

export const gjeldendeBankidBrukerMock = {
	brukerId: '12345678912',
	brukernavn: 'BeASt, BugTerminator',
	brukertype: 'BANKID',
}

export const brukerTeamsMock = [
	{
		beskrivelse: 'Testytest beskrivelse',
		brukerId: 'team-bruker-id-90',
		brukere: [gjeldendeAzureBrukerMock],
		id: 1,
		navn: 'Testytest',
		opprettet: '2022-09-06T10:24:03',
		opprettetAv: gjeldendeAzureBrukerMock,
	},
]

export const personOrgTilgangMock = [
	{
		navn: 'testytest',
		organisasjonsnummer: '12345678',
		organisasjonsform: 'BEDR',
	},
]

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
export const brukerMalerMock = [
	{
		id: 1,
		malNavn: 'Teste Playwright',
		malBestilling: malBestilling,
		bruker: gjeldendeAzureBrukerMock,
	},
]

export const uferdigBestillingMock = {
	id: 2,
	antallIdenter: 1,
	antallLevert: 0,
	ferdig: false,
	sistOppdatert: '2023-04-21T10:38:10.11282',
	bruker: gjeldendeAzureBrukerMock,
	gruppeId: 2,
	stoppet: false,
	bestilling: {
		pdldata: {
			opprettNyPerson: {
				identtype: 'FNR',
				id2032: false,
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
		transaksjonId: [
			{
				journalpostId: '999999999',
				dokumentInfoId: '888888888',
			},
		],
		datoEndret: '2022-01-01T11:58:58.227916',
	},
	{
		id: 2,
		bestillingId: 1,
		ident: '12345678912',
		system: 'INNTKMELD',
		miljoe: 'q2',
		transaksjonId: [
			{
				journalpostId: '999999999',
				dokumentInfoId: '888888888',
			},
		],
		datoEndret: '2022-01-01T11:58:59.289252',
	},
]

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

export const pensjonPensjonsavtaleMock = [{}, {}]

export const afpOffentligMock = {
	direktekall: [],
	mocksvar: [
		{
			tpId: '4099',
			statusAfp: 'INNVILGET',
			virkningsDato: '2024-09-01T00:00:00',
			sistBenyttetG: 2024,
			belopsListe: [
				{
					fomDato: '2024-09-02T00:00:00',
					belop: '10000',
				},
			],
		},
	],
}

export const tagsMock = [{ tag: 'DUMMY', beskrivelse: 'Dummy' }]

export const kontoregisterMock = {
	kontohaver: '12345678912',
	kontonummer: '99999999999',
	gyldigFom: '2022-01-01T11:58:24.030845',
	opprettetAv: 'Dolly',
	kilde: 'testnav-dolly-proxy-trygdeetaten',
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

export const testnorgeMalBestillinger = [
	{
		id: 4,
		antallIdenter: 10,
		antallLevert: 10,
		ferdig: true,
		sistOppdatert: '2024-05-08T12:55:37.565933',
		bruker: {
			brukerId: '1231231231231232131231312312312312',
			brukernavn: 'TestTestesen',
			brukertype: 'BANKID',
		},
		gruppeId: 1,
		stoppet: false,
		environments: ['q1', 'q2'],
		status: [
			{
				id: 'PDLIMPORT',
				navn: 'Import av personer (TESTNORGE)',
				statuser: [
					{
						melding: 'OK',
						identer: ['01816311111', '01816311111'],
					},
				],
			},
			{
				id: 'PDL_PERSONSTATUS',
				navn: 'Person finnes i PDL',
				statuser: [
					{
						melding: 'OK',
						identer: ['01816366666', '01816355555'],
					},
				],
			},
			{
				id: 'PEN_FORVALTER',
				navn: 'Pensjon persondata (PEN)',
				statuser: [
					{
						melding: 'OK',
						detaljert: [
							{
								miljo: 'q1',
								identer: ['01816355555', '01816355555'],
							},
							{
								miljo: 'q2',
								identer: ['01816311111', '01816311111'],
							},
						],
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
								identer: ['01816322222', '01816322222'],
							},
							{
								miljo: 'q2',
								identer: ['01816322222', '01816322222'],
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
								identer: ['01816333333', '01816333333'],
							},
							{
								miljo: 'q2',
								identer: ['01816344444', '01816344444'],
							},
						],
					},
				],
			},
		],
		bestilling: {
			pensjonforvalter: {
				inntekt: {
					fomAar: 2014,
					tomAar: 2023,
					belop: 560000,
					redusertMedGrunnbelop: true,
				},
				tp: [
					{
						ordning: '3010',
						ytelser: [
							{
								type: 'ALDER',
								datoInnmeldtYtelseFom: '2024-04-08',
								datoYtelseIverksattFom: '2024-04-08',
							},
						],
					},
				],
			},
		},
	},
	{
		id: 5,
		antallIdenter: 5,
		antallLevert: 5,
		ferdig: true,
		sistOppdatert: '2024-06-07T17:02:30.757521',
		bruker: {
			brukerId: '12312312312312312321312312312',
			brukernavn: 'TestTestesen',
			brukertype: 'BANKID',
		},
		gruppeId: 1,
		stoppet: false,
		environments: ['q1'],
		status: [
			{
				id: 'PDLIMPORT',
				navn: 'Import av personer (TESTNORGE)',
				statuser: [
					{
						melding: 'OK',
						identer: ['31816512345', '06896512345', '08826512345', '06836312345', '04916412345'],
					},
				],
			},
			{
				id: 'PDL_PERSONSTATUS',
				navn: 'Person finnes i PDL',
				statuser: [
					{
						melding: 'OK',
						identer: ['31816512345', '06896512345', '08826512345', '06836312345', '04916412345'],
					},
				],
			},
			{
				id: 'PEN_FORVALTER',
				navn: 'Pensjon persondata (PEN)',
				statuser: [
					{
						melding: 'OK',
						detaljert: [
							{
								miljo: 'q1',
								identer: ['04916123452', '31816123457'],
							},
							{
								miljo: 'q2',
								identer: ['04916412345', '31816512345'],
							},
						],
					},
				],
			},
			{
				id: 'SIGRUN_PENSJONSGIVENDE',
				navn: 'Pensjonsgivende inntekt (Sigrunstub)',
				statuser: [
					{
						melding: 'OK',
						identer: ['31816512345', '06896512345', '08826512345', '06836312345', '04916412345'],
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
								identer: ['04916412345', '31816512345', '08826512345'],
							},
							{
								miljo: 'q2',
								identer: ['04916412345', '31816512345'],
							},
						],
					},
				],
			},
		],
		bestilling: {
			aareg: [
				{
					arbeidsforholdstype: 'ordinaertArbeidsforhold',
					ansettelsesPeriode: {
						fom: '2004-05-07T12:06:12',
					},
					antallTimerForTimeloennet: [],
					arbeidsavtale: {
						arbeidstidsordning: 'ikkeSkift',
						avtaltArbeidstimerPerUke: 37.5,
						stillingsprosent: 100,
						yrke: '3231109',
						ansettelsesform: 'fast',
					},
					permittering: [],
					permisjon: [],
					fartoy: [],
					utenlandsopphold: [],
					arbeidsgiver: {
						aktoertype: 'ORG',
						orgnummer: '972671234',
					},
				},
			],
			sigrunstub: [
				{
					grunnlag: [
						{
							tekniskNavn: 'alminneligInntektFoerSaerfradrag',
							verdi: '550000',
						},
					],
					inntektsaar: '2024',
					svalbardGrunnlag: [],
					tjeneste: 'BEREGNET_SKATT',
				},
			],
			sigrunstubPensjonsgivende: [
				{
					inntektsaar: '2024',
					pensjonsgivendeInntekt: [
						{
							skatteordning: 'FASTLAND',
							datoForFastsetting: '2024-05-07T12:06:30.659Z',
						},
					],
					testdataEier: '',
				},
			],
		},
	},
]

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
		bruker: gjeldendeAzureBrukerMock,
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
				id: 'PEN_AFP_OFFENTLIG',
				navn: 'AFP offentlig (PEN)',
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
				landkode: '+47',
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
				afpOffentlig: {
					direktekall: [],
					mocksvar: [
						{
							tpId: '4099',
							statusAfp: 'INNVILGET',
							virkningsDato: '2024-09-01T00:00:00',
							sistBenyttetG: 2024,
							belopsListe: [
								{
									fomDato: '2024-09-02T00:00:00',
									belop: '10000',
								},
							],
						},
					],
				},
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
			dokarkiv: [
				{
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
			],
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
					id2032: false,
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
		bruker: gjeldendeAzureBrukerMock,
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
		bruker: gjeldendeAzureBrukerMock,
		gruppeId: 1,
		stoppet: false,
		environments: [''],
		opprettetFraId: 1,
		bestilling: {
			pdldata: {
				opprettNyPerson: {
					identtype: 'FNR',
					id2032: false,
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
	navn: 'Playwright testing',
	hensikt: 'Saftig testing med Playwright..',
	opprettetAv: gjeldendeAzureBrukerMock,
	sistEndretAv: gjeldendeAzureBrukerMock,
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
	opprettetAv: gjeldendeAzureBrukerMock,
	sistEndretAv: gjeldendeAzureBrukerMock,
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

export const laastGruppeMock = {
	...eksisterendeGruppeMock,
	erLaast: true,
	laastBeskrivelse: 'Låst gruppe',
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

export const miljoeMock = ['q1', 'q2', 'q4']
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

export const tenorSoekOversiktMock = {
	status: 'OK',
	data: {
		treff: 2,
		rader: 2,
		offset: 0,
		nesteSide: null,
		seed: 1111,
		personer: [
			{
				id: '12345678912',
				fornavn: 'TESTYTEST',
				etternavn: 'CAFE',
				tenorRelasjoner: ['Freg'],
				ibruk: true,
			},
			{
				id: '98765432198',
				fornavn: 'TIGER',
				etternavn: 'ULV',
				tenorRelasjoner: ['Freg'],
				ibruk: false,
			},
		],
	},
	query: 'legitimasjonsdokument:*',
	error: null,
}

export const tenorSoekOrganisasjonOversiktMock = {
	status: 'OK',
	data: {
		treff: 1234,
		rader: 3,
		offset: 0,
		nesteSide: 1,
		seed: 1234,
		organisasjoner: [
			{
				navn: 'OPTIMISTISK TYPISK KATT HØRSEL',
				organisasjonsnummer: '312342234',
				kilder: ['brregErFr'],
			},
			{
				navn: 'OVERFLØDIG FUNKSJONELL TIGER AS',
				organisasjonsnummer: '312345123',
				kilder: ['brregErFr', 'arbeidsforhold'],
			},
			{
				navn: 'UFORGJENGELIG PARODISK TIGER AS',
				organisasjonsnummer: '312345612',
				kilder: ['brregErFr'],
			},
		],
	},
	query: '',
}

export const tenorSoekTestdataMock = {
	status: 'OK',
	data: {
		treff: 2,
		rader: 2,
		offset: 0,
		nesteSide: null,
		seed: 1111,
		dokumentListe: [
			{
				foedselsdato: '1992-06-04',
				identifikator: ['12345678912'],
				kjoenn: 'kvinne',
				personstatus: 'midlertidig',
				sivilstand: 'gift',
				tenorMetadata: {
					kildedata: '{}',
				},
				visningnavn: 'TESTYTEST CAFE',
			},
			{
				foedselsdato: '1974-02-02',
				identifikator: ['98765432198'],
				kjoenn: 'kvinne',
				personstatus: 'bosatt',
				sivilstand: 'ugift',
				tenorMetadata: {
					kildedata: '{}',
				},
				visningnavn: 'TIGER ULV',
			},
		],
		fasetter: {},
	},
	query: '',
	error: null,
}
export const tenorSoekOrganisasjonTestdataMock = {
	status: 'OK',
	data: {
		treff: 1,
		rader: 1,
		offset: 0,
		nesteSide: null,
		seed: 0,
		organisasjoner: [
			{
				navn: 'OVERFLØDIG FUNKSJONELL TIGER AS',
				organisasjonsnummer: '312345123',
				organisasjonsform: {
					kode: 'ENK',
					beskrivelse: 'Enkeltpersonforetak',
				},
				forretningsadresse: {
					land: 'Norge',
					landkode: 'NO',
					postnummer: '2674',
					poststed: 'MYSUSÆTER',
					adresse: ['Tjønnbakkvegen 29'],
					kommune: 'SEL',
					kommunenummer: '3437',
				},
				kilder: ['brregErFr'],
				naeringskoder: [
					{
						kode: '86',
						beskrivelse: 'Helsetjenester',
						hjelpeenhetskode: false,
						rekkefolge: 1,
						nivaa: 1,
					},
					{
						kode: '86.2',
						beskrivelse: 'Lege og tannlegetjenester',
						hjelpeenhetskode: false,
						rekkefolge: 1,
						nivaa: 2,
					},
					{
						kode: '86.21',
						beskrivelse: 'Legetjeneste',
						hjelpeenhetskode: false,
						rekkefolge: 1,
						nivaa: 3,
					},
					{
						kode: '86.211',
						beskrivelse: 'Allmenn legetjeneste',
						hjelpeenhetskode: false,
						rekkefolge: 1,
						nivaa: 4,
					},
				],
				registreringsdatoEnhetsregisteret: '20121212',
				slettetIEnhetsregisteret: 'N',
				registrertIForetaksregisteret: 'N',
				slettetIForetaksregisteret: 'N',
				registreringspliktigForetaksregisteret: 'N',
				registrertIFrivillighetsregisteret: 'N',
				registrertIStiftelsesregisteret: 'N',
				registrertIMvaregisteret: 'N',
				konkurs: 'N',
				underAvvikling: 'N',
				underTvangsavviklingEllerTvangsopplosning: 'N',
				maalform: 'Bokmål',
				ansvarsbegrensning: 'N',
				harAnsatte: 'N',
				antallAnsatte: 0,
				kapital: {
					antallAksjer: '0',
					fritekst: [],
					sakkyndigRedegjorelse: 'N',
				},
				kjonnsrepresentasjon: 'N',
				fravalgAvRevisjon: {
					fravalg: 'N',
				},
			},
		],
	},
	query: 'organisasjonsnummer:312345678',
	error: null,
}

export const dollySearchMock = {
	antall: 1,
	error: null,
	personer: [
		{
			hentIdenter: {
				identer: [
					{
						ident: '12345678912',
						historisk: false,
						gruppe: 'FOLKEREGISTERIDENT',
					},
				],
			},
			hentPerson: {
				foedsel: [
					{
						foedselsaar: 1992,
						foedselsdato: '1992-01-11',
						foedeland: 'NOR',
						folkeregistermetadata: {
							ajourholdstidspunkt: '2022-10-03T11:57:40',
							gyldighetstidspunkt: '2022-10-03T11:57:40',
							kilde: 'Dolly',
						},
						metadata: {
							endringer: [
								{
									kilde: 'Dolly',
									registrert: '2022-10-03T11:57:40',
									registrertAv: 'Folkeregisteret',
									systemkilde: 'FREG',
									type: 'OPPRETT',
								},
							],
							historisk: false,
							master: 'FREG',
							opplysningsId: 'c8eb5066-14ec-4bf7-acb5-365cae1deaf3',
						},
					},
				],
				kjoenn: [
					{
						kjoenn: 'MANN',
						folkeregistermetadata: {
							ajourholdstidspunkt: '2022-10-03T11:57:40',
							gyldighetstidspunkt: '2022-10-03T11:57:40',
							kilde: 'Dolly',
						},
						metadata: {
							endringer: [
								{
									kilde: 'Dolly',
									registrert: '2022-10-03T11:57:40',
									registrertAv: 'Folkeregisteret',
									systemkilde: 'FREG',
									type: 'OPPRETT',
								},
							],
							historisk: false,
							master: 'FREG',
							opplysningsId: '017a6239-91c2-463b-b9d1-1e732c3db5e5',
						},
					},
				],
				navn: [
					{
						fornavn: 'Cafe',
						etternavn: 'Test',
						gyldigFraOgMed: '2022-10-03',
						folkeregistermetadata: {
							ajourholdstidspunkt: '2022-10-03T11:57:40',
							gyldighetstidspunkt: '2022-10-03T11:57:40',
							kilde: 'Dolly',
						},
						metadata: {
							endringer: [
								{
									kilde: 'Dolly',
									registrert: '2022-10-03T11:57:40',
									registrertAv: 'Folkeregisteret',
									systemkilde: 'FREG',
									type: 'OPPRETT',
								},
							],
							historisk: false,
							master: 'FREG',
							opplysningsId: '764dc813-3c85-42c3-abb6-472f6f30d953',
						},
					},
				],
				vergemaalEllerFremtidsfullmakt: [
					{
						type: 'forvaltningUtenforVergemaal',
						embete: 'Statsforvalteren i Innlandet',
						vergeEllerFullmektig: {
							navn: {
								fornavn: 'Testesen',
								etternavn: 'Cafe',
							},
							motpartsPersonident: '23456789123',
							omfangetErInnenPersonligOmraade: true,
						},
						folkeregistermetadata: {
							ajourholdstidspunkt: '2022-10-03T00:00',
							gyldighetstidspunkt: '2022-10-03T11:57:42',
							kilde: 'Dolly',
						},
						metadata: {
							endringer: [
								{
									kilde: 'Dolly',
									registrert: '2022-10-03T11:57:42',
									registrertAv: 'Folkeregisteret',
									systemkilde: 'FREG',
									type: 'OPPRETT',
								},
							],
							historisk: false,
							master: 'FREG',
							opplysningsId: 'f80d7429-dec4-49af-b933-88d07a143017',
						},
					},
				],
			},
		},
	],
	seed: 1111,
	side: 0,
	totalHits: 1,
}

export const fagsystemTyperMock = [
	{
		type: 'AAREG',
		beskrivelse: 'Arbeidsgiver/arbeidstaker-register (AAREG)',
	},
	{
		type: 'ARBEIDSPLASSENCV',
		beskrivelse: 'Nav CV',
	},
]
