export type BrregTypes = {
	understatuser: Array<number>
	enheter: Array<Enhet>
}

export type Enhet = {
	rolle: string
	registreringsdato: string
	foretaksNavn: {
		navn1: string
	}
	orgNr: string
	personroller: Array<PersonRolle>
}

export type PersonRolle = {
	egenskap: string
	fratraadt: boolean
}
