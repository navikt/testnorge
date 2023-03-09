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

export const initialSpraak = {
	// uuid: '',
	language: '',
	oralProficiency: 'IKKE_OPPGITT',
	writtenProficiency: 'IKKE_OPPGITT',
	// updatedAt: null,
}

export const initialFoererkort = {
	// uuid: '',
	type: '',
	acquiredDate: null,
	expiryDate: null,
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
