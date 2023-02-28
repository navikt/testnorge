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
	arbeidserfaring: [],
	utdanning: [],
	andreGodkjenninger: [],
	foererkort: [],
	annenErfaring: [],
	kurs: [],
	spraak: [],
	offentligeGodkjenninger: [],
	fagbrev: [],
	kompetanser: [],
	jobboensker: initialJobboensker,
	sammendrag: '',
	harBil: false,
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
	// styrkkode: '', // Stilling/yrke?
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

export const initialFagbrev = {
	// uuid: '',
	title: '',
	// conceptId: '',
	type: '',
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
