export const initialJobboensker = {
	// active: false,
	startOption: null,
	occupations: [],
	// occupationDrafts: [],
	locations: [],
	occupationTypes: [],
	workLoadTypes: [],
	workScheduleTypes: [],
}

export const initialJobboenskerVerdier = {
	// active: false,
	startOption: 'LEDIG_NAA',
	occupations: [
		{
			title: 'Frisør',
			styrk08: '5141',
		},
		{
			title: 'Sauegjeter',
			styrk08: '9212',
		},
		{
			title: 'Typograf',
			styrk08: '7321',
		},
		{
			title: 'Utvikler',
			styrk08: '9999',
		},
	],
	// occupationDrafts: [],
	locations: [
		{
			location: 'Lørenskog',
			code: 'NO02.0230',
		},
		{
			location: 'Oslo',
			code: 'NO03',
		},
		{
			location: 'Fredrikstad',
			code: 'NO01.0106',
		},
	],
	occupationTypes: ['FAST', 'PROSJEKT', 'FERIEJOBB'],
	workLoadTypes: ['HELTID'],
	workScheduleTypes: ['DAGTID', 'UKEDAGER', 'KVELD'],
}

export const initialCV = {
	jobboensker: initialJobboensker,
	utdanning: [],
	fagbrev: [],
	arbeidserfaring: [],
	annenErfaring: [],
	kompetanser: [],
	offentligeGodkjenninger: [],
	andreGodkjenninger: [],
	spraak: [],
	foererkort: [],
	kurs: [],
	sammendrag: '',
	// harBil: false,
}

export const initialArbeidserfaring = {
	employer: '',
	jobTitle: '',
	alternativeJobTitle: '',
	// conceptId: '',
	location: '',
	description: '',
	fromDate: null,
	toDate: null,
	ongoing: false,
	styrkkode: '',
	// ikkeAktueltForFremtiden: false,
}

export const initialArbeidserfaringVerdier = {
	employer: 'Dolly gård',
	jobTitle: 'Sauegjeter',
	alternativeJobTitle: 'Sauepasser',
	// conceptId: '',
	location: 'Sauda',
	description: 'Passe på at sauene har det bra.',
	fromDate: '2016-01-01T00:00:00',
	toDate: '2019-12-31T00:00:00',
	ongoing: false,
	styrkkode: '9212',
	// ikkeAktueltForFremtiden: false,
}

export const initialUtdanning = {
	institution: '',
	field: '',
	nuskode: '',
	// hasAuthorization: false,
	// vocationalCollege: null,
	startDate: null,
	endDate: null,
	description: '',
	ongoing: false,
}

export const initialUtdanningVerdier = {
	institution: 'Dolly Universitet',
	field: 'Bachelor i sauefag',
	nuskode: '6',
	// hasAuthorization: false,
	// vocationalCollege: null,
	startDate: '2012-08-10T00:00:00',
	endDate: '2015-05-10T00:00:00',
	description: 'Studier innen sau og syntetiske data.',
	ongoing: false,
}

export const initialFagbrev = {
	// uuid: '',
	title: '',
	// conceptId: '',
	type: '',
	// updatedAt: null,
}

export const initialFagbrevVerdier = {
	// uuid: '',
	title: 'Svennebrev frisør',
	// conceptId: '',
	type: 'SVENNEBREV_FAGBREV',
	// updatedAt: null,
}

export const initialAnnenErfaring = {
	// uuid: '',
	description: '',
	role: '',
	fromDate: null,
	toDate: null,
	ongoing: false,
	// updatedAt: null
}

export const initialAnnenErfaringVerdier = {
	// uuid: '',
	description: 'Mange års erfaring med trening av sau til utstillinger.',
	role: 'Sauetrener',
	fromDate: '2010-05-01T00:00:00+02:00',
	toDate: null,
	ongoing: true,
	// updatedAt: null
}

export const initialKompetanser = {
	// uuid: '',
	title: '',
	// conceptId: '',
	// updatedAt: null,
}

export const initialKompetanserVerdier = {
	// uuid: '',
	title: 'Fange dyr i feller',
	// conceptId: '',
	// updatedAt: null,
}

export const initialOffentligeGodkjenninger = {
	// uui: '',
	title: '',
	// conceptId: '',
	issuer: '',
	fromDate: null,
	toDate: null,
	// updatedAt: null
}

export const initialOffentligeGodkjenningerVerdier = {
	// uui: '',
	title: 'Godkjenning som profesjonell pyrotekniker',
	// conceptId: '',
	issuer: 'Dolly Pyroakademi',
	fromDate: '2017-12-31T00:00:00',
	toDate: '2027-12-31T00:00:00',
	// updatedAt: null
}

export const initialAndreGodkjenninger = {
	// uuid: '',
	certificateName: '',
	// alternativeName: '',
	// conceptId: '',
	issuer: '',
	fromDate: null,
	toDate: null,
	// updatedAt: null,
}

export const initialAndreGodkjenningerVerdier = {
	// uuid: '',
	certificateName: 'Ballongførerbevis',
	// alternativeName: '',
	// conceptId: '',
	issuer: 'Dolly Luftfart',
	fromDate: '2022-02-02T00:00:00',
	toDate: '2027-02-02T00:00:00',
	// updatedAt: null,
}

export const initialSpraak = {
	// uuid: '',
	language: '',
	oralProficiency: 'IKKE_OPPGITT',
	writtenProficiency: 'IKKE_OPPGITT',
	// updatedAt: null,
}

export const initialSpraakVerdier = {
	// uuid: '',
	language: 'Norsk',
	oralProficiency: 'FOERSTESPRAAK',
	writtenProficiency: 'FOERSTESPRAAK',
	// updatedAt: null,
}

export const initialFoererkort = {
	// uuid: '',
	type: '',
	acquiredDate: null,
	expiryDate: null,
	// updatedAt: null
}

export const initialFoererkortVerdier = {
	// uuid: '',
	type: 'C - Lastebil',
	acquiredDate: '2020-03-01T00:00:00',
	expiryDate: '2028-03-01T00:00:00',
	// updatedAt: null
}

export const initialKurs = {
	// uuid: '',
	title: '',
	issuer: '',
	duration: 0,
	durationUnit: '',
	date: null,
	// updatedAt: null,
}

export const initialKursVerdier = {
	// uuid: '',
	title: 'Kurs i saueklipping',
	issuer: 'Dolly Opplæringssenter',
	duration: 2,
	durationUnit: 'UKE',
	date: '2022-07-30T00:00:00',
	// updatedAt: null,
}

export const initialJobboenskerOmråde = {
	// id: '',
	location: '',
	code: '',
	// conceptId: null,
}

export const initialJobboenskerYrke = {
	// id: '',
	title: '',
	// conceptId: null,
	styrk08: '',
}

export const initialSammendragVerdi =
	'Trivelig person med kjærlighet for sau, som har tonnevis med allsidig kompetanse og fantastiske personlige egenskaper.'
