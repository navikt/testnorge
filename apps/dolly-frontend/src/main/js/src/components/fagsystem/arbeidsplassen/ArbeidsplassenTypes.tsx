export type ArbeidsplassenTypes = {
	jobboensker?: Jobboensker
	utdanning?: Array<Utdanning>
	fagbrev?: Array<Fagbrev>
	arbeidserfaring?: Array<Arbeidserfaring>
	annenErfaring?: Array<AnnenErfaring>
	kompetanser?: Array<Kompetanser>
	offentligeGodkjenninger?: Array<OffentligeGodkjenninger>
	andreGodkjenninger?: Array<AndreGodkjenninger>
	spraak?: Array<Spraak>
	foererkort?: Array<Foererkort>
	kurs?: Array<Kurs>
	sammendrag?: string
}

type Occupation = {
	title: string
	styrk08: string
}

type Location = {
	location: string
	code: string
}

export type Jobboensker = {
	startOption: string | null
	occupations: Array<Occupation>
	locations: Array<Location>
	occupationTypes: Array<string>
	workLoadTypes: Array<string>
	workScheduleTypes: Array<string>
}

export type Utdanning = {
	institution: string
	field: string
	nuskode: string
	startDate: string | null
	endDate: string | null
	description: string
	ongoing: boolean
}

export type Fagbrev = {
	title: string
	type: string
}

export type Arbeidserfaring = {
	employer: string
	jobTitle: string
	alternativeJobTitle: string
	location: string
	description: string
	fromDate: string | null
	toDate: string | null
	ongoing: boolean
	styrkkode: string
}

export type AnnenErfaring = {
	description: string
	role: string
	fromDate: string | null
	toDate: string | null
	ongoing: boolean
}

export type Kompetanser = {
	title: string
}

export type OffentligeGodkjenninger = {
	title: string
	issuer: string
	fromDate: string | null
	toDate: string | null
}

export type AndreGodkjenninger = {
	certificateName: string
	issuer: string
	fromDate: string | null
	toDate: string | null
}

export type Spraak = {
	language: string
	oralProficiency: string
	writtenProficiency: string
}

export type Foererkort = {
	type: string
	acquiredDate: string | null
	expiryDate: string | null
}

export type Kurs = {
	title: string
	issuer: string
	duration: number
	durationUnit: string
	date: string | null
}
