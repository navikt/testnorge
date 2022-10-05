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

export const krrstubMock = {
	id: 1,
	navn: 'Digipost',
	adresse: '984661185',
	x509Sertifikat: 'SuperSecretSertifikat',
}

export const backendTransaksjonMock = [
	{
		id: 1,
		bestillingId: 1,
		ident: '12345678912',
		system: 'INNTKMELD',
		miljoe: 'q1',
		transaksjonId: {
			journalpostId: '999999999',
			dokumentInfoId: '999999999',
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
			dokumentInfoId: '999999999',
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

export const pensjonMock = {
	miljo: 'q1',
	fnr: '12345678912',
	inntekter: [
		{
			belop: 11843,
			inntektAar: 2018,
		},
		{
			belop: 11532,
			inntektAar: 2017,
		},
		{
			belop: 11341,
			inntektAar: 2016,
		},
		{
			belop: 11065,
			inntektAar: 2015,
		},
		{
			belop: 10796,
			inntektAar: 2014,
		},
		{
			belop: 10410,
			inntektAar: 2013,
		},
		{
			belop: 10033,
			inntektAar: 2012,
		},
	],
}

export const kontoregisterMock = {
	aktivKonto: {
		kontohaver: '12345678912',
		kontonummer: '99999999999',
		gyldigFom: '2022-01-01T11:58:24.030845',
		opprettetAv: 'Dolly',
		kilde: 'testnav-kontoregister-person-proxy-trygdeetaten',
	},
}

export const joarkJournalpostMock = {
	journalpostId: 123123123,
	tittel: 'Syntetisk Inntektsmelding',
	tema: 'Sykepenger',
	avsenderMottaker: {
		type: 'ORGNR',
		id: '947064649',
		navn: 'Dolly Dollesen',
	},
	dokumenter: [
		{
			dokumentInfoId: 231231231,
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
		antallLevert: 1,
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
		bestilling: {},
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
							{
								miljo: 'q2',
								identer: ['01418221999'],
							},
							{
								miljo: 'q4',
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

export const miljoeMock = '["q1","q2","q4","q5","qx","t0","t1","t13","t2","t3","t4","t5","t6","u5"]'
