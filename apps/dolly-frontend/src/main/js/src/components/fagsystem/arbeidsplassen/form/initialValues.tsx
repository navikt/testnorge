export const initialJobboensker = {
	startOption: null,
	occupations: [],
	locations: [],
	occupationTypes: [],
	workLoadTypes: [],
	workScheduleTypes: [],
}

export const initialJobboenskerVerdier = {
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
			title: 'Saueklipper',
			styrk08: '6121',
		},
		{
			title: 'Ullklassifisør',
			styrk08: '7543',
		},
	],
	locations: [
		{
			location: 'Hamar',
			code: 'NO04.0403',
		},
		{
			location: 'Råde',
			code: 'NO30.3017',
		},
		{
			location: 'Vestby',
			code: 'NO02.0211',
		},
	],
	occupationTypes: ['FAST', 'PROSJEKT', 'FERIEJOBB'],
	workLoadTypes: ['HELTID'],
	workScheduleTypes: ['DAGTID', 'UKEDAGER', 'KVELD'],
}

export const initialArbeidserfaring = {
	employer: '',
	jobTitle: '',
	alternativeJobTitle: '',
	location: '',
	description: '',
	fromDate: null,
	toDate: null,
	ongoing: false,
	styrkkode: '',
}

export const initialArbeidserfaringVerdier = {
	employer: 'Dolly gård',
	jobTitle: 'Sauegjeter',
	alternativeJobTitle: 'Sauepasser',
	location: 'Sauda',
	description: 'Passe på at sauene har det bra.',
	fromDate: '2016-01-01T00:00:00',
	toDate: '2019-12-31T00:00:00',
	ongoing: false,
	styrkkode: '9212',
}

export const initialUtdanning = {
	institution: '',
	field: '',
	nuskode: '',
	startDate: null,
	endDate: null,
	description: '',
	ongoing: false,
}

export const initialUtdanningVerdier = {
	institution: 'Dolly Universitet',
	field: 'Bachelor i sauefag',
	nuskode: '6',
	startDate: '2012-08-10T00:00:00',
	endDate: '2015-05-10T00:00:00',
	description: 'Studier innen sau og syntetiske data.',
	ongoing: false,
}

export const initialFagbrev = {
	title: '',
	type: '',
}

export const initialFagbrevVerdier = {
	title: 'Svennebrev frisør',
	type: 'SVENNEBREV_FAGBREV',
}

export const initialAnnenErfaring = {
	description: '',
	role: '',
	fromDate: null,
	toDate: null,
	ongoing: false,
}

export const initialAnnenErfaringVerdier = {
	description: 'Mange års erfaring med trening av sau til utstillinger.',
	role: 'Sauetrener',
	fromDate: '2010-05-01T00:00:00',
	toDate: null,
	ongoing: true,
}

export const initialKompetanser = {
	title: '',
}

export const initialKompetanserVerdier = {
	title: 'Fange dyr i feller',
}

export const initialOffentligeGodkjenninger = {
	title: '',
	issuer: '',
	fromDate: null,
	toDate: null,
}

export const initialOffentligeGodkjenningerVerdier = {
	title: 'Godkjenning som profesjonell pyrotekniker',
	issuer: 'Dolly Pyroakademi',
	fromDate: '2017-12-31T00:00:00',
	toDate: '2027-12-31T00:00:00',
}

export const initialAndreGodkjenninger = {
	certificateName: '',
	issuer: '',
	fromDate: null,
	toDate: null,
}

export const initialAndreGodkjenningerVerdier = {
	certificateName: 'Ballongførerbevis',
	issuer: 'Dolly Luftfart',
	fromDate: '2022-02-02T00:00:00',
	toDate: '2027-02-02T00:00:00',
}

export const initialSpraak = {
	language: '',
	oralProficiency: 'IKKE_OPPGITT',
	writtenProficiency: 'IKKE_OPPGITT',
}

export const initialSpraakVerdier = {
	language: 'Norsk',
	oralProficiency: 'FOERSTESPRAAK',
	writtenProficiency: 'FOERSTESPRAAK',
}

export const initialFoererkort = {
	type: '',
	acquiredDate: null,
	expiryDate: null,
}

export const initialFoererkortVerdier = {
	type: 'C - Lastebil',
	acquiredDate: '2020-03-01T00:00:00',
	expiryDate: '2028-03-01T00:00:00',
}

export const initialKurs = {
	title: '',
	issuer: '',
	duration: 0,
	durationUnit: '',
	date: null,
}

export const initialKursVerdier = {
	title: 'Kurs i saueklipping',
	issuer: 'Dolly Opplæringssenter',
	duration: 2,
	durationUnit: 'UKE',
	date: '2022-07-30T00:00:00',
}

export const initialSammendragVerdi =
	'Trivelig person med kjærlighet for sau, som har tonnevis med allsidig kompetanse og fantastiske personlige egenskaper.'
